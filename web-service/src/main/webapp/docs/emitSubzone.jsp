<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="https://douglm.org/jsp/taglib/hwmon" prefix="hwm" %>

<subzone>
  <hwm:emitInput name="subzone" />
  <hwm:emitTemps name="subzone" property="temps" />
</subzone>

