/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.spi.Spi;

import java.security.ProviderException;

import static java.lang.String.format;

/**
 * User: mike Date: 3/13/25 Time: 14:30
 */
public class TryStuff {
  public static void main(String[] args) {
    final var ts = new TryStuff();

    try (final var spi = ts.createSpi(0, 1)) {
      while (true) {
        final var val = ts.readByte(spi, 7);
        System.out.println(val);
        try {
          Thread.sleep(500);
        } catch (final InterruptedException ignored) {
          break;
        }
      }
    }
  }

  private final Context pi4j;

  public TryStuff() throws ProviderException {
    pi4j = Pi4J.newAutoContext();
  }

  private int readByte(final Spi spi, final int channel) throws ProviderException {
    final byte[] data = new byte[3];
    data[0] = (byte)(0x06 | (channel >> 2));
    data[1] = (byte)((channel << 6) & 0xC0);
    dumpBytes("before", data);

    final var res = spi.transfer(data, 3);
    if (res >= 0) {
      dumpBytes("after (" + res + ")", data);
      return ((data[1] & 0x0F) << 8) | data[2];
    } else {
      throw new ProviderException("Bad result " + res);
    }
  }

  private void dumpBytes(final String s,final byte[] data) {
    System.out.print(s + ": ");
    for (final byte b: data) {
       System.out.print(format("%02x ", b));
    }
    System.out.println();
  }

  private Spi createSpi(final Integer address,
                        final Integer channel) {
    final var config = Spi.newConfigBuilder(pi4j)
                          .address(address).channel(channel)
                          .build();

    /*
    var config  = Spi.newConfigBuilder(pi4j)
                     .id("my-spi")
                     .name("My SPI")
                     .bus(SpiBus.BUS_1)                    //<----- CONFIGURE SPI BUS
     .chipSelect(SpiChipSelect.CS_0)       //<----- CONFIGURE SPI CS/ADDRESS/CHANNEL
            .mode(SpiMode.MODE_3)
            .build();
     */
    final var p = pi4j.spi();
    System.out.println(format("Provider class %s", p.getClass().getName()));
    return p.create(config);
  }
}
