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

package org.douglm.heatingMonitor.jsp.taglib;

import org.bedework.util.misc.Util;
import org.bedework.util.servlet.jsp.NameScopePropertyTag;

import org.douglm.heatingMonitor.common.status.Temperature;

import jakarta.servlet.jsp.JspWriter;

import java.text.DecimalFormat;
import java.util.Collection;

import static org.bedework.util.servlet.jsp.BwTagUtilCommon.closeTag;
import static org.bedework.util.servlet.jsp.BwTagUtilCommon.openTag;
import static org.bedework.util.servlet.jsp.BwTagUtilCommon.outTagged;
import static org.bedework.util.servlet.jsp.BwTagUtilCommon.pushIndent;

/** Emit input values.
 *
 * @author Mike Douglass
 */
public class EmitTempsTag extends NameScopePropertyTag {
  /** Optional attribute: name of outer tag */
  private String tagName;

  /** Optional attribute: for those who like tidy xml
   * If specified we add the value after a new line. */
  private String indent = null;

  /** Optional attribute: true for all fields. */
  private boolean full = true;

  /**
   * Constructor
   */
  public EmitTempsTag() {
  }

  private static final DecimalFormat df =
          new DecimalFormat("#.###");

  /** Called at end of Tag
   *
   * @return int      either EVAL_PAGE or SKIP_PAGE
   */
  public int doEndTag() {
    try {
      /* Try to retrieve the value */
      final var val = (Collection<Temperature>)getObject(false);

      final JspWriter out = pageContext.getOut();

      outTemps(out, getIndent(), getTagName(),
                             getFull(), val);
    } finally {
      tagName = null; // reset for next time.
      indent = null;
    }

    return EVAL_PAGE;
  }

  /**
   *
   * @param out writer
   * @param indent starting indent level or null
   * @param temps the temperatures
   */
  public static void outTemps(final JspWriter out,
                              final String indent,
                              final String tagName,
                              final boolean full,
                              final Collection<Temperature> temps) {
    final var curIndent = pushIndent(indent);

    var tag = tagName;
    if (tag == null) {
      tag = "temps";
    }
    // Assume indented for first
    openTag(out, null, tag, true);

    if (!Util.isEmpty(temps)) {
      for (final var temp: temps) {
        outTemp(out, curIndent, "temp", full, temp);
      }
    }

    closeTag(out, curIndent, tag);
  }

  /** Output with surrounding tag
   *
   * @param out writer
   * @param indent starting indent level or null
   * @param val the Temperature object
   */
  public static void outTemp(final JspWriter out,
                             final String indent,
                             final String tagName,
                             final boolean full,
                             final Temperature val) {
    final var curIndent = pushIndent(indent);

    if (tagName != null) {
      // Assume indented for first
      openTag(out, null, tagName, true);
    }

    outTagged(out, curIndent, "name", val.getName());
    outTagged(out, curIndent, "degreesCelsius",
              String.valueOf(
                      df.format(val.getDegreesCelsius())));
    outTagged(out, curIndent, "degreesFahrenheit",
              String.valueOf(
                      df.format(((val.getDegreesCelsius() * 9) /
                                         5) + 32)));

    if (tagName != null) {
      closeTag(out, curIndent, tagName);
    }
  }

  /**
   * @param val String name
   */
  public void setTagName(final String val) {
    tagName = val;
  }

  /**
   * @return String name
   */
  public String getTagName() {
    return tagName;
  }

  /**
   * @param val String indent
   */
  public void setIndent(final String val) {
    indent = val;
  }

  /**
   * @return  String indent
   */
  public String getIndent() {
    return indent;
  }

  /**
   * @param val true for all fields
   */
  public void setFull(final boolean val) {
    full = val;
  }

  /**
   * @return  true for all fields
   */
  public boolean getFull() {
    return full;
  }
}
