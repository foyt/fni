<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="refresh" content="0"/>
    <title>SystemUtils - Reindex</title>
  </head>
  <body>
    <div>Remaining classes</div>
    <div>
      <c:forEach var="remainingClass" items="${remainingClasses}">
        <div>${remainingClass}</div>
      </c:forEach>
    </div>
  </body>
</html>