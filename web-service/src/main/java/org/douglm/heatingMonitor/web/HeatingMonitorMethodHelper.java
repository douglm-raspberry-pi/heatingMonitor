package org.douglm.heatingMonitor.web;

import org.bedework.util.servlet.MethodHelper;

import org.douglm.heatingMonitor.common.status.MonitorStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collection;
import java.util.List;

public abstract class HeatingMonitorMethodHelper extends MethodHelper {
  protected HeatingMonitorMethodBase hmb;
  protected WebGlobals globals;

  // We'll build a ring of these
  private static final CircularList<MonitorStatus> statuses =
          new CircularList<>(100);

  public abstract void hmProcess(List<String> resourceUri,
                                 HttpServletRequest req,
                                 HttpServletResponse resp);
  @Override
  public void process(final List<String> resourceUri,
                      final HttpServletRequest req,
                      final HttpServletResponse resp) {
    hmb = (HeatingMonitorMethodBase)super.getMethodBase();
    globals = hmb.getWebGlobals();
    hmProcess(resourceUri, req, resp);
  }

  protected HeatingMonitorMethodBase getMethodBase() {
    return hmb;
  }

  protected Request getRequest() {
    return hmb.getRequest();
  }

  public boolean requireHref() {
    if (globals.getHref() == null) {
      errorReturn("Bad resource url - no href specified");
      return false;
    }
    return true;
  }

  protected void errorReturn(final Throwable t) {
    globals.setMessage(t.getLocalizedMessage());
    if (debug()) {
      debug("Exception- error return " + t.getLocalizedMessage());
    }
    forward("error");
  }

  protected void errorReturn(final String msg) {
    globals.setMessage(msg);
    if (debug()) {
      debug("Error return " + msg);
    }
    forward("error");
  }

  public MonitorStatus getCurrentStatus() {
    final var current = statuses.head();
    if (current == null) {
      return null;
    }

    return current.value();
  }

  public Collection<MonitorStatus> getAllStatuses() {
    return statuses.allForward();
  }

  public void addStatus(final MonitorStatus val) {
    if (val == null) {
      warn("Attempt to add null status");
      return;
    }
    statuses.addValue(val);
    if (debug()) {
      debug("Statuses contains {} values", statuses.size());
    }
  }
}
