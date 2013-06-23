<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="forgeViewGoogleDocumentContainer">
  <div class="forgeViewGoogleDocumentInnerContainer">
    <c:choose>
      <c:when test="${(googleDocument.documentType eq 'DOCUMENT') or (googleDocument.documentType eq 'SPREADSHEET')}">
        <iframe src="${pageContext.request.contextPath}/${googleDocument.path}"></iframe>
      </c:when>
      <c:otherwise>
        <img src="${pageContext.request.contextPath}/${googleDocument.path}"/>
      </c:otherwise>
    </c:choose>
  </div>
</div>