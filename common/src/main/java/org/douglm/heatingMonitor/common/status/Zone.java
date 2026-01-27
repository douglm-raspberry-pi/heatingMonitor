/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.status;

import org.bedework.base.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.douglm.heatingMonitor.common.config.SubZoneConfig;
import org.douglm.heatingMonitor.common.config.ZoneConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** provides information about a zone - consists of a number of
 * sub-zones, controlled by inputs and a circulator.
 * <br/>
 * User: mike Date: 4/5/25 Time: 22:45
 */
public class Zone extends BasicSwitchedEntity {
  @JsonIgnore
  private final ZoneConfig config;
  private Input circulator;
  private boolean circulatorOn;
  private boolean inputChanged;
  private boolean wasChecked;
  private final Map<String, Temperature> temps = new HashMap<>();
  private final Map<String, Zone> subZones = new HashMap<>();
  @JsonIgnore
  private final List<Input> inputs = new ArrayList<>();

  @JsonCreator
  public Zone(@JsonProperty("name") final String name) {
    super(name);
    config = null;
  }

  public Zone(final ZoneConfig config) {
    super(config.getName());
    this.config = config;
    setLastChange(System.currentTimeMillis());

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
  }

  private Zone(final SubZoneConfig config) {
    this((ZoneConfig)config);
  }

  public ZoneConfig getConfig() {
    return config;
  }

  /**
   *
   * @return input which monitors the actual circulator state
   */
  @JsonIgnore
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

  public Collection<Temperature> getTemps() {
    return temps.values();
  }

  public void setTemps(final Collection<Temperature> val) {
    temps.clear();
    if (val != null) {
      for (final var temp: val) {
        temps.put(temp.getName(), temp);
      }
    }
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
      final var szTemp = sz.findTemp(name);
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

  public boolean getWasChecked() {
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

  public Collection<Zone> getSubZones() {
    return subZones.values();
  }

  public void setSubZones(final Collection<Zone> val) {
    subZones.clear();
    if (val != null) {
      for (final var zone: val) {
        subZones.put(zone.getName(), zone);
      }
    }
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
                .append("config", config)
                .append("circulator", circulator)
                .append("circulatorOn", isCirculatorOn())
                .append("inputChanged", getInputChanged())
                .append("wasChecked", getWasChecked())
                .append("temps", getTemps())
                .append("subZones", getSubZones())
                .append("inputs", inputs);
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
