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
import org.douglm.piSpi.PiSpi8AIChannelConfig.Mode;
import org.douglm.piSpi.PiSpi8AIPlus;
import org.douglm.piSpi.PiSpi8DI;

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
    pi4j = Pi4J.newAutoContext();
  }

  /*
  public void testDigital() {
    // Find config for chip address 1
    DigitalBoardConfig digitalConfig = null;
    for (final var digitalBoard : config.getDigitalBoards()) {
      if (digitalBoard.getHardwareAddress() == 1) {
        digitalConfig = digitalBoard;
        break;
      }
    }

    // Find the always on input
    DigitalInputConfig inp = null;
    for (final var input: digitalConfig.getInputs()) {
      if (input.isAlwaysOn()) {
        inp = input;
        break;
      }
    }

    final var db = new PiSpi8DI(pi4j, digitalConfig);

    boolean lastState = false;
    var lastTransition = System.currentTimeMillis();
    final var transition = "----------";
    final var onLine = "         |";
    final var offLine = "|         ";
    while (true) {
      final var states = statesDelay(db);
      final var state = states[inp.getIndex()];
      if (lastState != state) {
        final var newTransition = System.currentTimeMillis();
        if (!state) {
          info(transition + " " + (newTransition - lastTransition));
          lastTransition = newTransition;
        }
        lastState = state;
     // } else if (state) {
     //   info(onLine);
     // } else  {
     //   info(offLine);
      }
    }
  }
   */

  public void monitorBoards() {
    int alwaysOnErrors = 0;
    final var analogBoard = config.getAnalogBoard();
    try (final var aToD = new PiSpi8AIPlus(pi4j, analogBoard);
         final var digitalBoards = new DigitalBoards(pi4j,
                                                     config.getDigitalBoards())) {
      while (true) {
        for (final var analogChannel: analogBoard.getChannels()) {
          if (analogChannel.getMode() == Mode.thermistor) {
            final var val = aToD.getTemperature(analogChannel.getChannel());
            final double f = (val * 9 / 5) + 32;
            final double out;
            if (config.isCentigrade()) {
              out = val;
            } else {
              out = f;
            }
            System.out.println(analogChannel.getName() + ": " + out);
          }
        }

        for (final var digitalBoard: digitalBoards.getDigitalBoards()) {
          final var db = digitalBoard.digitalBoard();
          /* My belief is the board is seeing the low voltage transitions
             from the ac input. Try multiple reads and OR the results.
           */
          final var states = or(db.states(),
                                or(statesDelay(db),
                                   or(statesDelay(db), statesDelay(db))));
          for (final var input: digitalBoard.digitalBoardConfig().getInputs()) {
            info(input.getName() + ": " +
                         states[input.getIndex()]);
            if (input.isAlwaysOn() && !states[input.getIndex()]) {
              alwaysOnErrors++;
              warn("Expected on for "  + input.getName());
            }
          }
        }

        if (alwaysOnErrors > 0) {
          info("Always on errors: " + alwaysOnErrors);
        }

        try {
          Thread.sleep(config.getWaitTime());
        } catch (final InterruptedException ignored) {
          break;
        }
      }
    }
  }

  private boolean[] or(final boolean[] b1,
                       final boolean[] b2) {
    if (b1 == null) {
      return b2;
    }

    final var out = new boolean[b1.length];
    for (int i = 0; i < b1.length; i++) {
      out[i] = b1[i] || b2[i];
    }

    return out;
  }

  private boolean[] statesDelay(final PiSpi8DI db) {
    try {
      Thread.sleep(1);
    } catch (final InterruptedException e) {
      throw new RuntimeException(e);
    }
    return db.states();
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
