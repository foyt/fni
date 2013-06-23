<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>
      <fmt:message key="index.indexPage.pageTitle"/>
    </title>
      
    <jsp:include page="templates/head.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/index/index.js"></script>
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
    <div id="siteWrapper">
      <div id="siteLayoutWrapper">
        
        <jsp:include page="templates/header.jsp"></jsp:include>
              
        <!-- Site's Content -->
        <div id="siteContentWrapper">
          <div id="indexSidebarWrapper">
            <div id="indexSidebarBackground"></div>
            <div id="indexSidebarContent">
              <div id="indexSideBarWidgetWhatIsWrapper">
                <div id="indexSideBarWidgetWhatIsIcon" class="siderbarWidgetIconContainer"></div>
                <div id="indexWhatIsContentWrapper" class="indexSiderbarWidgetContentContainer">
                  <h1><fmt:message key="index.indexPage.whatIsForgeHeader"/></h1>
                  <div id="indexWhatIsContent" class="indexSiderbarWidgetContent">
                    <b><fmt:message key="index.indexPage.whatIsForgeContentPrefix"/></b> <fmt:message key="index.indexPage.whatIsForgeContent"/>
                  </div>
                </div>
              </div>
              
              <c:if test="${fn:length(technicalAnnouncements) gt 0}">
                <div id="indexSideBarWidgetTechnicalAnnouncementsWrapper">
                  <div id="indexSideBarWidgetTechnicalAnnouncementsIcon" class="siderbarWidgetIconContainer"></div>
                  <div id="indexTechnicalAnnouncementsContentWrapper" class="indexSiderbarWidgetContentContainer">
                    <h1><fmt:message key="index.indexPage.technicalAnnouncementsHeader"/></h1>
                    <div id="indexTechnicalAnnouncementsContent" class="indexSiderbarWidgetContent">
                      <ul> 
                        <c:forEach var="article" items="${technicalAnnouncements}">
                          <li>
                            <input type="hidden" name="articleId" value="${article.id}"/>
                            <a href="javascript:void(null);" class="articleLink">${article.material.title}</a>
                            <c:if test="${loggedUser.role eq 'ADMINISTRATOR'}">
                              <a href="javascript:void(null);" class="defaultArticleLink"></a>
                            </c:if>
                          </li>
                        </c:forEach>
                      </ul>
                    </div>
                  </div>
                </div>
              </c:if>

              <c:if test="${false}">
                <!-- TODO: Add support for latest publications -->
                <div id="indexSideBarWidgetLastestPublicationsWrapper">
                  <div id="indexSideBarWidgetLastestPublicationsIcon" class="siderbarWidgetIconContainer"></div>
                  <div id="indexLastestPublicationsContentWrapper" class="indexSiderbarWidgetContentContainer">
                    <h1><fmt:message key="index.indexPage.latestPublicationsHeader"/></h1>
                    <div id="indexLastestPublicationsContent" class="indexSiderbarWidgetContent">
                      <ul>
                        <c:forEach var="publication" items="${publications}">
                          <li><a href="#${publication.id}">${publication.material.title}</a></li>
                        </c:forEach>
                      </ul>
                    </div>
                  </div>
                </div>
              </c:if>

              <c:if test="${fn:length(articles) gt 0}">
                <div id="indexSideBarWidgetLastestArticlesWrapper">
                  <div id="indexSideBarWidgetLastestArticlesIcon" class="siderbarWidgetIconContainer"></div>
                  <div id="indexLastestArticlesContentWrapper" class="indexSiderbarWidgetContentContainer">
                    <h1><fmt:message key="index.indexPage.latestArticlesHeader"/></h1>
                    <div id="indexLastestArticlesContent" class="indexSiderbarWidgetContent">
                      <ul>
                        <c:forEach var="article" items="${articles}">
                          <li>
                            <input type="hidden" name="articleId" value="${article.id}"/>
                            <a href="javascript:void(null);" class="articleLink">${article.material.title}</a>
                            <c:if test="${loggedUser.role eq 'ADMINISTRATOR'}">
                              <a href="javascript:void(null);" class="defaultArticleLink"></a>
                            </c:if>
                          </li>
                        </c:forEach>
                      </ul>
                    </div>
                  </div>
                </div>
              </c:if>
            </div>
            
          </div>
          <div id="frontPageArticle">
            <h1 class="frontPageArticleTitle">${defaultArticle.title}</h1>
            <div class="frontPageArticleTopArrow"></div>
            <div class="frontPageArticleContent">${defaultArticle.content}</div>
            <div class="frontPageArticleLoading" style="display:none"></div>
          </div>
          <div class="clearFloating"></div>
        </div> 
              
        <jsp:include page="templates/footer.jsp"></jsp:include>
    
      </div> <!-- end of siteWrapper -->
    </div> <!-- end of siteLayoutWrapper -->
  </body>
</html>