/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.config;

import org.bedework.base.ToString;

/** A circuit is a stream of water which may be subdivided
 * into sub-circuits. It may have a circulator - which is a
 * zone, or it may have a zone-valve which is a sub=zone.
 *
 * <p>It may have monitored outgoing and return
 * temperatures. A null name means unmonitored</p>
 * User: mike Date: 3/26/25 Time: 11:52
 */
public class Circuit {
  private String name;
  private String outTempName;
  private String returnTempName;

  public String getName() {
    return name;
  }

  public void setName(final String val) {
    name = val;
  }

  public String getOutTempName() {
    return outTempName;
  }

  public void setOutTempName(final String val) {
    outTempName = val;
  }

  public String getReturnTempName() {
    return returnTempName;
  }

  public void setReturnTempName(final String val) {
    returnTempName = val;
  }

  public boolean equals(final Circuit other) {
    return other != null && getName().equals(other.getName());
  }

  public ToString toStringSegment(final ToString ts) {
    return ts.append("name", getName())
             .append("outTempName", getOutTempName())
             .append("returnTempName", getReturnTempName());
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
