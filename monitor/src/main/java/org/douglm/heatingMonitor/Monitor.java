/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.response.GetEntityResponse;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import org.douglm.heatingMonitor.common.MonitorException;
import org.douglm.heatingMonitor.common.MonitorWebServiceClient;
import org.douglm.heatingMonitor.common.config.MonitorConfig;
import org.douglm.heatingMonitor.common.status.HeatSource;
import org.douglm.heatingMonitor.common.status.Input;
import org.douglm.heatingMonitor.common.status.MonitorStatus;
import org.douglm.heatingMonitor.common.status.Zone;
import org.douglm.heatingMonitor.config.HardwareConfig;

import static org.douglm.heatingMonitor.common.util.MonitorUtil.readConfig;

/**
 * User: mike Date: 3/13/25 Time: 14:30
 */
public class Monitor implements Logged {
  private final Context pi4j;
  private final HardwareConfig hardwareConfig;
  private final long startTime =  System.currentTimeMillis();

  private MonitorConfig monitorConfig;
  private MonitorStatus status;

  private final MonitorWebServiceClient webClient;

  public Monitor() {
    /* Configurations are read from config files.
       Eventually from web service
     */
    final var hcresp = readConfig("hardware.yaml",
                                  HardwareConfig.class);
    if (!hcresp.isOk()) {
      throw new MonitorException(hcresp.getMessage());
    }

    hardwareConfig = hcresp.getEntity();
    validateHardwareConfig();
    debug(hardwareConfig.toString());
    pi4j = Pi4J.newAutoContext();
    webClient = new MonitorWebServiceClient(
            hardwareConfig.getServerUrl());
  }

  public void validateHardwareConfig() {
    if (hardwareConfig.getServerUrl() == null) {
      throw new MonitorException("Hardware cnfig must sepcify server url");
    }
  }

  public Monitor init() {
    final var webRes = webClient.readConfig();
    if (!webRes.isOk()) {
      if (webRes.getException() != null) {
        throw new MonitorException(webRes.getException());
      }

      throw new MonitorException(webRes.getMessage());
    }
    monitorConfig = webRes.getEntity();
    debug(monitorConfig.toString());

    final var resp = fromConfig();
    if (!resp.isOk()) {
      throw new MonitorException(resp.getMessage());
    }
    status = resp.getEntity();

    return this;
  }

  public void monitorBoards() {
    final var inputs = new InputsThread(pi4j, status,
                                        hardwareConfig);
    inputs.start();

    final var monitor = new MonitorThread(status,
                                          webClient);
    monitor.start();

    try {
      inputs.join();
    } catch (final InterruptedException ignored) {
    }
  }

  public MonitorStatus getStatus() {
    return status;
  }

  public MonitorWebServiceClient getWebClient() {
    return webClient;
  }

  private GetEntityResponse<MonitorStatus> fromConfig() {
    final var status = new MonitorStatus(monitorConfig);
    final var resp = new GetEntityResponse<MonitorStatus>();

    status.setStartTime(startTime);

    for (final var hs: monitorConfig.getHeatSources()) {
      final var heatSource = new HeatSource(hs);
      status.addHeatSource(heatSource);

      for (final var zc: hs.getZones()) {
        if (zc.getName() == null) {
          return resp.invalid("Zone name is null: " + zc);
        }

        if (heatSource.addZone(new Zone(zc)) != null) {
          return resp.invalid("Zone " + zc.getName() +
                                      " already exists: " + zc);
        }
      }

      for (final var db: hardwareConfig.getDigitalBoards()) {
        for (final var ic: db.getInputs()) {
          if (ic.getName() == null) {
            return resp.invalid("Input name is null: " + ic);
          }

          // Input may not have a zone

          Input input = null;
          final Zone zone;

          if (ic.getZone() == null) {
            // Maybe other checks?
            zone = null;
          } else {
            zone = heatSource.getZone(ic.getZone());
            if (zone == null) {
              return resp.invalid("Zone " + ic.getZone() +
                                          " does not exist: " +
                                          ic);
            }
          }

          if (zone == null) {
            input = new Input(ic.getName());
            status.addSensor(input);
          } else if (ic.isCirculator()) {
            input = zone;
          } else if (ic.isSubZone()) {
            input = zone.getSubZone(ic.getName());
            if (input == null) {
              return resp.invalid("No subzone " +
                                          ic.getName() +
                                          " for zone " +
                                          zone.getName());
            }
          } else {
            return resp.invalid("No subzone specified for " +
                                        ic.getName());
          }

          if (status.addInput(input) != null) {
            return resp.invalid("Input " + ic.getName() +
                                        " already exists: " +
                                        ic);
          }

        }
      }
    }

    /*
    for (final var zone: status.getZones()) {
      if (zone.getCirculator() == null) {
        return resp.invalid("Circulator is null: " + zone);
      }
    }*/

    resp.setEntity(status);

    return resp;
  }

  /* ====================================================
   *                   Logged methods
   * ==================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }

  public static void main(final String[] args) {
    final var m = new Monitor().init();

    m.monitorBoards();
//    m.testDigital();
  }
}
