<?xml version="1.0" encoding="UTF-8"?>
<!--
    This file is licensed under the Apache License,
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

  <xsl:template match="heatSource" mode="heatSourceList">
    <div id="heatSource">
      <strong><xsl:value-of select="name"/></strong>
      <table class="zoneTable">
        <tr>
          <th>
            <xsl:copy-of select="$hmon-OnOffState"/>
          </th>
          <td>
            <xsl:value-of select="switchValue"/>
          </td>
        </tr>
        <xsl:for-each select="temps/temp">
          <tr>
            <th>
              <strong><xsl:value-of select="name"/></strong>
            </th>
            <td>
              <xsl:value-of select="degreesCelsius"/>
              <xsl:text> (</xsl:text>
              <xsl:value-of select="degreesFahrenheit"/>
              <xsl:text>)</xsl:text>
            </td>
          </tr>
        </xsl:for-each>
      </table>
      <xsl:apply-templates select="zones/zone"
                           mode="zonesList"/>
    </div>
  </xsl:template>
</xsl:stylesheet>