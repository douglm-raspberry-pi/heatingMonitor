<%@ page contentType="text/xml;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix="c" %>
<%
try {
%>

  <c:set var="globals" value="${sessionScope.globals}" />
  <browserResourceRoot><c:out value="${globals.browserResourceRoot}"/></browserResourceRoot>
<%
} catch (Throwable t) {
  t.printStackTrace();
}
%>

