/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.config;

import org.bedework.base.ToString;

import java.util.List;

/** A zone is an apartment or floor often handled by a single
 * circulator. It may be divided into sub-zones, usually controlled by
 * zone valves - covering perhaps a single room.
 * User: mike Date: 3/26/25 Time: 11:52
 */
public class ZoneConfig extends Circuit {
  private boolean hasPriority;
  private List<SubZoneConfig> subZones;

  public boolean getHasPriority() {
    return hasPriority;
  }

  public void setHasPriority(final boolean val) {
    hasPriority = val;
  }

  public List<SubZoneConfig> getSubZones() {
    return subZones;
  }

  public void setSubZones(final List<SubZoneConfig> val) {
    subZones = val;
  }

  public boolean equals(final ZoneConfig other) {
    return other != null && getName().equals(other.getName());
  }

  public ToString toStringSegment(final ToString ts) {
    return super.toStringSegment(ts)
                .append("hasPriority", getHasPriority())
                .append("subZones", getSubZones());
  }
}
