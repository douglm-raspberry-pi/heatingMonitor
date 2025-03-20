/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.ToString;

/**
 * User: mike Date: 3/19/25 Time: 22:26
 */
public class AnalogChannelConfig {
  public enum Mode {
    thermistor,
    milliAmp,
    dc5,
    dc10
  }

  private Mode mode;
  private int channel;
  private String name;
  private int beta; // for thermistors

  public Mode getMode() {
    return mode;
  }

  public void setMode(
          final Mode mode) {
    this.mode = mode;
  }

  public int getChannel() {
    return channel;
  }

  public void setChannel(final int channel) {
    this.channel = channel;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public int getBeta() {
    return beta;
  }

  public void setBeta(final int beta) {
    this.beta = beta;
  }

  @Override
  public String toString() {
    return new ToString(this)
            .append("mode", mode)
            .append("channel", channel)
            .append("name", name)
            .append("beta", beta)
            .toString();
  }
}
