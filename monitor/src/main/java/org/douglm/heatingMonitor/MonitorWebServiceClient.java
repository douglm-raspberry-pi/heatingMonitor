/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import org.bedework.base.response.GetEntityResponse;
import org.bedework.base.response.Response;
import org.bedework.util.http.Headers;
import org.bedework.util.http.HttpUtil;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.douglm.heatingMonitor.common.MonitorException;
import org.douglm.heatingMonitor.common.config.MonitorConfig;
import org.douglm.heatingMonitor.common.status.MonitorStatus;

import java.net.URI;

/**
 * User: mike Date: 1/21/26 Time: 17:40
 */
public class MonitorWebServiceClient implements Logged {
  private final String serverUrl;

  // TODO - this needs to be somewhere it gets shut down properly
  private static CloseableHttpClient http;
  private static final Headers defaultHeaders;

  private ObjectMapper objectMapper;

  static {
    defaultHeaders = new Headers();
    defaultHeaders.add("Accept", "application/json");
  }

  public MonitorWebServiceClient(final String serverUrl) {
    this.serverUrl = serverUrl;
  }

  public GetEntityResponse<MonitorConfig> readConfig() {
    final var res = new GetEntityResponse<MonitorConfig>();

    // Read monitor config.
    try {
      final CloseableHttpClient cl = getClient();

      try (final CloseableHttpResponse hresp =
                   HttpUtil.doGet(cl,
                                  new URI(serverUrl + "/config"),
                                  this::getDefaultHeaders,
                                  null)) {   // content type
        final int status = HttpUtil.getStatus(hresp);

        if ((status / 100) != 2) {
          return res.error("Unable to fetch monitor configuration");
        }

        final var monitorConfig = getMapper()
                .readValue(hresp.getEntity().getContent(),
                           MonitorConfig.class);
        debug(monitorConfig.toString());
        res.setEntity(monitorConfig);
      }
    } catch(final Throwable t) {
      return res.error(t);
    }

    return res;
  }

  public Response<?> postStatus(final MonitorStatus mstatus) {
    final var res = new Response<>();

    try {
      final CloseableHttpClient cl = getClient();
      final var json = getMapper().writeValueAsString(mstatus);

      try (final CloseableHttpResponse hresp =
                   HttpUtil.doPost(cl,
                                  new URI(serverUrl + "/postStatus"),
                                  this::getDefaultHeaders,
                                  "application/json",
                                  json)) {
        final int status = HttpUtil.getStatus(hresp);

        if ((status / 100) != 2) {
          return res.error("Unable to post monitor status");
        }
      }
    } catch(final Throwable t) {
      return res.error(t);
    }

    return res;
  }

  private ObjectMapper getMapper() {
    if (objectMapper != null) {
      return objectMapper;
    }

    objectMapper = new ObjectMapper();

    return objectMapper;
  }

  private Headers getDefaultHeaders() {
    return defaultHeaders;
  }

  private CloseableHttpClient getClient() {
    if (http == null) {
      try {
        http  = HttpClients.createDefault();
      } catch (final Throwable t) {
        error(t);
        throw new MonitorException(t);
      }
    }

    return http;
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
}
