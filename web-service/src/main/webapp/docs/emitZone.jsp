<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<zone>
  <name><c:out value="${zone.name}"/></name>
  <lastChange><c:out value="${zone.lastChange}"/></lastChange>
  <runningTime><c:out value="${zone.runningTime}"/></runningTime>
  <switchValue><c:out value="${zone.switchValue}"/></switchValue>

  <circulatorOn><c:out value="${zone.circulatorOn}"/></circulatorOn>
  <inputChanged><c:out value="${zone.inputChanged}"/></inputChanged>
  <wasChecked><c:out value="${zone.wasChecked}"/></wasChecked>

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
  <c:if test="${not empty zone.subzones}">
    <subzones>
      <c:forEach var="zone" items="${zone.subzones}">
        <jsp:include page="/docs/emitZone.jsp" />
      </c:forEach>
    </subzones>
  </c:if>
</zone>

