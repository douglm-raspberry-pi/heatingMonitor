/* ********************************************************************
    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.
*/
package org.douglm.heatingMonitor.web;

import org.bedework.util.misc.Util;
import org.bedework.util.servlet.MethodBase;
import org.bedework.util.servlet.MethodHelper;
import org.bedework.util.servlet.ReqUtil;
import org.bedework.util.servlet.config.AppInfo;
import org.bedework.util.servlet.filters.PresentationState;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.douglm.heatingMonitor.common.MonitorException;
import org.douglm.heatingMonitor.common.config.MonitorConfig;
import org.douglm.heatingMonitor.common.status.MonitorStatus;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collection;
import java.util.List;

import static org.douglm.heatingMonitor.common.util.MonitorUtil.readConfig;

/** Base class for all event registration servlet methods.
   Subclasses should provide a static block registering
   the helper classes.
   e.g.
   <pre>
   static {
     registerHelper("listForms",
                    ProcessListForms.class);
     registerHelper("reginfo",
                    ProcessRegInfo.class);
     registerHelper("selectForms",
                    ProcessSelectForms.class);
   }
   </pre>
 */
public abstract class HeatingMonitorMethodBase extends MethodBase {
  private ObjectMapper objectMapper;
  private MonitorConfig config;
  private WebGlobals webGlobals;

  // We'll build a ring of these
  private static final CircularList<MonitorStatus> statuses =
          new CircularList<>(100);

  @Override
  public boolean beforeMethod(final HttpServletRequest req,
                              final HttpServletResponse resp) {
    if (!super.beforeMethod(req, resp)) {
      return false;
    }

    if (debug()) {
      debug("beforeMethod {}", this);
    }

    webGlobals = (WebGlobals)rutil.getSessionAttr(WebGlobals.webGlobalsAttrName);
    if (webGlobals == null) {
      if (debug()) {
        debug("No WebGlobals attribute found in request - creating");
      }
      webGlobals = newWebGlobals();
      rutil.setSessionAttr(WebGlobals.webGlobalsAttrName,
                           webGlobals);
    } else if (debug()) {
      debug("WebGlobals attribute found in request");
    }

    webGlobals.reset(getRequest());

    final var formName = rutil.getReqPar("formName");
    if (formName != null) {
      webGlobals.setFormName(validName(formName));
    }

    final var href = rutil.getReqPar("href");
    if (href != null) {
      webGlobals.setHref(href);
    }

    final var ps = getPresentationState();
    final MethodHelper helper;
    try {
      helper = getMethodHelper(req, resp);
    } catch (final ServletException unused) {
      return false;
    }
    final var hinfo = helper.getHelperInfo();
    final var xslPar = hinfo.getParameter("xsl");
    final var doXsl = ((xslPar != null) &&
                               ("true".equals(xslPar.getValue())));
    final var snPar = hinfo.getParameter("skinName");
    final String skinName;
    if (snPar != null) {
      skinName = snPar.getValue();
    } else {
      skinName = null;
    }

    ps.setNoXSLT(!doXsl);
    if (skinName != null) {
      ps.setSkinName(skinName);
    }

    ps.reinit(req);

    if (ps.getAppRoot() == null) {
      ps.setAppRoot(getContext().getInitParameter("appRoot"));
      ps.setBrowserResourceRoot(getContext().getInitParameter("browserResourceRoot"));

      // Set the default skin

      if (ps.getSkinName() == null) {
        // No skin name supplied - use the default
        ps.setSkinName(getContext().getInitParameter("skinName"));
        ps.setSkinNameSticky(true);
      }
    }

    return true;
  }

  @Override
  public void doMethod(final HttpServletRequest req,
                       final HttpServletResponse resp) throws ServletException {
    try {
      final var helper = getMethodHelper(req, resp);
      final List<String> resourceUri = getResourceUri(req);

      if (Util.isEmpty(resourceUri)) {
        throw new ServletException("Bad resource url - no path specified");
      }

      debug("About to call helper execute method");
      helper.execute(resourceUri, req, resp, this);
    } catch (final ServletException se) {
      throw se;
    } catch(final Throwable t) {
      throw new ServletException(t);
    }
  }

  /** Override to create a subclassed object
   *
   * @return new web globals object
   */
  public WebGlobals newWebGlobals() {
    return new WebGlobals();
  }

  public WebGlobals getWebGlobals() {
    return webGlobals;
  }

  @Override
  public ReqUtil newReqUtil(final HttpServletRequest req,
                            final HttpServletResponse resp) {
    return new Request(req, resp);
  }

  public Request getRequest() {
    return (Request)rutil;
  }

  /** Called at each request
   *
   * @param dumpContent true to wrap and dump content for debugging
   */
  public void init(final ServletContext context,
                   final boolean dumpContent,
                   final String methodName,
                   final AppInfo appInfo) {
    super.init(context, dumpContent, methodName, appInfo);
  }

  public MonitorConfig getConfig() {
    if (config != null) {
      return config;
    }

    final var hcresp = readConfig("monitor.yaml",
                                  MonitorConfig.class);
    if (!hcresp.isOk()) {
      throw new MonitorException(hcresp.getException());
    }

    config = hcresp.getEntity();

    return config;
  }
  
  public ObjectMapper getMapper() {
    if (objectMapper != null) {
      return objectMapper;
    }

    objectMapper = new ObjectMapper();
    return objectMapper;
  }

  /* name validation. form, field and group names must all be
     valid json names.
   */
  public static String validName(final String name) {
    if ((name == null) || (name.isEmpty())) {
      throw new MonitorException(name);
    }

    if (!Character.isLetter(name.charAt(0))) {
      throw new MonitorException(name);
    }

    for (int i = 1; i < name.length(); i++) {
      final char ch = name.charAt(i);

      if (Character.isLetterOrDigit(ch)) {
        continue;
      }

      if ((ch == '-') || (ch == '_')) {
        continue;
      }

      throw new MonitorException(name);
    }

    return name;
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

  /* ========================================================
   *               Presentation state methods
   * ======================================================== */

  /**
   * @return PresentationState
   */
  public PresentationState getPresentationState() {
    final String attrName = PresentationState.presentationAttrName;

    final Object o = getRequest().getRequestAttr(attrName);
    final PresentationState ps;

    if (!(o instanceof PresentationState)) {
      ps = new PresentationState().reinit(getRequest().getRequest());
    } else {
      ps = (PresentationState)o;
    }

    getRequest().setRequestAttr(attrName, ps);

    return  ps;
  }
}

