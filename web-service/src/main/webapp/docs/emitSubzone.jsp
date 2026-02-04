<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<subzone>
  <name><c:out value="${subzone.name}"/></name>
  <lastChange><c:out value="${subzone.lastChange}"/></lastChange>
  <runningTime><c:out value="${subzone.runningTime}"/></runningTime>
  <switchValue><c:out value="${subzone.switchValue}"/></switchValue>

  <c:if test="${not empty subzone.temps}">
    <temps>
    <c:forEach var="temp" items="${subzone.temps}">
      <temp>
        <name><c:out value="${temp.name}"/></name>
        <degreesCelsius><c:out value="${temp.degreesCelsius}"/></degreesCelsius>
      </temp>
    </c:forEach>
    </temps>
  </c:if>
</subzone>

