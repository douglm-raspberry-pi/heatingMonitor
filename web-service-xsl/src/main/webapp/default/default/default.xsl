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
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:hmon="http://douglm.org/xslt/hwmon">
<xsl:output
  method="xml"
  indent="no"
  media-type="text/html"
  doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
  doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
  omit-xml-declaration="yes"/>

  <!-- DEFINE INCLUDES -->
  <xsl:include href="../../functions.xsl" />
  <xsl:include href="../../globals.xsl" />
  <xsl:include href="../strings.xsl" />
  <xsl:include href="../localeSettings.xsl" />

  <!-- DEFAULT THEME NAME -->
  <!-- to change the default theme, change this include -->
  <xsl:include href="../../packages/default/hsmon.xsl" />

</xsl:stylesheet>
