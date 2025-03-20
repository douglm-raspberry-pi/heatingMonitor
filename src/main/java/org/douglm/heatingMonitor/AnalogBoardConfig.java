/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.ToString;

import java.util.List;

/**
 * User: mike Date: 3/19/25 Time: 22:37
 */
public class AnalogBoardConfig {
  private int spiAddress;
  private List<AnalogChannelConfig> channels;

  public int getSpiAddress() {
    return spiAddress;
  }

  public void setSpiAddress(final int val) {
    spiAddress = val;
  }

  public List<AnalogChannelConfig> getChannels() {
    return channels;
  }

  public void setChannels(
          final List<AnalogChannelConfig> val) {
    channels = val;
  }

  public String toString() {
    return new ToString(this)
            .append("spiAddress", spiAddress)
            .append("channels", channels)
            .toString();
  }
}
