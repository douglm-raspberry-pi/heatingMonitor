/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.ToString;

import java.util.List;

/**
 * User: mike Date: 3/19/25 Time: 22:24
 */
public class MonitorConfig {
  private long waitTime;
  private boolean centigrade;
  private AnalogBoardConfig analogBoard;
  private List<DigitalBoardConfig> digitalBoards;
  private List<CirculatorConfig> circulators;

  public long getWaitTime() {
    return waitTime;
  }

  public void setWaitTime(final long val) {
    waitTime = val;
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

  public List<CirculatorConfig> getCirculators() {
    return circulators;
  }

  public void setCirculators(final List<CirculatorConfig> val) {
    circulators = val;
  }

  public ToString toStringSegment(final ToString ts) {
    ts.append("waitTime", waitTime)
      .append("analogBoard", analogBoard)
      .append("digitalBoards", digitalBoards)
      .append("circulators", circulators);

    return ts;
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
