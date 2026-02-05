/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.util.misc.AbstractProcessorThread;

import org.douglm.heatingMonitor.common.MonitorWebServiceClient;
import org.douglm.heatingMonitor.common.config.MonitorConfig;
import org.douglm.heatingMonitor.common.status.MonitorStatus;

/**
 * User: mike Date: 4/6/25 Time: 13:08
 */
public class MonitorThread extends AbstractProcessorThread {
  private final MonitorStatus status;
  private final MonitorConfig config;
  private final MonitorWebServiceClient webClient;

  /**
   * @param status for the monitor
   */
  public MonitorThread(final MonitorStatus status,
                       final MonitorWebServiceClient webClient) {
    super("Monitor");

    this.status = status;
    this.config = status.getConfig();
    this.webClient = webClient;
  }

  @Override
  public void runInit() {
    setRunning(Boolean.TRUE);
  }

  @Override
  public void end(final String msg) {

  }

  @Override
  public void runProcess() throws Throwable {
    while (getRunning()) {
      synchronized (this) {
        status.setTimestamp(System.currentTimeMillis());
        final var postRes = webClient.postStatus(status);
        if (!postRes.isOk()) {
          warn(postRes.toString());
        }

        try {
          this.wait(config.getInputsWaitTime());
        } catch (final InterruptedException ignored) {
          setRunning(false);
          break;
        }
      }
    }
  }

  @Override
  public void close() {

  }
}
