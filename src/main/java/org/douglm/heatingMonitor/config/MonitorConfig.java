/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.config;

import org.bedework.base.ToString;

import java.util.List;

/**
 * User: mike Date: 3/19/25 Time: 22:24
 */
public class MonitorConfig {
  private long inputsWaitTime;
  private long monitorWaitTime;
  private boolean centigrade;
  private AnalogBoardConfig analogBoard;
  private List<DigitalBoardConfig> digitalBoards;
  private List<ZoneConfig> zones;

  /**
   *
   * @return time in milliseconds inputs should wait. Should be
   *          shorter than the monitor wait time.
   */
  public long getInputsWaitTime() {
    return inputsWaitTime;
  }

  public void setInputsWaitTime(final long val) {
    inputsWaitTime = val;
  }

  /**
   *
   * @return time in milliseconds monitor should wait. Should be
   *          longer than the inputs wait time.
   */
  public long getMonitorWaitTime() {
    return monitorWaitTime;
  }

  public void setMonitorWaitTime(final long val) {
    monitorWaitTime = val;
  }

  public boolean isCentigrade() {
    return centigrade;
  }

  public void setCentigrade(final boolean val) {
    centigrade = val;
  }

  public AnalogBoardConfig getAnalogBoard() {
    return analogBoard;
  }

  public void setAnalogBoard(final AnalogBoardConfig val) {
    analogBoard = val;
  }

  public List<DigitalBoardConfig> getDigitalBoards() {
    return digitalBoards;
  }

  public void setDigitalBoards(
          final List<DigitalBoardConfig> val) {
    digitalBoards = val;
  }

  public List<ZoneConfig> getZones() {
    return zones;
  }

  public void setZones(final List<ZoneConfig> val) {
    zones = val;
  }

  public ToString toStringSegment(final ToString ts) {
    ts.append("waitTime", inputsWaitTime)
      .append("analogBoard", analogBoard)
      .append("digitalBoards", digitalBoards)
      .append("zones", zones);

    return ts;
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
