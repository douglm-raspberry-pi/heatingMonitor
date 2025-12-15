/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.config;

import org.bedework.base.ToString;

import java.util.List;

/**
 * User: mike Date: 3/19/25 Time: 22:24
 */
public class MonitorConfig {
  private long inputsWaitTime;
  private long monitorWaitTime;
  private boolean centigrade;
  private List<ZoneConfig> zones;

  /**
   *
   * @return time in milliseconds inputs should wait. Should be
   *          shorter than the monitor wait time.
   */
  public long getInputsWaitTime() {
    return inputsWaitTime;
  }

  public void setInputsWaitTime(final long val) {
    inputsWaitTime = val;
  }

  /**
   *
   * @return time in milliseconds monitor should wait. Should be
   *          longer than the inputs wait time.
   */
  public long getMonitorWaitTime() {
    return monitorWaitTime;
  }

  public void setMonitorWaitTime(final long val) {
    monitorWaitTime = val;
  }

  public boolean isCentigrade() {
    return centigrade;
  }

  public void setCentigrade(final boolean val) {
    centigrade = val;
  }

  public List<ZoneConfig> getZones() {
    return zones;
  }

  public void setZones(final List<ZoneConfig> val) {
    zones = val;
  }

  public ToString toStringSegment(final ToString ts) {
    ts.append("inputsWaitTime", inputsWaitTime)
      .append("monitorWaitTime", monitorWaitTime)
      .append("centigrade", centigrade)
      .append("zones", zones);

    return ts;
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
