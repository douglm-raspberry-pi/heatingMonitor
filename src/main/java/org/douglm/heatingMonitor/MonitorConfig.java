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
  private AnalogBoardConfig analogBoard;
  private List<DigitalBoardConfig> digitalBoards;

  public AnalogBoardConfig getAnalogBoard() {
    return analogBoard;
  }

  public void setAnalogBoard(
          final AnalogBoardConfig val) {
    analogBoard = val;
  }

  public List<DigitalBoardConfig> getDigitalBoards() {
    return digitalBoards;
  }

  public void setDigitalBoards(
          final List<DigitalBoardConfig> val) {
    digitalBoards = val;
  }

  public String toString() {
    return new ToString(this)
            .append("analogBoard", analogBoard)
            .append("digitalBoards", digitalBoards)
            .toString();
  }
}
