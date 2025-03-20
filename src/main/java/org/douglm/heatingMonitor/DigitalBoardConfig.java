/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.ToString;

import java.util.List;

/**
 * User: mike Date: 3/19/25 Time: 22:37
 */
public class DigitalBoardConfig {
  private int spiAddress;
  private int chipAddress; // 0 to 3
  private List<DigitalInputConfig> inputs;

  public int getSpiAddress() {
    return spiAddress;
  }

  public void setSpiAddress(final int val) {
    spiAddress = val;
  }

  public int getChipAddress() {
    return chipAddress;
  }

  public void setChipAddress(final int val) {
    chipAddress = val;
  }

  public List<DigitalInputConfig> getInputs() {
    return inputs;
  }

  public void setInputs(
          final List<DigitalInputConfig> val) {
    inputs = val;
  }

  public String toString() {
    return new ToString(this)
            .append("spiAddress", spiAddress)
            .append("inputs", inputs)
            .toString();
  }
}
