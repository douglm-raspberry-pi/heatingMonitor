/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.webService.tester;

import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;
import org.bedework.util.misc.AbstractProcessorThread;

import org.douglm.heatingMonitor.Monitor;
import org.douglm.heatingMonitor.common.status.MonitorStatus;

/**
 * User: mike Date: 1/25/26 Time: 00:05
 */
public class RunTest extends AbstractProcessorThread
        implements Logged {
  private MonitorStatus status;
  private Monitor monitor;

  public RunTest() {
    super("RunTest");
  }

  public RunTest init() {
    monitor = new Monitor().init();
    status = monitor.getStatus();

    return this;
  }

  @Override
  public void runInit() {
    setRunning(Boolean.TRUE);
  }

  @Override
  public void end(final String s) {

  }

  @Override
  public void runProcess() {
    for (final var hs: status.getHeatSources()) {
      if (hs.getZones() != null) {
        for (final var z: hs.getZones()) {
          z.incRunningTime(500);

          if (z.getSubZones() != null) {
            for (final var sz: z.getSubZones()) {
              sz.incRunningTime(250);
            }
          }
        }
      }
    }
    final var webClient = monitor.getWebClient();

    while (getRunning()) {
      final var postRes = webClient.postStatus(status);
      if (!postRes.isOk()) {
        warn(postRes.toString());
      }

      synchronized (this) {
        try {
          this.wait(5000);
        } catch (final InterruptedException ignored) {
          setRunning(false);
          break;
        }
      }
    } // while
  }

  @Override
  public void close() {

  }

  /* ====================================================
   *                   Logged methods
   * ==================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }

  public static void main(final String[] args) {
    final var rt = new RunTest().init();

    rt.start();
  }
}
