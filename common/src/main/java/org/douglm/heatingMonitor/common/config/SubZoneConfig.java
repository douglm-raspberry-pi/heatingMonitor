/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.config;

import org.bedework.base.ToString;

/** A sub-zone is usually controlled by a
 * zone valve - covering perhaps a single room.
 * User: mike Date: 3/26/25 Time: 11:52
 */
public class SubZoneConfig {
  private String name;

  // TODO - links to thermostats

  public String getName() {
    return name;
  }

  public void setName(final String val) {
    name = val;
  }

  public boolean equals(final SubZoneConfig other) {
    return other != null && getName().equals(other.getName());
  }

  public ToString toStringSegment(final ToString ts) {
    return ts.append("name", getName());
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
