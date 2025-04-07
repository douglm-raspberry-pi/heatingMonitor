package org.douglm.heatingMonitor.status;

/**
 * User: mike Date: 4/6/25 Time: 23:32
 */
public interface SwitchedEntity {
  /**
   *
   * @return time in millis since last change
   */
  long getLastChange();

  void setLastChange(long val);

  long getRunningTime();

  void incRunningTime(long val);

  default void updateRunningTime() {
    final var now = System.currentTimeMillis();
    incRunningTime(now - getLastChange());
    setLastChange(now);
  }

  default long getRunningTimeMinutes() {
    return getRunningTime() / 60000;
  }

  default int getRunningTimePercent(final long start) {
    final var sinceStart =
            System.currentTimeMillis() - start;
    return (int)(100 * getRunningTime() / sinceStart);
  }
}
