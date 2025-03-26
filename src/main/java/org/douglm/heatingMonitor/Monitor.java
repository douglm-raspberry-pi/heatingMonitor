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
import org.douglm.piSpi.PiSpi8AIPlus;

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

    m.monitorTemp();
  }

  private final Context pi4j;
  private final MonitorConfig config;

  public Monitor() throws ProviderException {
    final var classLoader = Thread.currentThread().getContextClassLoader();
    final var file = new File(classLoader.getResource("monitor.yaml").getFile());
    try {
      config = mapper.readValue(file, MonitorConfig.class);
    } catch (final IOException ioe) {
      error(ioe);
      throw new RuntimeException(ioe);
    }
    info(config.toString());
    pi4j = Pi4J.newAutoContext();
  }

  public void monitorTemp() {
    final var analogBoard = config.getAnalogBoard();
    try (final var aToD = new PiSpi8AIPlus(analogBoard, pi4j,
                                           analogBoard.getSpiAddress())) {
      while (true) {
        for (final var analogChannel: analogBoard.getChannels()) {
          if (analogChannel.getMode() == AnalogChannelConfig.Mode.thermistor) {
            final var val = aToD.getTemperature(analogChannel.getChannel());
            System.out.println(analogChannel.getName() + ": " + val);
          }
        }
        try {
          Thread.sleep(500);
        } catch (final InterruptedException ignored) {
          break;
        }
      }
    }
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
