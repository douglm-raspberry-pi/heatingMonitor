/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import java.security.ProviderException;

import static org.douglm.heatingMonitor.PiSpi8a1Plus.Mode.thermistor;

/**
 * User: mike Date: 3/13/25 Time: 14:30
 */
public class Monitor implements Logged {
  public static void main(final String[] args) {
    final var m = new Monitor();

    m.monitorTemp();
  }

  private final Context pi4j;

  public Monitor() throws ProviderException {
    pi4j = Pi4J.newAutoContext();
  }

  public void monitorTemp() {
    try (final var aToD = new PiSpi8a1Plus(pi4j, 0, thermistor, 7)) {
      while (true) {
        final var val = aToD.getTemperature();
        System.out.println(val);
        try {
          Thread.sleep(500);
        } catch (final InterruptedException ignored) {
          break;
        }
      }
    }
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
