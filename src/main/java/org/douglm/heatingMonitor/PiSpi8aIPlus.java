/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import com.pi4j.context.Context;

import java.security.ProviderException;

/** Support Widgetlords PI-SPI-8AI+ analog to digital converter.
 * <p>
 *   This may be configured in 4 modes. Only support thermistor mode
 *   for the moment.
 *   There are 8 channels on each board. Each may be configured with one
 *   of the four modes.
 * </p>
 *
 * User: mike Date: 3/17/25 Time: 17:38
 */
public class PiSpi8aIPlus extends SpiDevice {
  final AnalogBoardConfig config;

  public PiSpi8aIPlus(final AnalogBoardConfig config,
                      final Context context,
                      final int address) {
    super(context, address);
    this.config = config;
  }

  public AnalogChannelConfig getChannelConfig(final int channel) {
    for (final var c: config.getChannels()) {
      if (c.getChannel() == channel) {
        return c;
      }
    }

    throw new RuntimeException("Channel " + channel + " not configured");
  }

  public double getTemperature(final int channel) {
    final var chanConf = getChannelConfig(channel);
    final var val = readChannel(chanConf);
    System.out.println(val);
    System.out.println("SH: " + rToDegCWithSh(chanConf, val));
    final var degCBeta = rToDegCWithBeta(chanConf, val);
    System.out.println("beta: " + degCBeta +
                               " F: " + ((degCBeta * 1.8) + 32));

    return degCBeta;
  }

  /* mcp3208 for 8 bit chunks:
      Output
      Byte 0
      0 0 0 0 0 1 s/d D2     5 pad bits 1 start bit 1 s/d D2
      s/d is single-ended = 1 differential = 0
      D2 D1 D0 specify channel (0-7)

      Byte 1
      D1 D0 x x x x x x
      x is don't care

      Byte 2 - all don't care

      Input:
      Byte 0
      x x x x x x x x
      Byte 1
      x x x x b11 b10 b9 b8
      Byte 2
      b7 b6 b5 b4 b3 b2 b1 b0

      That is 12 bits of MSB data
   */
  private int readChannel(final AnalogChannelConfig chanConf) throws ProviderException {
    final var channel = chanConf.getChannel();
    final byte[] data = new byte[3];
    data[0] = (byte)(0x06 | (channel >> 2)); // = start + single + D2
    data[1] = (byte)((channel << 6) & 0xC0); // = D1 D0 x x x x x x
    dumpBytes("before", data);

    final var res = getSpi().transfer(data, 3);
    if (res >= 0) {
      dumpBytes("after (" + res + ")", data);
      return ((data[1] & 0x0F) << 8) | Byte.toUnsignedInt(data[2]);
    } else {
      throw new ProviderException("Bad result " + res);
    }
  }

  public double rToDegCWithSh(final AnalogChannelConfig chanConf,
                              final int value) {
    return rToDegC(chanConf, value);
  }

  final static double kelvinAt0C = 273.15;

  public double countToResistance(final AnalogChannelConfig chanConf,
                                  final int value) {
    return (double)(value * chanConf.getInputReference()) /
            (chanConf.getInputRange() - value);
  }

  /** This converts the value - a measure of resistance to degrees c.
   * It applies the Steinhart-Hart equation. There is a wikipedia
   * article but <a href="https://www.northstarsensors.com/calculating-temperature-from-resistance">this from North Star sensors</a>
   * is a much fuller description of all the variables involved.
   *
   * @param chanConf config for the channel
   * @param value read from device
   * @return degrees centigrade
   */
  public double rToDegC(final AnalogChannelConfig chanConf,
                        final int value) {
    final var A               = 0.00116597;
    final var B               = 0.000220635;
    final var C               = 1.81284e-06;
    final var D               = 2.73396e-09;

    final var r = countToResistance(chanConf, value);

    final var logr = Math.log(r);
    return 1.0 / (A + (B * r) +
                          (C * (logr * logr)) +
                          (D * (logr * logr * logr)));
  }

  /** This converts the value - a measure of resistance to degrees c.
   * It uses the beta value. See <a href="https://www.northstarsensors.com/calculating-temperature-from-resistance">from North Star sensors</a>
   * a much fuller description of all the variables involved.
   *
   * @param chanConf config for the channel
   * @param value read from device
   * @return degrees centigrade
   */
  public double rToDegCWithBeta(final AnalogChannelConfig chanConf,
                                final int value) {
    final int rAt25 = 10000;

    final var r = countToResistance(chanConf, value);
    return 1 / (1 / (kelvinAt0C + 25.) +
                        (double)1L / chanConf.getBeta() * Math.log(r / rAt25)) - kelvinAt0C;
  }
}
