/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.ToString;

import java.util.List;

/** provides information about a zone - consists of a number of
 * sub-zones, controlled by valves and a circulator.
 *
 * User: mike Date: 4/5/25 Time: 22:45
 */
public class Zone {
  private ZoneConfig config;
  private long lastChange;
  private long runningTime;
  private DigitalInputConfig circulator;
  private boolean circulatorExpected;
  private List<DigitalInputConfig> valves;

  public ZoneConfig getConfig() {
    return config;
  }

  public void setConfig(final ZoneConfig val) {
    config = val;
  }

  public long getLastChange() {
    return lastChange;
  }

  public void setLastChange(final long val) {
    lastChange = val;
  }

  public long getRunningTime() {
    return runningTime;
  }

  public void setRunningTime(final long val) {
    runningTime = val;
  }

  /**
   *
   * @return input which monitors the actual circulator state
   */
  public DigitalInputConfig getCirculator() {
    return circulator;
  }

  /**
   *
   * @return true if circulator is expected to be running
   */
  public boolean isCirculatorExpected() {
    return circulatorExpected;
  }

  public void setCirculatorExpected(final boolean val) {
    circulatorExpected = val;
  }

  public void setCirculator(final DigitalInputConfig val) {
    circulator = val;
  }

  public List<DigitalInputConfig> getValves() {
    return valves;
  }

  public void setValves(final List<DigitalInputConfig> val) {
    valves = val;
  }

  /** Called whenever a valve changes state - this will update
   * circulatorExpected.
   *
   */
  public void valveChanged() {

  }

  public ToString toStringSegment(final ToString ts) {
    return ts.append("config", config)
             .append("lastChange", lastChange)
             .append("runningTime", runningTime)
             .append("circulator", circulator)
             .append("valves", valves);
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
