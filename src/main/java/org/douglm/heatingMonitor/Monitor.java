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
    final var inputs = new InputsThread(pi4j, config);
    inputs.start();
    try {
      inputs.join();
    } catch (final InterruptedException ignored) {
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
