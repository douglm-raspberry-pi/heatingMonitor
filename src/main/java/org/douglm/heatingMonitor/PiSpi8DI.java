/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import com.pi4j.context.Context;

import java.security.ProviderException;

/**
 * User: mike Date: 3/18/25 Time: 22:41
 */
public class PiSpi8DI extends SpiDevice {
  private final int channel;

  /**
   *
   * @param context Pi4j
   * @param address of chip
   * @param channel sensor number 0-7
   */
  public PiSpi8DI(final Context context,
                  final int address,
                  final int channel) {
    super(context, address);

    this.channel = channel;
  }

  private int readByte() throws ProviderException {
    final byte[] data = new byte[3];
    data[0] = (byte)(0x41 | (channel << 1)); // Address
    data[1] = (byte)(0x09);    // GPIO register
    dumpBytes("before", data);

    final var res = getSpi().transfer(data, 3);
    if (res >= 0) {
      dumpBytes("after (" + res + ")", data);
      return Byte.toUnsignedInt(data[2]);
    } else {
      throw new ProviderException("Bad result " + res);
    }
  }

  private boolean[] states() {
    final var res =  new boolean[8];
    final var b = readByte();

    for (var i = 0; i < 8; i++) {
      res[i] = ((byte)(b >> i) & 1) == 1;
    }

    return res;
  }

  private boolean state(final int input)  throws ProviderException {
    final var b = readByte();
    return ((byte)(b >> input) & 1) == 1;
  }
}
