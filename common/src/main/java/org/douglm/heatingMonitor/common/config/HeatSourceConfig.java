/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.config;

import java.util.List;

/** A heat source may be a boiler or heat pump etc (which suggests
 * the name is wrong as it could also cool...)
 * <p>
 *   It will have an incoming temperature and an outgoing temperature.
 *   It may also have a switch - for example is the gas running?
 * </p>
 * <p>
 *   A heat source will have a number of zones.
 * </p>
 *
 * User: mike Date: 2/3/26 Time: 22:28
 */
public class HeatSourceConfig extends Circuit {
  private List<ZoneConfig> zones;

  public List<ZoneConfig> getZones() {
    return zones;
  }

  public void setZones(final List<ZoneConfig> val) {
    zones = val;
  }

}
