<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="formField formSubmitField">
  <c:choose>
    <c:when test="${!empty(param.textLocale)}">
      <input type="submit" value="<fmt:message key="${param.textLocale}"/>" name="${param.name}" class="${param.classes}"/>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${param.disabled eq 'true'}">
          <input type="submit" value="${param.text}" name="${param.name}" class="${param.classes}" disabled="disabled"/>
        </c:when>
        <c:otherwise>
          <input type="submit" value="${param.text}" name="${param.name}" class="${param.classes}"/>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
</div>