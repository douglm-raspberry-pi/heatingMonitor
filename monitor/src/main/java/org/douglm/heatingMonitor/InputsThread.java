/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.util.misc.AbstractProcessorThread;

import com.pi4j.context.Context;
import org.douglm.heatingMonitor.common.MonitorWebServiceClient;
import org.douglm.heatingMonitor.common.status.MonitorStatus;
import org.douglm.heatingMonitor.config.HardwareConfig;
import org.douglm.piSpi.PiSpi8AIChannelConfig;
import org.douglm.piSpi.PiSpi8AIPlus;

/**
 * User: mike Date: 4/5/25 Time: 23:19
 */
public class InputsThread extends AbstractProcessorThread {
  private final Context pi4j;
  private final MonitorStatus status;
  private final HardwareConfig config;
  private final MonitorWebServiceClient webClient;

  /**
   * @param pi4j context
   * @param status for the monitor
   */
  public InputsThread(final Context pi4j,
                      final MonitorStatus status,
                      final HardwareConfig config,
                      final MonitorWebServiceClient webClient) {
    super("Inputs");

    this.pi4j = pi4j;
    this.status = status;
    this.config = config;
    this.webClient = webClient;
  }

  @Override
  public void runInit() {
    setRunning(Boolean.TRUE);
  }

  @Override
  public void end(final String msg) {
    setRunning(false);
  }

  @Override
  public void runProcess() {
    final var analogBoard = config.getAnalogBoard();
    try (final var aToD = new PiSpi8AIPlus(pi4j, analogBoard);
         final var digitalBoards = new DigitalBoards(pi4j,
                                                     config.getDigitalBoards())) {
      while (getRunning()) {
        for (final var analogChannel: analogBoard.getChannels()) {
          if (analogChannel.getMode() == PiSpi8AIChannelConfig.Mode.thermistor) {
            final var val = aToD.getTemperature(
                    analogChannel.getChannel());

            final var temp = status.findTemp(
                    analogChannel.getName());
            if (temp != null) {
              temp.setDegreesCelsius(val);
            }
          }
        }

        for (final var digitalBoard: digitalBoards.getDigitalBoards()) {
          final var db = digitalBoard.digitalBoard();
          final var states = db.states();

          for (final var ic: digitalBoard.digitalBoardConfig()
                                            .getInputs()) {
            final var inputName = ic.getName();
            final var istatus = states[ic.getIndex()];
            final var input = status.getInput(inputName);

            info(inputName + ": " +  istatus);
            input.currentStatus(istatus);

            if (ic.isAlwaysOn() && !istatus) {
              status.incAlwaysOnErrors();
              warn("Expected on for " + inputName);
            }
          }
        }

        final var postRes = webClient.postStatus(status);
        if (!postRes.isOk()) {
          warn(postRes.toString());
        }

        synchronized (this) {
          try {
            this.wait(status.getConfig().getInputsWaitTime());
          } catch (final InterruptedException ignored) {
            setRunning(false);
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
