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
    outputJson(resp, null, null, getMethodBase().getConfig());
  }
}
