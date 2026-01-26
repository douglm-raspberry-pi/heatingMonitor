package org.douglm.heatingMonitor.common.status;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

  long getRunningTime();

  void incRunningTime(long val);

  default void updateRunningTime() {
    final var now = System.currentTimeMillis();
    incRunningTime(now - getLastChange());
    setLastChange(now);
  }

  @JsonIgnore
  default long getRunningTimeMinutes() {
    return getRunningTime() / 60000;
  }

  @JsonIgnore
  default int getRunningTimePercent(final long start) {
    final var sinceStart =
            System.currentTimeMillis() - start;
    return (int)(100 * getRunningTime() / sinceStart);
  }
}
