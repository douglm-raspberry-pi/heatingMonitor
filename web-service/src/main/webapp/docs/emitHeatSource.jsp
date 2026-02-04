<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<heatSource>
  <name><c:out value="${heatSource.name}"/></name>
  <lastChange><c:out value="${heatSource.lastChange}"/></lastChange>
  <runningTime><c:out value="${heatSource.runningTime}"/></runningTime>
  <switchValue><c:out value="${heatSource.switchValue}"/></switchValue>

  <c:if test="${not empty heatSource.temps}">
    <temps>
    <c:forEach var="temp" items="${heatSource.temps}">
      <temp>
        <name><c:out value="${temp.name}"/></name>
        <degreesCelsius><c:out value="${temp.degreesCelsius}"/></degreesCelsius>
      </temp>
    </c:forEach>
    </temps>
  </c:if>
  <c:if test="${not empty heatSource.zones}">
    <zones>
      <c:forEach var="z" items="${heatSource.zones}">
        <c:set var="zone" value="${z}" scope="session" />
        <jsp:include page="/docs/emitZone.jsp" />
      </c:forEach>
    </zones>
  </c:if>
</heatSource>

