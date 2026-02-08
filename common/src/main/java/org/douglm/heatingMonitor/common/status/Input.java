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
public class Input implements SwitchedEntity {
  private final String name;
  private long lastChange;
  private long runningTime;
  private long offTime;
  private boolean switchValue;

  // true if lastStatus needs flipping
  private boolean changed;

  @JsonCreator
  public Input(
          @JsonProperty("name") final String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
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
  public void setRunningTime(final long val) {
    runningTime = val;
  }

  @Override
  public void incRunningTime(final long val) {
    runningTime += val;
  }

  @Override
  public long getOffTime() {
    return offTime;
  }

  @Override
  public void setOffTime(final long val) {
    offTime = val;
  }

  @Override
  public void incOffTime(final long val) {
    offTime += val;
  }

  /**
   *
   * @return status last time monitor checked.
   */
  @Override
  public boolean getSwitchValue() {
    return switchValue;
  }

  @Override
  public void setSwitchValue(final boolean val) {
    switchValue = val;
  }

  public void currentStatus(final boolean val) {
    updateTimes();
    setSwitchValue(val);
  }

  @JsonIgnore
  public boolean isChanged() {
    return changed;
  }

  public ToString toStringSegment(final ToString ts) {
    return ts.append("name", getName())
             .append("lastChange", getLastChange())
             .append("runningTime", getRunningTime())
             .append("offTime", getOffTime())
             .append("switchValue", getSwitchValue());
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
