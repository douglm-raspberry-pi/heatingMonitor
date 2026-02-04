/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.status;

import org.bedework.base.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.douglm.heatingMonitor.common.config.SubZoneConfig;

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
public class SubZone extends BasicSwitchedEntity {
  @JsonIgnore
  private final SubZoneConfig config;
  private boolean inputChanged;
  private boolean wasChecked;
  private final Map<String, Temperature> temps = new HashMap<>();
  @JsonIgnore
  private final List<Input> inputs = new ArrayList<>();

  @JsonCreator
  public SubZone(@JsonProperty("name") final String name) {
    super(name);
    config = null;
  }

  public SubZone(final SubZoneConfig config) {
    super(config.getName());
    this.config = config;
    setLastChange(System.currentTimeMillis());

    if (config.getOutTempName() != null) {
      putTemp(new Temperature(config.getOutTempName()));
    }
    if (config.getReturnTempName() != null) {
      putTemp(new Temperature(config.getReturnTempName()));
    }
  }

  public SubZoneConfig getConfig() {
    return config;
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

  public boolean equals(final SubZone other) {
    return other != null && this.config.equals(other.getConfig());
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
                .append("inputChanged", getInputChanged())
                .append("wasChecked", getWasChecked())
                .append("temps", getTemps())
                .append("inputs", inputs);
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
