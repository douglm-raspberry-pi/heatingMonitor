/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.status;

import org.bedework.base.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.douglm.heatingMonitor.common.config.MonitorConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Maintains current status of heating.
 *
 * User: mike Date: 4/6/25 Time: 13:27
 */
public class MonitorStatus {
  @JsonIgnore
  private final MonitorConfig config;
  private final long startTime =  System.currentTimeMillis();

  private final Map<String, Zone> zones = new HashMap<>();
  @JsonIgnore
  private final Map<String, Input> inputs = new HashMap<>();

  private int alwaysOnErrors;

  // Needed for json
  public MonitorStatus() {
    config = null;
  }

  public MonitorStatus(final MonitorConfig config) {
    this.config = config;
  }

  public MonitorConfig getConfig() {
    return config;
  }

  public long getStartTime() {
    return startTime;
  }

  public Zone getZone(final String name) {
    return zones.get(name);
  }

  public Zone addZone(final Zone zone) {
    return zones.put(zone.getConfig().getName(), zone);
  }

  public Collection<Zone> getZones() {
    return zones.values();
  }

  public void setZones(final Collection<Zone> val) {
    zones.clear();
    if (val != null) {
      for (final var zone: val) {
        zones.put(zone.getName(), zone);
      }
    }
  }

  @JsonIgnore
  public Input getInput(final String name) {
    return inputs.get(name);
  }

  public Input addInput(final Input input) {
    return inputs.put(input.getName(), input);
  }

  @JsonIgnore
  public Collection<Input> getInputs() {
    return inputs.values();
  }

  public int getAlwaysOnErrors() {
    return alwaysOnErrors;
  }

  public void incAlwaysOnErrors() {
    alwaysOnErrors++;
  }

  public Temperature findTemp(final String name) {
    for (final var zone: getZones()) {
      final var temp = zone.findTemp(name);
      if (temp != null) {
        return temp;
      }
    }

    return null;
  }

  public String toString() {
    return new ToString(this)
            .append("startTime", getStartTime())
            .append("zones", getZones())
            .append("alwaysOnErrors", getAlwaysOnErrors())
            .toString();
  }
}
