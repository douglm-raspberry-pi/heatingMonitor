/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.response.GetEntityResponse;
import org.bedework.util.http.PooledHttpClient;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import org.douglm.heatingMonitor.common.MonitorException;
import org.douglm.heatingMonitor.common.config.MonitorConfig;
import org.douglm.heatingMonitor.common.status.Input;
import org.douglm.heatingMonitor.common.status.MonitorStatus;
import org.douglm.heatingMonitor.common.status.Zone;
import org.douglm.heatingMonitor.config.HardwareConfig;

import java.security.ProviderException;

import static org.douglm.heatingMonitor.common.util.MonitorUtil.readConfig;

/**
 * User: mike Date: 3/13/25 Time: 14:30
 */
public class Monitor implements Logged {
  // TODO - this needs to be somewhere it gets shut down properly
  private static PooledHttpClient http;

  public static void main(final String[] args) {
    final var m = new Monitor();

    m.monitorBoards();
//    m.testDigital();
  }

  private final Context pi4j;
  private final MonitorConfig monitorConfig;
  private final HardwareConfig hardwareConfig;
  private final MonitorStatus status;

  public Monitor() throws ProviderException {
    /* Configurations are read from config files.
       Eventually from web service
     */
    monitorConfig = readConfig("monitor.yaml", MonitorConfig.class);
    hardwareConfig = readConfig("hardware.yaml", HardwareConfig.class);

    debug(monitorConfig.toString());
    debug(hardwareConfig.toString());
    final var resp = validateConfig();
    if (!resp.isOk()) {
      throw new MonitorException(resp.getMessage());
    }
    status = resp.getEntity();
    pi4j = Pi4J.newAutoContext();
  }

  public void monitorBoards() {
    final var inputs = new InputsThread(pi4j, status, hardwareConfig);
    inputs.start();

    final var monitor = new MonitorThread(status);
    monitor.start();

    try {
      inputs.join();
    } catch (final InterruptedException ignored) {
    }
  }

  private GetEntityResponse<MonitorStatus> validateConfig() {
    final var status = new MonitorStatus(monitorConfig);
    final var resp = new GetEntityResponse<MonitorStatus>();

    for (final var zc: monitorConfig.getZones()) {
      if (zc.getName() == null) {
        return resp.invalid("Zone name is null: " + zc);
      }

      if (status.addZone(new Zone(zc)) != null) {
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

        final Input input;
        final Zone zone;

        if (ic.getZone() == null) {
          // Maybe other checks?
          zone = null;
        } else {
          zone = status.getZone(ic.getZone());
          if (zone == null) {
            return resp.invalid("Zone " + ic.getZone() +
                                        " does not exist: " +
                                        ic);
          }
        }

        input = new Input(ic.getName(), zone);

        if (status.addInput(input) != null) {
          return resp.invalid("Input " + ic.getName() +
                                      " already exists: " +
                                      ic);
        }

        if (zone != null) {
          if (ic.isCirculator()) {
            zone.setCirculator(input);
          } else {
            zone.addInput(input);
          }
        }
      }
    }

    for (final var zone: status.getZones()) {
      if (zone.getCirculator() == null) {
        return resp.invalid("Circulator is null: " + zone);
      }
    }

    resp.setEntity(status);

    return resp;
  }

  /* ==============================================================
   *                   Logged methods
   * ============================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
