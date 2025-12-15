/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.status;

import org.douglm.heatingMonitor.common.config.MonitorConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Maintains current status of heating.
 *
 * User: mike Date: 4/6/25 Time: 13:27
 */
public class MonitorStatus {
  private final MonitorConfig config;
  private final long startTime =  System.currentTimeMillis();

  private final Map<String, Zone> zones = new HashMap<>();
  private final Map<String, Input> inputs = new HashMap<>();

  private int alwaysOnErrors;

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

  public Input getInput(final String name) {
    return inputs.get(name);
  }

  public Input addInput(final Input input) {
    return inputs.put(input.getName(), input);
  }

  public Collection<Input> getInputs() {
    return inputs.values();
  }

  public int getAlwaysOnErrors() {
    return alwaysOnErrors;
  }

  public void incAlwaysOnErrors() {
    alwaysOnErrors++;
  }
}
