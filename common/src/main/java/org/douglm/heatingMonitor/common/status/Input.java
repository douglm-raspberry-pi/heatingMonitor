/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.status;

import org.bedework.base.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User: mike Date: 4/6/25 Time: 13:38
 */
public class Input implements SwitchedEntity {
  private final String name;

  private final Zone zone;

  private long lastChange;
  private long runningTime;

  private boolean lastStatus;

  // true if lastStatus needs flipping
  private boolean changed;

  public Input(final String name,
               final Zone zone) {
    this.name = name;
    this.zone = zone;
  }

  public String getName() {
    return name;
  }

  @JsonIgnore
  public Zone getZone() {
    return zone;
  }

  @Override
  public long getLastChange() {
    return lastChange;
  }

  @Override
  public void setLastChange(final long val) {
    lastChange = val;
  }

  @Override
  public long getRunningTime() {
    return runningTime;
  }

  @Override
  public void incRunningTime(final long val) {
    runningTime += val;
  }

  /**
   *
   * @return status last time monitor checked.
   */
  public boolean getLastStatus() {
    return lastStatus;
  }

  public void setLastStatus(final boolean val) {
    lastStatus = val;
  }

  public void currentStatus(final boolean val) {
    changed = val != lastStatus;
  }

  public boolean testAndSetChanged() {
    if (!changed) {
      return false;
    }

    lastStatus = !lastStatus;
    changed = false;

    if (!lastStatus) {
      // Turned off - update running time
      updateRunningTime();
    } else {
      // Turned on - flag start
      setLastChange(System.currentTimeMillis());
    }
    return true;
  }

  public boolean isChanged() {
    return changed;
  }

  public ToString toStringSegment(final ToString ts) {
    return ts.append("name", getName())
             .append("zone", getZone())
             .append("lastChange", getLastChange())
             .append("runningTime", getRunningTime())
             .append("lastStatus", getLastStatus());
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
