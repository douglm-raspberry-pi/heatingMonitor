<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<sensor>
  <name><c:out value="${sensor.name}"/></name>
  <lastChange><c:out value="${sensor.lastChange}"/></lastChange>
  <runningTime><c:out value="${sensor.runningTime}"/></runningTime>
  <switchValue><c:out value="${sensor.switchValue}"/></switchValue>
</sensor>

