/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.response.GetEntityResponse;
import org.bedework.util.http.Headers;
import org.bedework.util.http.HttpUtil;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.douglm.heatingMonitor.common.MonitorException;
import org.douglm.heatingMonitor.common.config.MonitorConfig;
import org.douglm.heatingMonitor.common.status.Input;
import org.douglm.heatingMonitor.common.status.MonitorStatus;
import org.douglm.heatingMonitor.common.status.Zone;
import org.douglm.heatingMonitor.config.HardwareConfig;

import java.net.URI;

import static org.douglm.heatingMonitor.common.util.MonitorUtil.readConfig;

/**
 * User: mike Date: 3/13/25 Time: 14:30
 */
public class Monitor implements Logged {
  // TODO - this needs to be somewhere it gets shut down properly
  private static CloseableHttpClient http;

  private final Context pi4j;
  private final HardwareConfig hardwareConfig;

  private MonitorConfig monitorConfig;
  private MonitorStatus status;

  private ObjectMapper objectMapper;

  private static final Headers defaultHeaders;

  static {
    defaultHeaders = new Headers();
    defaultHeaders.add("Accept", "application/json");
  }

  public Monitor() {
    /* Configurations are read from config files.
       Eventually from web service
     */
    hardwareConfig = readConfig("hardware.yaml", HardwareConfig.class);

    debug(hardwareConfig.toString());
    pi4j = Pi4J.newAutoContext();
  }

  public Monitor init() {
    // Read monitor config.
    try {
      final CloseableHttpClient cl = getClient();

      try (final CloseableHttpResponse hresp =
                   HttpUtil.doGet(cl,
                                  new URI(hardwareConfig.getServerUrl()),
                                  this::getDefaultHeaders,
                                  null)) {   // content type
        final int status = HttpUtil.getStatus(hresp);

        if ((status / 100) != 2) {
          throw new MonitorException("Unable to fetch monitor configuration");
        }

        monitorConfig = getMapper()
                .readValue(hresp.getEntity().getContent(),
                           MonitorConfig.class);
        debug(monitorConfig.toString());
      }

      final var resp = validateConfig();
      if (!resp.isOk()) {
        throw new MonitorException(resp.getMessage());
      }
      status = resp.getEntity();
    } catch(final Throwable t) {
      if (t instanceof MonitorException) {
        throw (MonitorException)t;
      }
      throw new MonitorException(t);
    }

    return this;
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

  private CloseableHttpClient getClient() {
    if (http == null) {
      try {
        http  = HttpClients.createDefault();
      } catch (final Throwable t) {
        error(t);
        throw new MonitorException(t);
      }
    }

    return http;
  }

  private ObjectMapper getMapper() {
    if (objectMapper != null) {
      return objectMapper;
    }

    objectMapper = new ObjectMapper();

    return objectMapper;
  }

  private Headers getDefaultHeaders() {
    return defaultHeaders;
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

  public static void main(final String[] args) {
    final var m = new Monitor().init();

    m.monitorBoards();
//    m.testDigital();
  }
}
