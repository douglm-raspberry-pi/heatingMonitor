package org.douglm.heatingMonitor.web.gethelpers;

import org.douglm.heatingMonitor.web.HeatingMonitorMethodHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class ProcessGetConfig
        extends HeatingMonitorMethodHelper {
  @Override
  public void hmProcess(final List<String> resourceUri,
                        final HttpServletRequest req,
                        final HttpServletResponse resp) {
    try {
      outputJson(resp, null, null, getMethodBase().getConfig());
    } catch (final Throwable t) {
      sendJsonError(resp, t.getMessage());
    }
  }
}
