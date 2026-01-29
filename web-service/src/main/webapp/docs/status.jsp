<%@ page contentType="text/xml;charset=UTF-8" buffer="none" language="java" %><?xml version="1.0" encoding="UTF-8"?>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix="c" %>

<hsmon>
    <%@ include file="/docs/header.jsp" %>

      <%
try {
%>
  <page>status</page>
  <c:set var="status" value="${globals.status}" />
  <c:if test="${not empty status}">
  <status>
    <startTime><c:out value="${status.startTime}"/></startTime>
    <c:if test="${not empty status.zones}">
      <zones>
      <c:forEach var="z" items="${status.zones}">
        <c:set var="zone" value="${z}" scope="session" />
        <jsp:include page="/docs/emitZone.jsp" />
      </c:forEach>
      </zones>
    </c:if>
    <c:if test="${not empty status.sensors}">
      <sensors>
      <c:forEach var="s" items="${status.sensors}">
        <c:set var="sensor" value="${s}" scope="session" />
        <jsp:include page="/docs/emitSensor.jsp" />
      </c:forEach>
      </sensors>
    </c:if>
  </status>
  </c:if>

<%
} catch (Throwable t) {
  t.printStackTrace();
}
%>
</hsmon>