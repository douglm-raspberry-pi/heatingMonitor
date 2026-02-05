/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.status;

import org.bedework.base.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.douglm.heatingMonitor.common.config.SubZoneConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** provides information about a zone - consists of a number of
 * sub-zones, controlled by inputs and a circulator.
 * <br/>
 * User: mike Date: 4/5/25 Time: 22:45
 */
public class SubZone extends Input {
  @JsonIgnore
  private final SubZoneConfig config;
  private final Map<String, Temperature> temps = new HashMap<>();

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

  public Temperature getTemp(final String name) {
    return temps.get(name);
  }

  public boolean equals(final SubZone other) {
    return other != null && this.config.equals(other.getConfig());
  }

  public ToString toStringSegment(final ToString ts) {
    return super.toStringSegment(ts)
                .append("config", config)
                .append("temps", getTemps());
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
