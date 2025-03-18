/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.pi4j.context.Context;
import com.pi4j.io.spi.Spi;

/**
 * User: mike Date: 3/17/25 Time: 18:27
 */
public class SpiDevice implements Logged, AutoCloseable {
  private final Context pi4j;
  private final int address;
  private Spi spi;

  public SpiDevice(final Context pi4j,
                   final int address) {
    this.pi4j = pi4j;
    this.address = address;
  }

  public Spi getSpi() {
    if (spi == null) {
      createSpi();
    }
    return spi;
  }

  public void close() {
    if (spi != null) {
      spi.close();
    }
  }


  protected void createSpi() {
    final var config = Spi.newConfigBuilder(pi4j)
                          .address(address)
                          .channel(1)
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
    debug("Provider class {}", p.getClass().getName());

    spi = p.create(config);
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
