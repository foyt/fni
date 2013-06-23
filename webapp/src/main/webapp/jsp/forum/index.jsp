<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>
      <fmt:message key="forum.indexPage.pageTitle"/>
    </title>
      
    <jsp:include page="templates/head.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forum/index.js"></script>
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
          <div id="forumsList">
            <div class="forumActions">
              <a title="<fmt:message key="forum.indexPage.followButtonTooltip"/>" class="forumButton forumForumFollowAllButton incompleteFeature" href="javascript:void(null);"> </a>
              <c:if test="${canModerate eq true}">
                <a title="<fmt:message key="forum.indexPage.createCategoryTooltip"/>" class="forumButton forumForumCreateCategoryButton incompleteFeature" href="javascript:void(null);"> </a>
                <a title="<fmt:message key="forum.indexPage.createForumTooltip"/>" class="forumButton forumForumCreateForumButton incompleteFeature" href="javascript:void(null);"> </a>
              </c:if>
          
              <div id="forumSearch">    
                <input id="forumSearchInput" type="text" />
                <div id="forumSearchResultsContainer">
                  <ul id="forumSearchResults" style="display: none"></ul>
                </div>
              </div>
            </div>
        
            <c:forEach var="category" items="${categories}">
              <div class="forumCategory">
                <div class="forumCategoryName">${category.name}</div>
                <c:if test="${canModerate eq true}">
                  <div class="forumCategoryActions">
                    <a href="javascript:void(null);" class="incompleteFeature"><fmt:message key="forum.indexPage.editCategory"/></a>
                    <a href="javascript:void(null);" class="incompleteFeature"><fmt:message key="forum.indexPage.deleteCategory"/></a>
                  </div>
                </c:if>
              </div>
              <div class="forumCategoryForums">
                <c:forEach var="forum" items="${forums[category]}">
                  <div class="forumCategoryForum">
                    <div class="forumCategoryForumName">
                      <a href="${pageContext.request.contextPath}/forum/${forum.urlName}">${forum.name}</a>
                    </div>
                    <c:if test="${canModerate eq true}">
                      <div class="forumForumActions">
                        <a href="javascript:void(null);" class="incompleteFeature"><fmt:message key="forum.indexPage.editForum"/></a>
                        <a href="javascript:void(null);" class="incompleteFeature"><fmt:message key="forum.indexPage.deleteForum"/></a>
                      </div>
                    </c:if>
                  </div>
                </c:forEach>
              </div>
            </c:forEach>
          </div>
        
          <div class="clearFloating"></div>
        </div> 
    
        <jsp:include page="templates/footer.jsp"></jsp:include>
      
      </div> <!-- end of siteWrapper -->
    </div> <!-- end of siteLayoutWrapper -->
  </body>
</html>