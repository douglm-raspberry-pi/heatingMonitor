<?xml version="1.0" encoding="UTF-8"?>
<!--
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
-->
<xsl:stylesheet
        version="3.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:include href="head.xsl" />
  <xsl:include href="header.xsl" />
  <xsl:include href="menu.xsl" />
  <xsl:include href="status.xsl" />
  <xsl:include href="zone.xsl" />

  <!--==== MAIN TEMPLATE  ====-->
  <xsl:template match="/">
    <html lang="en">
      <xsl:call-template name="head"/>
      <body>
        <div id="hsmon"><!-- main wrapper div to keep styles
        -->
          <xsl:variable name="curPage" select="/hsmon/page"/>
          <xsl:call-template name="header"/>
          <xsl:call-template name="menu1"/>
          <div id="content">
            <h1><xsl:value-of select="$curPage"/></h1>
            <xsl:choose>
              <xsl:when test="$curPage='status'">
                <xsl:call-template name="status"/>
              </xsl:when>
            </xsl:choose>
          </div>
        </div>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
