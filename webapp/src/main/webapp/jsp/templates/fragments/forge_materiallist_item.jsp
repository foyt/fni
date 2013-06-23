<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="editLinkTooltip"><fmt:message key="forge.indexPage.materialsListEditTooltip"/></c:set>
<c:set var="moveLinkTooltip"><fmt:message key="forge.indexPage.materialsListMoveTooltip"/></c:set>
<c:set var="shareLinkTooltip"><fmt:message key="forge.indexPage.materialsListShareTooltip"/></c:set>
<c:set var="deleteLinkTooltip"><fmt:message key="forge.indexPage.materialsListDeleteTooltip"/></c:set>
<c:set var="printToPdfLinkTooltip"><fmt:message key="forge.indexPage.materialsListPrintToPdfTooltip"/></c:set>
<c:set var="publishLinkTooltip"><fmt:message key="forge.indexPage.materialsListPublishAsArticleTooltip"/></c:set>
<c:set var="unpublishLinkTooltip"><fmt:message key="forge.indexPage.materialsListUnpublishAsArticleTooltip"/></c:set>

<c:choose>
  <c:when test="${!empty(param.classes)}">
    <c:set var="classes" value="forgeWorkspaceMaterial ${param.classes}"/>
  </c:when>
  <c:otherwise>
    <c:set var="classes" value="forgeWorkspaceMaterial"/>
  </c:otherwise>
</c:choose>

<li class="${classes}">
  <input type="hidden" name="materialId" value="${material.id}"/>
  <input type="hidden" name="materialType" value="${material.type}"/>
  <input type="hidden" name="materialArchetype" value="${material.archetype}"/>
  <input type="hidden" name="materialPath" value="${material.path}"/>
  <c:choose>
    <c:when test="${material.starred}">
      <span class="forgeWorkspaceMaterialStar forgeWorkspaceMaterialStarred">&#9733;</span>
    </c:when>
    <c:otherwise>
      <span class="forgeWorkspaceMaterialStar">&#9733;</span>
    </c:otherwise>
  </c:choose>
  
  <span class="forgeWorkspaceMaterialIcon ${material.archetype} ${material.archetype}-${material.type}"></span>

  <span class="forgeWorkspaceMaterialDate">
    <fmt:formatDate value="${material.modified}" type="date" dateStyle="SHORT"/>
  </span>
  <span class="forgeWorkspaceMaterialTitle">
    ${fn:escapeXml(material.title)}
  </span>

  <c:if test="${param.showParentFolder eq true}">
    <c:if test="${material.parentId ne null}">
      <span class="forgeWorkspaceMaterialParentContainer">
        <a href="javascript:void(null)" class="forgeWorkspaceMaterialParentLink">${material.parentTitle}</a>
        <input type="hidden" name="parentId" value="${material.parentId}"/>
      </span>
    </c:if>
  </c:if>
  
  <span class="forgeWorkspaceMaterialEditor">
    ${fn:escapeXml(material.editorName)}
  </span>
  <div class="forgeWorkspaceMaterialActions">
    <c:if test="${material.editable}">
      <a href="javascript:void(null)" class="forgeWorkspaceMaterialEditLink" title="${editLinkTooltip}"></a>
    </c:if>
    <c:if test="${material.deletable}">
      <a href="javascript:void(null)" class="forgeWorkspaceMaterialDeleteLink" title="${deleteLinkTooltip}"></a>
    </c:if>
    <c:if test="${material.printableAsPdf}">
      <a href="javascript:void(null)" class="forgeWorkspaceMaterialPrintToPdfLink" title="${printToPdfLinkTooltip}"></a>
    </c:if>
    <c:if test="${material.movable}">
      <a href="javascript:void(null)" class="forgeWorkspaceMaterialMoveLink" title="${moveLinkTooltip}"></a>
    </c:if>
    <c:if test="${material.shareable}">
      <a href="javascript:void(null)" class="forgeWorkspaceMaterialShareLink" title="${shareLinkTooltip}"></a>
    </c:if>
    <c:if test="${(loggedUser.role eq 'ADMINISTRATOR') and (material.type eq 'DOCUMENT')}">
      <c:choose>
        <c:when test="${material.articleId ne null}">
          <a href="javascript:void(null)" class="forgeWorkspaceMaterialPublishLink" title="${publishLinkTooltip}" style="display: none"></a>
          <a href="javascript:void(null)" class="forgeWorkspaceMaterialUnpublishLink" title="${unpublishLinkTooltip}"></a>
        </c:when>
        <c:otherwise>
          <a href="javascript:void(null)" class="forgeWorkspaceMaterialPublishLink" title="${publishLinkTooltip}"></a>
          <a href="javascript:void(null)" class="forgeWorkspaceMaterialUnpublishLink" title="${unpublishLinkTooltip}" style="display: none"></a>
        </c:otherwise>
      </c:choose>
    </c:if>
  </div>
</li>