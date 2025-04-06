/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.ToString;

/**
 * User: mike Date: 3/26/25 Time: 11:52
 */
public class ZoneConfig {
  private String name;
  private boolean hasPriority;

  public String getName() {
    return name;
  }

  public void setName(final String val) {
    name = val;
  }

  public boolean isHasPriority() {
    return hasPriority;
  }

  public void setHasPriority(final boolean val) {
    hasPriority = val;
  }

  public ToString toStringSegment(final ToString ts) {
    ts.append("name", name)
      .append("hasPriority", hasPriority);

    return ts;
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
