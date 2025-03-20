/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.ToString;

/**
 * User: mike Date: 3/19/25 Time: 22:26
 */
public class DigitalInputConfig {
  private int index;
  private String name;
  private boolean highTrue; // A 1 bit is true otherwise false

  public int getIndex() {
    return index;
  }

  public void setIndex(final int val) {
    index = val;
  }

  public String getName() {
    return name;
  }

  public void setName(final String val) {
    name = val;
  }

  public boolean isHighTrue() {
    return highTrue;
  }

  public void setHighTrue(final boolean val) {
    highTrue = val;
  }

  public String toString() {
    return new ToString(this)
            .append("index", index)
            .append("name", name)
            .append("highTrue", highTrue)
            .toString();
  }
}
