<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
  <c:when test="${param.folded eq '1'}">
    <c:set var="contentStyle" value="display:none"/>
  </c:when>
  <c:otherwise>
    <c:set var="contentStyle" value=""/>
  </c:otherwise>
</c:choose>

<div class="forgeWorkspaceMaterialListContent" style="${contentStyle}">
  <c:set var="listVariableName" value="${param['listVariable']}"/>
  <c:set var="list" value="${requestScope[listVariableName]}"/>
  
  <c:if test="${param.showBreadcrumps eq '1'}">
    <ul class="forgeWorkspaceMaterialListBreadcrumps">
      <c:forEach var="parentFolder" items="${parentFolders}">
        <c:set value="${parentFolder}" var="material" scope="request"></c:set>
        <jsp:include page="forge_materiallist_item.jsp">
          <jsp:param name="classes" value="parentFolder"/>
        </jsp:include>
      </c:forEach>
    </ul>
  </c:if>
  
  <c:choose>
    <c:when test="${(fn:length(list) > 0) or (param.forceAddList eq '1')}">
      <ul class="forgeWorkspaceMaterialListItems">
        <c:forEach items="${list}" var="materialItem">
          <c:set value="${materialItem}" var="material" scope="request"></c:set>
          <jsp:include page="forge_materiallist_item.jsp">
            <jsp:param value="${param.showParentFolder}" name="showParentFolder"/>
          </jsp:include>
        </c:forEach>
      </ul>
      
      <c:if test="${param.showMoreLink eq '1'}">
        <a class="forgeWorkspaceMaterialListShowMoreLink" href="javascript:void(null);"><fmt:message key="${param.showMoreLocale}"/></a>
      </c:if>
      
      <c:if test="${param.forceAddMessage eq '1'}">
        <div class="forgeWorkspaceMaterialListMessage"><fmt:message key="${param.noItemsMessageLocale}"/></div>
      </c:if>
    </c:when>
    <c:otherwise>
      <div class="forgeWorkspaceMaterialListMessage"><fmt:message key="${param.noItemsMessageLocale}"/></div>
    </c:otherwise>
  </c:choose>
</div>