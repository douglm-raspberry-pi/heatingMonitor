/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.ToString;

import org.douglm.piSpi.PiSpi8DIInputConfig;

/**
 * User: mike Date: 3/19/25 Time: 22:26
 */
public class DigitalInputConfig extends PiSpi8DIInputConfig {
  private boolean alwaysOn;
  private String zone;
  private boolean circulator;

  public boolean isAlwaysOn() {
    return alwaysOn;
  }

  /**
   *
   * @param val true if we expect this to be always on.
   */
  public void setAlwaysOn(final boolean val) {
    alwaysOn = val;
  }

  public String getZone() {
    return zone;
  }

  public void setZone(final String val) {
    zone = val;
  }

  /**
   *
   * @return true if this is a zone circulator
   */
  public boolean isCirculator() {
    return circulator;
  }

  public void setCirculator(final boolean val) {
    circulator = val;
  }

  public ToString toStringSegment(final ToString ts) {
    return super.toStringSegment(ts)
                .append("zone", zone);
  }
}
