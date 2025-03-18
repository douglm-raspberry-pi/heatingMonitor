/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.util.logging.BwLogger;

import com.pi4j.context.Context;

import java.security.ProviderException;

import static java.lang.String.format;

/** Support Widgetlords PI-SPI-8A1+ analog to digital converter.
 * <p>
 *   This may be configured in 4 modes. Only support thermistor mode
 *   for the moment.
 *   There are 8 channels on each board. Each may be configured with one
 *   of the four modes.
 * </p>
 *
 * User: mike Date: 3/17/25 Time: 17:38
 */
public class PiSpi8a1Plus extends SpiDevice {
  public enum Mode {
    thermistor,
    milliAmp,
    dc5,
    dc10
  }

  private final Context context;
  private final Mode mode;
  private final int channel;

  public PiSpi8a1Plus(final Context context,
                      final int address,
                      final Mode mode,
                      final int channel) {
    super(context, address);

    if (mode != Mode.thermistor) {
      throw new IllegalArgumentException("Mode must be thermistor");
    }

    this.context = context;
    this.mode = mode;
    this.channel = channel;
  }

  public double getTemperature() {
    if (mode != Mode.thermistor) {
      throw new RuntimeException("Not configured as thermistor");
    }

    final var val = readByte();
    System.out.println(val);
    System.out.println("SH: " + rToDegCWithSh(val));
    final var degCBeta = rToDegCWithBeta(val);
    System.out.println("beta: " + degCBeta +
                               " F: " + ((degCBeta * 1.8) + 32));

    return degCBeta;
  }

  private int readByte() throws ProviderException {
    final byte[] data = new byte[3];
    data[0] = (byte)(0x06 | (channel >> 2));
    data[1] = (byte)((channel << 6) & 0xC0);
    dumpBytes("before", data);

    final var res = getSpi().transfer(data, 3);
    if (res >= 0) {
      dumpBytes("after (" + res + ")", data);
      return ((data[1] & 0x0F) << 8) | Byte.toUnsignedInt(data[2]);
    } else {
      throw new ProviderException("Bad result " + res);
    }
  }

  public static double rToDegCWithSh(final int value) {
    return rToDegC(10000, // Load for widgetlords Pi-Spi-8A1+
                   4096,  // Range of values
                   value);
  }

  public static double rToDegCWithBeta(final int value) {
    return rToDegC(10000, // Load for widgetlords Pi-Spi-8A1+
                   4095,  // Range of values
                   3380,  // Value provided on widgetlords site
                   value);
  }

  final static double kelvinAt0C = 273.15;

  /** This converts the value - a measure of resistance to degrees c.
   * It applies the Steinhart-Hart equation. There is a wikipedia
   * article but <a href="https://www.northstarsensors.com/calculating-temperature-from-resistance">this from North Star sensors</a>
   * is a much fuller description of all the variables involved.
   *
   * @param load value of load resistance
   * @param range of values
   * @param value read from device
   * @return degrees centigrade
   */
  public static double rToDegC(final int load,
                               final int range,
                               final int value) {
    final var A               = 0.00116597;
    final var B               = 0.000220635;
    final var C               = 1.81284e-06;
    final var D               = 2.73396e-09;

    final var r = (value * load) / (range - value);

    final var logr = Math.log(r);
    return 1.0 / (A + (B * r) +
                          (C * (logr * logr)) +
                          (D * (logr * logr * logr)));
  }

  /** This converts the value - a measure of resistance to degrees c.
   * It uses the beta value. See <a href="https://www.northstarsensors.com/calculating-temperature-from-resistance">from North Star sensors</a>
   * a much fuller description of all the variables involved.
   *
   * @param load value of load resistance
   * @param range of values
   * @param beta calculated or from manufacturers tables
   * @param value read from device
   * @return degrees centigrade
   */
  public static double rToDegC(final int load,
                               final int range,
                               final int beta,
                               final int value) {
    final int rAt25 = 10000;

    final var r = (value * load) / (range - value);
    return 1 / (1 / (kelvinAt0C + 25.) +
                        (double)1L / beta * Math.log((double)r / rAt25)) - kelvinAt0C;
  }

  private void dumpBytes(final String s,final byte[] data) {
    final var sb = new StringBuilder(s).append(":");
    for (final byte b: data) {
      sb.append(format("%02x ", b));
    }
    debug(sb.toString());
  }

  /* ==============================================================
   *                   Logged methods
   * ============================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
