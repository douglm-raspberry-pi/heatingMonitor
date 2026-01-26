/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User: mike Date: 1/18/26 Time: 22:39
 */
public class Temperature {
  private final String name;
  private double degreesCelsius;

  @JsonCreator
  public Temperature(
          @JsonProperty("name") final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public double getDegreesCelsius() {
    return degreesCelsius;
  }

  public void setDegreesCelsius(final double val) {
    degreesCelsius = val;
  }
}
