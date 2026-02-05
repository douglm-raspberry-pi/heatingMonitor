/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.status;

import org.bedework.base.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.douglm.heatingMonitor.common.config.MonitorConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Maintains current status of heating.
 *
 * User: mike Date: 4/6/25 Time: 13:27
 */
public class MonitorStatus {
  @JsonIgnore
  private final MonitorConfig config;
  private long startTime;
  private long timestamp;

  private final Map<String, HeatSource> heatSources = new HashMap<>();
  @JsonIgnore
  private final Map<String, Input> inputs = new HashMap<>();

  private final List<Input> sensors = new ArrayList<>();

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

  public void setStartTime(final long val) {
    startTime = val;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setTimestamp(final long val) {
    timestamp = val;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public HeatSource getHeatSource(final String name) {
    return heatSources.get(name);
  }

  public HeatSource addHeatSource(final HeatSource val) {
    return heatSources.put(val.getConfig().getName(), val);
  }

  public Collection<HeatSource> getHeatSources() {
    return heatSources.values();
  }

  public void setHeatSources(final Collection<HeatSource> val) {
    heatSources.clear();
    if (val != null) {
      for (final var hs: val) {
        heatSources.put(hs.getName(), hs);
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

  public void setSensors(final List<Input> val) {
    sensors.clear();
    sensors.addAll(val);
  }

  public List<Input> getSensors() {
    return sensors;
  }

  public void addSensor(final Input val) {
    sensors.add(val);
  }

  public void setAlwaysOnErrors(final int val) {
    alwaysOnErrors = val;
  }

  public int getAlwaysOnErrors() {
    return alwaysOnErrors;
  }

  public void incAlwaysOnErrors() {
    alwaysOnErrors++;
  }

  public Temperature findTemp(final String name) {
    for (final var hs: getHeatSources()) {
      final var temp = hs.findTemp(name);
      if (temp != null) {
        return temp;
      }
    }

    return null;
  }

  public String toString() {
    return new ToString(this)
            .append("startTime", getStartTime())
            .append("heatSources", getHeatSources())
            .append("alwaysOnErrors", getAlwaysOnErrors())
            .append("sensors", getSensors())
            .toString();
  }
}
