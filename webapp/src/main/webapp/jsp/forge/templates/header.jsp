<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<jsp:include page="../../templates/header.jsp"></jsp:include>

<!-- Site's Header -->
<div id="siteHeaderWrapper" class="forgeHeaderWrapper">

  <div id="forgeMenuContainer">                 
    <ul class="forgeMenu">
          <!-- 
      <li class="forgeMenuMenuItem forgeMenuMenuItemBrowseMaterial">
        <a href="javascript:void(null)" class="forgeMenuMenuText"><fmt:message key="forge.header.menu.browseMaterialMenuText"/></a>
        <ul>
          <li class="forgeMenuSubMenuItem forgeMenuMenuItemBrowseOwnMaterial">
            <a href="javascript:void(null)"><fmt:message key="forge.header.menu.browseMaterial.browseOwnMaterialsMenuText"/></a>
          </li>
          <li class="forgeMenuSubMenuItem forgeMenuMenuItemBrowsePublicMaterial">
            <a href="javascript:void(null)"><fmt:message key="forge.header.menu.browseMaterial.browsePublicMaterialsMenuText"/></a>
          </li>
        </ul>
      </li>
          -->  
      
      <li class="forgeMenuMenuItem forgeMenuMenuItemCreateMaterial">
        <a href="javascript:void(null)" class="forgeMenuMenuText"><fmt:message key="forge.header.menu.newMaterialMenuText"/></a>
        <ul>
          <li class="forgeMenuSubMenuItem forgeMenuMenuItemCreateDocument">
            <a href="javascript:void(null)" id="forgeCreateDocumentMenuItem"><fmt:message key="forge.header.menu.newMaterial.createDocumentMenuText"/></a>
          </li>
            
          <li class="forgeMenuSubMenuItem forgeMenuMenuItemCreateVectorImage">
            <a href="javascript:void(null)" id="forgeCreateVectorImageMenuItem"><fmt:message key="forge.header.menu.newMaterial.createVectorImageMenuText"/></a>
          </li>
            <!-- 
          <li class="forgeMenuSubMenuItem forgeMenuMenuItemCreateImageGallery">
            <a href="javascript:void(null)"><fmt:message key="forge.header.menu.newMaterial.createImageGalleryMenuText"/></a>
          </li>
             -->
        </ul>
      </li>
        
      <li class="forgeMenuMenuItem forgeMenuMenuItemImportMaterial">
        <a href="javascript:void(null)" class="forgeMenuMenuText"><fmt:message key="forge.header.menu.importMaterialMenuText"/></a>
        <ul>
          <li class="forgeMenuSubMenuItem forgeMenuMenuItemUploadMaterials">
            <a href="javascript:void(null)" id="forgeUploadMaterialsMenuItem"><fmt:message key="forge.header.menu.importMaterial.uploadMaterialsMenu"/></a>
          </li>
            
          <li class="forgeMenuSubMenuItem forgeMenuMenuItemImportGoogleDocuments">
            <a href="javascript:void(null)" id="forgeImportGoogleDocumentMenuItem"><fmt:message key="forge.header.menu.importMaterial.importGoogleDocumentsMenu"/></a>
          </li>
            
          <c:if test="${connectedToDropbox ne true}">
            <li class="forgeMenuSubMenuItem forgeMenuMenuItemConnectDropbox">
              <a href="${pageContext.request.contextPath}/forge/dropboxconnect.page" id="forgeConnectDropboxMenuItem"><fmt:message key="forge.header.menu.importMaterial.connectDropboxMenu"/></a>
            </li>
          </c:if>
            
          <c:if test="${connectedToUbuntuOne ne true}">
            <li class="forgeMenuSubMenuItem forgeMenuMenuItemConnectUbuntuOne">
              <a href="${pageContext.request.contextPath}/forge/ubuntuoneconnect.page" id="forgeConnectUbuntuOneMenuItem"><fmt:message key="forge.header.menu.importMaterial.connectUbuntuOneMenu"/></a>
            </li>
          </c:if>
        </ul>
      </li>
    </ul>
  </div> 

</div>