<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="https://bedework.org/jsp/taglib/bedework-tags" prefix="bwt" %>
<%@ taglib uri="https://douglm.org/jsp/taglib/hwmon" prefix="hwm" %>

<zone>
  <hwm:emitInput name="zone" />
  <hwm:emitTemps name="zone" property="temps" />

  <c:if test="${not empty zone.subZones}">
    <subzones>
      <c:forEach var="sz" items="${zone.subZones}">
        <c:set var="subzone" value="${sz}" scope="session" />
        <jsp:include page="/docs/emitSubzone.jsp" />
      </c:forEach>
    </subzones>
  </c:if>
</zone>

