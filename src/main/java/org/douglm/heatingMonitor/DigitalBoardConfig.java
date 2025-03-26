/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.ToString;

import org.douglm.piSpi.PiSpi8DIConfig;

/**
 * User: mike Date: 3/19/25 Time: 22:37
 */
public class DigitalBoardConfig
        extends PiSpi8DIConfig<DigitalInputConfig> {
  private String circulator;

  public String getCirculator() {
    return circulator;
  }

  public void setCirculator(final String val) {
    circulator = val;
  }

  public ToString toStringSegment(final ToString ts) {
    return super.toStringSegment(ts)
                .append("circulator", circulator);
  }

}
