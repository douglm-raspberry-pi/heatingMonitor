/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.util.misc.AbstractProcessorThread;

import com.pi4j.context.Context;
import org.douglm.piSpi.PiSpi8AIChannelConfig;
import org.douglm.piSpi.PiSpi8AIPlus;

/**
 * User: mike Date: 4/5/25 Time: 23:19
 */
public class InputsThread extends AbstractProcessorThread {
  private final Context pi4j;
  private final MonitorConfig config;
  private int alwaysOnErrors;

  /**
   * @param config for the monitor
   */
  public InputsThread(final Context pi4j,
                      final MonitorConfig config) {
    super("Inputs");

    this.pi4j = pi4j;
    this.config = config;
  }

  @Override
  public void runInit() {

  }

  @Override
  public void end(final String msg) {

  }

  @Override
  public void runProcess() throws Throwable {
    final var analogBoard = config.getAnalogBoard();
    try (final var aToD = new PiSpi8AIPlus(pi4j, analogBoard);
         final var digitalBoards = new DigitalBoards(pi4j,
                                                     config.getDigitalBoards())) {
      while (true) {
        for (final var analogChannel: analogBoard.getChannels()) {
          if (analogChannel.getMode() == PiSpi8AIChannelConfig.Mode.thermistor) {
            final var val = aToD.getTemperature(
                    analogChannel.getChannel());
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
          final var states = db.states();

          for (final var input: digitalBoard.digitalBoardConfig()
                                            .getInputs()) {
            info(input.getName() + ": " +
                         states[input.getIndex()]);
            if (input.isAlwaysOn() && !states[input.getIndex()]) {
              alwaysOnErrors++;
              warn("Expected on for " + input.getName());
            }
          }
        }

        if (alwaysOnErrors > 0) {
          info("Always on errors: " + alwaysOnErrors);
        }

        synchronized (this) {
          try {
            this.wait(config.getWaitTime());
          } catch (final InterruptedException ignored) {
            break;
          }
        }
      }
    }
  }

  @Override
  public void close() {

  }
}
