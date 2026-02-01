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

  <!-- LOCALE SETTINGS -->
  <!-- A place for javascript strings and locale specific javascript overrides -->

  <!-- Set up the datepicker defaults -->
  <!-- For futher configuration, see http://docs.jquery.com/UI/Datepicker -->
  <xsl:template name="jqueryDatepickerDefaults">
    
    <!-- pull in the localization strings and defaults. -->
    <!--  
      U.S. English is the default, so there is no string file for U.S. English. 
      This code is here for consistency and clarity across localeSettings files.
    <script type="text/javascript" src="/javascript/jquery-3/jquery-ui/datepicker/lang/.datepicker-en.js">&#160;</script>
    -->

    <!-- Bedework datepicker defaults.  You can include further overrides to regionalization here. -->
    <script type="text/javascript">
      <xsl:comment>
      $.datepicker.setDefaults({
        constrainInput: true,
        dateFormat: "yy-mm-dd",
        showOn: "both",
        buttonImage: "/images/calcommon/calIcon.gif",
        buttonImageOnly: true,
        gotoCurrent: true,
        duration: ""
      });
      </xsl:comment>
    </script>
  </xsl:template>
</xsl:stylesheet>