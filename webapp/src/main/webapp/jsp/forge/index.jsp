<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>
      <fmt:message key="forge.indexPage.pageTitle"/>
    </title>
      
    <jsp:include page="templates/head.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/svg-edit/embedapi.js"> </script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forge/index.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forge/imageviewer.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forge/documentviewer.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forge/documenteditor.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forge/vectorimageeditor.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forge/vectorimageviewer.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forge/googledocumentviewer.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forge/importgoogledocuments.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forge/uploadmaterialsdialog.js"></script>
    <script type="text/javascript">
      var viewController = new IndexViewController();
    
      Event.observe(document, "dom:loaded", function (event) {
        viewController.setup();
      });
      
      Event.observe(window, "beforeunload", function (event) {
        viewController.destroy();
      });
    </script>
  </head>
  
  <body class="siteBodyWrapper">
    
    <jsp:include page="../templates/ads.jsp"></jsp:include>
    
    <div id="siteWrapper">
      <div id="siteLayoutWrapper">
        
        <jsp:include page="templates/header.jsp"></jsp:include>
              
        <!-- Site's Content -->
        <div id="siteContentWrapper">
          <div id="forgeWorkspace"> 
            <div id="forgeWorkspaceBackground"> </div>
            <div id="forgeWorkspaceContent"> 
              <div class="forgeWorkspaceWelcome">
                <h1><fmt:message key="forge.indexPage.welcomeTitle"/></h1>
                <p><fmt:message key="forge.indexPage.welcomeContent"/></p>
              </div>
              
              <div class="forgeWorkspaceMaterials" id="forgeMaterialLists">
                <input id="forgeMaterialSearch" type="text" value="" name=""/>
                
                <jsp:include page="/jsp/templates/fragments/forge_materiallist.jsp">
                  <jsp:param name="listId" value="forgeFoundMaterials" />
                  <jsp:param name="folded" value="0" />
                  <jsp:param name="hidden" value="1" />
                  <jsp:param name="showBreadcrumps" value="0" />
                  <jsp:param name="forceAddList" value="1" />
                  <jsp:param name="forceAddMessage" value="1" />
                  <jsp:param name="titleLocale" value="forge.indexPage.foundMaterialsTitle"/>
                  <jsp:param name="noItemsMessageLocale" value="forge.indexPage.noMaterialsFoundMessage"/>
                </jsp:include>
              
                <jsp:include page="/jsp/templates/fragments/forge_materiallist.jsp">
                  <jsp:param name="listId" value="forgeMaterialList" />
                  <jsp:param name="listVariable" value="materials" />
                  <jsp:param name="folded" value="1" />
                  <jsp:param name="showBreadcrumps" value="1" />
                  <jsp:param name="titleLocale" value="forge.indexPage.materialsTitle"/>
                  <jsp:param name="noItemsMessageLocale" value="forge.indexPage.noMaterialsMessage"/>
                </jsp:include>
              
                <jsp:include page="/jsp/templates/fragments/forge_materiallist.jsp">
                  <jsp:param name="listId" value="forgeStarredMaterialList" />
                  <jsp:param name="listVariable" value="starredMaterials" />
                  <jsp:param name="showMoreLink" value="${starredMaterialCount gt 5 ? '1' : '0'}"/>
                  <jsp:param name="titleLocale" value="forge.indexPage.starredMaterialsTitle"/>
                  <jsp:param name="showMoreLocale" value="forge.indexPage.showMoreStarredMaterialsLabel"/>
                  <jsp:param name="noItemsMessageLocale" value="forge.indexPage.noStarredMaterialsMessage"/>
                  <jsp:param name="showParentFolder" value="true"/>
                </jsp:include>
                
                <jsp:include page="/jsp/templates/fragments/forge_materiallist.jsp">
                  <jsp:param name="listId" value="forgeRecentlyModifiedMaterialList" />
                  <jsp:param name="listVariable" value="recentlyModifiedMaterials" />
                  <jsp:param name="showMoreLink" value="0"/>
                  <jsp:param name="titleLocale" value="forge.indexPage.recentlyModifiedMaterialsTitle"/>
                  <jsp:param name="showMoreLocale" value="forge.indexPage.showMoreRecentlyModifiedMaterialsLabel"/>
                  <jsp:param name="noItemsMessageLocale" value="forge.indexPage.noRecentlyModifiedMaterialsMessage"/>
                  <jsp:param name="showParentFolder" value="true"/>
                </jsp:include>
                
                <jsp:include page="/jsp/templates/fragments/forge_materiallist.jsp">
                  <jsp:param name="listId" value="forgeRecentlyViewedMaterialList" />
                  <jsp:param name="listVariable" value="recentlyViewedMaterials" />
                  <jsp:param name="showMoreLink" value="0"/>
                  <jsp:param name="titleLocale" value="forge.indexPage.recentlyViewedMaterialsTitle"/>
                  <jsp:param name="showMoreLocale" value="forge.indexPage.showMoreRecentlyViewedMaterialsLabel"/>
                  <jsp:param name="noItemsMessageLocale" value="forge.indexPage.noRecentlyViewedMaterialsMessage"/>
                  <jsp:param name="showParentFolder" value="true"/>
                </jsp:include>
                  
              </div>       
            </div>
          </div>
          <div class="clearFloating"></div>
        </div> 
        
        <div id="forgeWorkspaceWindowDockingBar"></div>
              
        <jsp:include page="templates/footer.jsp"></jsp:include>
    
      </div> <!-- end of siteWrapper -->
    </div> <!-- end of siteLayoutWrapper -->
  </body>
</html>