/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import org.douglm.heatingMonitor.config.MonitorConfig;
import org.douglm.heatingMonitor.status.Input;
import org.douglm.heatingMonitor.status.MonitorStatus;
import org.douglm.heatingMonitor.status.Zone;

import java.io.File;
import java.io.IOException;
import java.security.ProviderException;

/**
 * User: mike Date: 3/13/25 Time: 14:30
 */
public class Monitor implements Logged {
  final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  public static void main(final String[] args) {
    final var m = new Monitor();

    m.monitorBoards();
//    m.testDigital();
  }

  private final Context pi4j;
  private final MonitorConfig config;
  private final MonitorStatus status;

  public Monitor() throws ProviderException {
    final var classLoader = Thread.currentThread().getContextClassLoader();
    final var file = new File(classLoader.getResource("monitor.yaml").getFile());
    try {
      config = mapper.readValue(file, MonitorConfig.class);
    } catch (final IOException ioe) {
      error(ioe);
      throw new RuntimeException(ioe);
    }

    debug(config.toString());
    status = validateConfig();
    pi4j = Pi4J.newAutoContext();
  }

  public void monitorBoards() {
    final var inputs = new InputsThread(pi4j, status);
    inputs.start();

    final var monitor = new MonitorThread(status);
    monitor.start();

    try {
      inputs.join();
    } catch (final InterruptedException ignored) {
    }
  }

  private MonitorStatus validateConfig() {
    final var status = new MonitorStatus(config);

    for (final var zc: config.getZones()) {
      if (zc.getName() == null) {
        throw new MonitorException("Zone name is null: " + zc);
      }

      if (status.addZone(new Zone(zc)) != null) {
        throw new MonitorException("Zone " + zc.getName() +
                                           " already exists: " + zc);
      }
    }

    for (final var db: config.getDigitalBoards()) {
      for (final var ic: db.getInputs()) {
        if (ic.getName() == null) {
          throw new MonitorException("Input name is null: " + ic);
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
            throw new MonitorException("Zone " + ic.getZone() +
                                               " does not exist: " +
                                               ic);
          }
        }

        input = new Input(ic, zone);

        if (status.addInput(input) != null) {
          throw new MonitorException("Input " + ic.getName() +
                                             " already exists: " +
                                             ic);
        }

        if (zone != null) {
          if (input.getInputConfig().isCirculator()) {
            zone.setCirculator(input);
          } else {
            zone.addInput(input);
          }
        }
      }
    }

    for (final var zone: status.getZones()) {
      if (zone.getCirculator() == null) {
        throw new MonitorException("Circulator is null: " + zone);
      }
    }

    return status;
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
