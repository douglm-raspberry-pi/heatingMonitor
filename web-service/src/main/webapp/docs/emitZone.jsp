<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<zone>
  <name><c:out value="${zone.name}"/></name>
  <lastChange><c:out value="${zone.lastChange}"/></lastChange>
  <runningTime><c:out value="${zone.runningTime}"/></runningTime>
  <switchValue><c:out value="${zone.switchValue}"/></switchValue>

  <c:if test="${not empty zone.temps}">
    <temps>
    <c:forEach var="temp" items="${zone.temps}">
      <temp>
        <name><c:out value="${temp.name}"/></name>
        <degreesCelsius><c:out value="${temp.degreesCelsius}"/></degreesCelsius>
      </temp>
    </c:forEach>
    </temps>
  </c:if>
  <c:if test="${not empty zone.subZones}">
    <subzones>
      <c:forEach var="sz" items="${zone.subZones}">
        <c:set var="subzone" value="${sz}" scope="session" />
        <jsp:include page="/docs/emitSubzone.jsp" />
      </c:forEach>
    </subzones>
  </c:if>
</zone>

