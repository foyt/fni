<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
  <c:when test="${param.folded eq '1'}">
    <c:set var="listClasses" value="forgeWorkspaceMaterialList forgeWorkspaceMaterialListFolded"/>
  </c:when>
  <c:otherwise>
    <c:set var="listClasses" value="forgeWorkspaceMaterialList"/>
  </c:otherwise>
</c:choose>

<c:choose>
  <c:when test="${param.hidden eq '1'}">
    <c:set var="listStyle" value="display: none"/>
  </c:when>
  <c:otherwise>
    <c:set var="listStyle" value=""/>
  </c:otherwise>
</c:choose>

<div class="${listClasses}" id="${param.listId}" style="${listStyle}">
  <h2><fmt:message key="${param.titleLocale}"/></h2>
  <jsp:include page="forge_materiallist_content.jsp">
    <jsp:param name="folded" value="${param.folded}" />
    <jsp:param name="forceAddList" value="${param.forceAddList}" />
    <jsp:param name="forceAddMessage" value="${param.forceAddMessage}" />
    <jsp:param name="listVariable" value="${param.listVariable}" />
    <jsp:param name="showMoreLink" value="${param.showMoreLink}" />
    <jsp:param name="showMoreLocale" value="${param.showMoreLocale}" />
    <jsp:param name="noItemsMessageLocale" value="${param.noItemsMessageLocale}" />
    <jsp:param name="showParentFolder" value="${param.showParentFolder}"/>
  </jsp:include>
</div>