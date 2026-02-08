package org.douglm.heatingMonitor.common.status;

/**
 * User: mike Date: 4/6/25 Time: 23:32
 */
public interface SwitchedEntity {
  String getName();

  /**
   *
   * @return time in millis since last change
   */
  long getLastChange();

  void setLastChange(long val);

  /**
   * @return time in millis the switch is on
   */
  long getRunningTime();

  /** For json.
   *
   * @param val running time in millis
   */
  @SuppressWarnings("unused")
  void setRunningTime(long val);

  void incRunningTime(long val);

  /**
   * @return time in millis the switch is off
   */
  long getOffTime();

  /** For json.
   *
   * @param val off time in millis
   */
  @SuppressWarnings("unused")
  void setOffTime(long val);

  void incOffTime(long val);

  /**
   *
   * @return status last time monitor checked.
   */
  boolean getSwitchValue();

  void setSwitchValue(boolean val);

  default void updateTimes() {
    final var now = System.currentTimeMillis();
    if (getSwitchValue()) {
      incRunningTime(now - getLastChange());
    } else {
      incOffTime(now - getLastChange());
    }
    setLastChange(now);
  }
}
