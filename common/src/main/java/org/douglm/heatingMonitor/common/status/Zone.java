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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** provides information about a zone - consists of a number of
 * sub-zones, controlled by inputs and a circulator.
 * <br/>
 * User: mike Date: 4/5/25 Time: 22:45
 */
public class Zone extends Input {
  @JsonIgnore
  private final ZoneConfig config;
  private final Map<String, Temperature> temps = new HashMap<>();
  private final Map<String, SubZone> subZones = new HashMap<>();

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
        addSubZone(new SubZone(szc));
      }
    }
  }

  private Zone(final SubZoneConfig config) {
    this((ZoneConfig)config);
  }

  public ZoneConfig getConfig() {
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

    for (final var sz: subZones.values()) {
      final var szTemp = sz.getTemp(name);
      if (szTemp != null) {
        return szTemp;
      }
    }

    return null;
  }

  public boolean equals(final Zone other) {
    return other != null && this.config.equals(other.getConfig());
  }

  public void addSubZone(final SubZone val) {
    subZones.put(val.getName(), val);
  }

  public SubZone getSubZone(final String name) {
    return subZones.get(name);
  }

  public Collection<SubZone> getSubZones() {
    return subZones.values();
  }

  public void setSubZones(final Collection<SubZone> val) {
    subZones.clear();
    if (val != null) {
      for (final var zone: val) {
        subZones.put(zone.getName(), zone);
      }
    }
  }

  public ToString toStringSegment(final ToString ts) {
    return super.toStringSegment(ts)
                .append("config", getConfig())
                .append("temps", getTemps())
                .append("subZones", getSubZones());
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
