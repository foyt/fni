<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
  <c:when test="${empty(param.id)}">
    <jsp:useBean scope="page" class="fi.foyt.fni.utils.jsp.UUID" id="uuid"/>
    <c:set var="fieldId" value="form-password-field-${uuid.uniqueId}"/>  
  </c:when>
  <c:otherwise>
    <c:set var="fieldId" value="${param.id}"/>
  </c:otherwise>
</c:choose>

<div class="formField formTextField">
  <div class="formFieldLabelContainer">
    <c:choose>
      <c:when test="${!empty(param.labelLocale)}">
        <label for="${fieldId}"> <fmt:message key="${param.labelLocale}"></fmt:message>
        </label>
      </c:when>
      <c:otherwise>
        <label for="${fieldId}">${param.labelText}</label>
      </c:otherwise>
    </c:choose>
  </div>
  <div class="formFieldEditorContainer">
    <input id="${fieldId}" type="password" name="${param.name}" class="${param.classes}" />
  </div>
</div>