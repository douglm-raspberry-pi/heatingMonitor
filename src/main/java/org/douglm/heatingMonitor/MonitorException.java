/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

/**
 * User: mike Date: 4/6/25 Time: 13:50
 */
public class MonitorException extends RuntimeException {
  public MonitorException(final String message) {
    super(message);
  }

  public MonitorException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public MonitorException(final Throwable cause) {
    super(cause);
  }
}
