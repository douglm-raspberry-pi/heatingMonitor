/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.status;

import org.bedework.base.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User: mike Date: 4/6/25 Time: 13:38
 */
public class Input extends BasicSwitchedEntity {
  private final Zone zone;

  // true if lastStatus needs flipping
  private boolean changed;

  @JsonCreator
  public Input(
          @JsonProperty("name") final String name) {
    this(name, null);
  }

  public Input(final String name,
               final Zone zone) {
    super(name);
    this.zone = zone;
  }

  @JsonIgnore
  public Zone getZone() {
    return zone;
  }

  public void currentStatus(final boolean val) {
    changed = val != getSwitchValue();
  }

  public boolean testAndSetChanged() {
    if (!changed) {
      return false;
    }

    setSwitchValue(!getSwitchValue());
    changed = false;

    if (!getSwitchValue()) {
      // Turned off - update running time
      updateRunningTime();
    } else {
      // Turned on - flag start
      setLastChange(System.currentTimeMillis());
    }
    return true;
  }

  @JsonIgnore
  public boolean isChanged() {
    return changed;
  }

  public SwitchedEntity toSwitchedEntity() {
    final var res = new BasicSwitchedEntity(getName());
    res.setLastChange(getLastChange());
    res.setRunningTime(getRunningTime());
    res.setSwitchValue(getSwitchValue());

    return res;
  }

  public ToString toStringSegment(final ToString ts) {
    return super.toStringSegment(ts)
                .append("zone", getZone());
  }
}
