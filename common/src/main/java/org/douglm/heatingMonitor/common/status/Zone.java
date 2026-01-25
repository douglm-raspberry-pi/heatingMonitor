/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.status;

import org.bedework.base.ToString;

import org.douglm.heatingMonitor.common.config.SubZoneConfig;
import org.douglm.heatingMonitor.common.config.ZoneConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** provides information about a zone - consists of a number of
 * sub-zones, controlled by inputs and a circulator.
 * <br/>
 * User: mike Date: 4/5/25 Time: 22:45
 */
public class Zone implements SwitchedEntity {
  private final String name;
  private final ZoneConfig config;
  private Input circulator;
  private boolean circulatorOn;
  private boolean inputChanged;
  private boolean wasChecked;
  private final Map<String, Temperature> temps = new HashMap<>();
  private final Map<String, Zone> subZones = new HashMap<>();
  private final List<Input> inputs = new ArrayList<>();

  private long lastChange;
  private long runningTime;

  public Zone(final ZoneConfig config) {
    this.config = config;
    this.name = config.getName();
    if (config.getOutTempName() != null) {
      putTemp(new Temperature(config.getOutTempName()));
    }
    if (config.getReturnTempName() != null) {
      putTemp(new Temperature(config.getReturnTempName()));
    }

    if (config.getSubZones() != null) {
      for (final var szc: config.getSubZones()) {
        addSubZone(new Zone(szc));
      }
    }
    this.lastChange = System.currentTimeMillis();
  }

  private Zone(final SubZoneConfig config) {
    this((ZoneConfig)config);
  }

  public String getName() {
    return name;
  }

  public ZoneConfig getConfig() {
    return config;
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
   * @return input which monitors the actual circulator state
   */
  public Input getCirculator() {
    return circulator;
  }

  public void setCirculator(final Input val) {
    circulator = val;
  }

  /**
   *
   * @return true if circulator is running
   */
  public boolean isCirculatorOn() {
    return circulatorOn;
  }

  public void setCirculatorOn(final boolean val) {
    circulatorOn = val;
  }

  public Map<String, Temperature> getTemps() {
    return temps;
  }

  public void putTemp(final Temperature val) {
    temps.put(val.getName(), val);
  }

  public Temperature findTemp(final String name) {
    final var temp = temps.get(name);
    if (temp != null) {
      return temp;
    }

    for (final var sz: subZones.values()) {
      final var szTemp = sz.getTemps().get(name);
      if (szTemp != null) {
        return szTemp;
      }
    }

    return null;
  }

  public List<Input> getInputs() {
    return inputs;
  }

  public void addInput(final Input val) {
    inputs.add(val);
  }

  public boolean getInputChanged() {
    return inputChanged;
  }

  /** Called whenever an input changes state
   *
   */
  public void inputChanged() {
    inputChanged = true;
  }

  public boolean wasChecked() {
    return wasChecked;
  }

  public void setWasChecked(final boolean val) {
    wasChecked = val;
  }

  public boolean equals(final Zone other) {
    return other != null && this.config.equals(other.getConfig());
  }

  public void addSubZone(final Zone val) {
    subZones.put(val.getName(), val);
  }

  public Zone getSubZone(final String name) {
    return subZones.get(name);
  }

  public ToString toStringSegment(final ToString ts) {
    return ts.append("config", config)
             .append("lastChange", lastChange)
             .append("runningTime", runningTime)
             .append("circulator", circulator)
             .append("inputs", inputs);
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
