/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.web.posthelpers;

import org.douglm.heatingMonitor.common.status.MonitorStatus;
import org.douglm.heatingMonitor.web.HeatingMonitorMethodHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * User: mike Date: 12/20/25 Time: 23:55
 */
public class ProcessPostStatus
        extends HeatingMonitorMethodHelper {
  @Override
  public void hmProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    try {
      final var mb = getMethodBase();
      final var status = mb.getMapper()
                           .readValue(req.getReader(),
                                      MonitorStatus.class);
      mb.getLogger().info("Status {}", status);
      if (debug()) {
        debug("Status {}", status);
      }
      mb.addStatus(status);
    } catch (final Throwable t) {
      sendJsonError(resp, t.getMessage());
    }
  }
}
