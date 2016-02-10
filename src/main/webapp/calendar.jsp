<%--
  Created by IntelliJ IDEA.
  User: gregg varona
  Date: 2/7/2016
  Time: 11:57 AM
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Calendar</title>
</head>
<body>
  Events: <br/>
  <c:forEach items="${events}" var="event" varStatus="itemStatus">
    ${itemStatus.index}: ${event}
  </c:forEach>
</body>
</html>
