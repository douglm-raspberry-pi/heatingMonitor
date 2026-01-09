/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.web;

import java.util.ArrayList;
import java.util.Collection;

/** Circular list which can be traversed backwards or forwards.
 * The head is considered the starting point and the point of
 * insertion.
 * <p>
 *   This list allows us to maintain a fixed size list of log
 *   entries for example, with the newest at the head. Adding a
 *   new value moves the head back one to the oldest and replaces
 *   the value.
 *
 * User: mike Date: 12/17/25 Time: 23:13
 */
public class CircularList<T> {
  private CircularListElement<T> head;
  private int size;
  private final int maxSize;

  public static class CircularListElement<T> {
    CircularListElement<T> next;
    CircularListElement<T> prev;
    private T value;

    public CircularListElement<T> next() {
      return next;
    }

    public CircularListElement<T> prev() {
      return prev;
    }

    public T value() {
      return value;
    }
  }

  public CircularList(final int maxSize) {
    this.maxSize = maxSize;
  }

  public CircularListElement<T> head() {
    return head;
  }

  public Collection<T> allForward() {
    final var res = new ArrayList<T>();
    if (head == null) {
      return res;
    }

    var p = head;
    do {
      res.add(p.value());
      p = p.next;
    } while (p != head);

    return res;
  }

  public void add(final T val) {
    final var newElement = new CircularListElement<T>();
    newElement.value = val;

    if (head == null) {
      newElement.next = newElement;
      newElement.prev = newElement;
    } else {
      newElement.next = head;
      newElement.prev = head.prev;
      head.prev.next = newElement;
      head.prev = newElement;
    }

    head = newElement;
    size++;
  }

  /** New element always goes to the head. If at maxsize advance the
   * head and replace the value
   *
   * @param val to add to the list
   */
  public void addValue(final T val) {
    if (size < maxSize) {
      add(val);
    } else {
      head = head.prev;
      head.value = val;
    }

  }
}
