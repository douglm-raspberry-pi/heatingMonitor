/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.status;

import org.bedework.base.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.douglm.heatingMonitor.common.config.HeatSourceConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** provides information about a heat source - e.g a
 * boiler. The input represents it's current state, e.g.
 * the burners are running
 * <br/>
 * User: mike Date: 4/5/25 Time: 22:45
 */
public class HeatSource extends Input {
  @JsonIgnore
  private final HeatSourceConfig config;
  private final Map<String, Temperature> temps = new HashMap<>();
  private final Map<String, Zone> zones = new HashMap<>();

  @JsonCreator
  public HeatSource(@JsonProperty("name") final String name) {
    super(name);
    config = null;
  }

  public HeatSource(final HeatSourceConfig config) {
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

  public HeatSourceConfig getConfig() {
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

  public Temperature findTemp(final String name) {
    final var temp = temps.get(name);
    if (temp != null) {
      return temp;
    }

    for (final var sz: zones.values()) {
      final var szTemp = sz.findTemp(name);
      if (szTemp != null) {
        return szTemp;
      }
    }

    return null;
  }

  public boolean equals(final HeatSource other) {
    return other != null && this.config.equals(other.getConfig());
  }

  public Zone addZone(final Zone val) {
    return zones.put(val.getName(), val);
  }

  public Zone getZone(final String name) {
    return zones.get(name);
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

  public ToString toStringSegment(final ToString ts) {
    return super.toStringSegment(ts)
                .append("config", config)
                .append("temps", getTemps())
                .append("subZones", getZones());
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
