/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.config;

import org.bedework.base.ToString;

import java.util.List;

/**
 * User: mike Date: 3/19/25 Time: 22:24
 */
public class HardwareConfig {
  private String serverUrl;
  private AnalogBoardConfig analogBoard;
  private List<DigitalBoardConfig> digitalBoards;

  public String getServerUrl() {
    return serverUrl;
  }

  public void setServerUrl(final String val) {
    serverUrl = val;
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

  public ToString toStringSegment(final ToString ts) {
    ts.append("analogBoard", analogBoard)
      .append("digitalBoards", digitalBoards);

    return ts;
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
