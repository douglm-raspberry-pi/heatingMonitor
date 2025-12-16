/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.util.misc.AbstractProcessorThread;

import org.douglm.heatingMonitor.common.config.MonitorConfig;
import org.douglm.heatingMonitor.common.status.Input;
import org.douglm.heatingMonitor.common.status.MonitorStatus;
import org.douglm.heatingMonitor.common.status.Zone;

/**
 * User: mike Date: 4/6/25 Time: 13:08
 */
public class MonitorThread extends AbstractProcessorThread {
  private final MonitorStatus status;
  private final MonitorConfig config;

  /**
   * @param status for the monitor
   */
  public MonitorThread(final MonitorStatus status) {
    super("Monitor");

    this.status = status;
    this.config = status.getConfig();
  }

  @Override
  public void runInit() {
    setRunning(Boolean.TRUE);
  }

  @Override
  public void end(final String msg) {

  }

  @Override
  public void runProcess() throws Throwable {
    while (getRunning()) {

      synchronized (this) {
        if (status.getAlwaysOnErrors() > 0) {
          info("Always on errors: " + status.getAlwaysOnErrors());
        }

        for (final var input: status.getInputs()) {
          if (input.testAndSetChanged()) {
            final var zone = input.getZone();
            if (zone != null) {
              zone.inputChanged();
            }

            debug("Input " + input.getName() +
                    " changed to " + input.getLastStatus());
          }
        }

        // See if any zones need checking
        for (final var zone: status.getZones()) {
          if (zone.getInputChanged()) {
            updateZone(zone);
          }
        }

        try {
          this.wait(config.getInputsWaitTime());
        } catch (final InterruptedException ignored) {
          setRunning(false);
          break;
        }
      }
    }
  }

  @Override
  public void close() {

  }

  private void updateZone(final Zone zone) {
    // Check the inputs for this zone
    final var circulator = zone.getCirculator();
    final var inputs = zone.getInputs();

    /* If any input is on we expect the circulator to be on.
       There may be a delay from the input being true to the
       circulator actually coming on.

       Zone valves have to rotate to the end of their travel to turn
       the zone on.

       We allow a single mismatch between input states and actual
       circulator state.
     */

    final var currentState = zone.getCirculator().getLastStatus();

    var expectOn = false;
    if (inputs.isEmpty()) {
      expectOn = currentState;
    } else {
      for (final Input input: inputs) {
        if (input.getLastStatus()) {
          expectOn = true;
          break;
        }
      }
    }

    if (expectOn == currentState) {
      if (expectOn != zone.isCirculatorOn()) {
        // Update the zone if necessary
        zone.setWasChecked(false);
        zone.setCirculatorOn(currentState);
        if (!currentState) {
          // zone turned off - update amount of time on
          zone.updateRunningTime();

          debug("zone {}, running time {} minutes, {}%",
                zone.getConfig().getName(),
                zone.getRunningTimeMinutes(),
                zone.getRunningTimePercent(status.getStartTime()));
        } else {
          zone.setLastChange(System.currentTimeMillis());
        }
      }
    } else if (zone.wasChecked()) {
        warn("Mismatch for {}: current: {}, expected: {}",
             zone.getConfig().getName(),
             currentState, expectOn);
    } else {
      zone.setWasChecked(true);
    }
  }
}
