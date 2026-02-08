<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="https://douglm.org/jsp/taglib/hwmon" prefix="hwm" %>

<heatSource>
  <hwm:emitInput name="heatSource" />
  <hwm:emitTemps name="heatSource" property="temps" />

  <c:if test="${not empty heatSource.zones}">
    <zones>
      <c:forEach var="z" items="${heatSource.zones}">
        <c:set var="zone" value="${z}" scope="session" />
        <jsp:include page="/docs/emitZone.jsp" />
      </c:forEach>
    </zones>
  </c:if>
</heatSource>

