<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>
      <fmt:message key="forum.viewForumPage.pageTitle">
        <fmt:param>${forum.name}</fmt:param>
      </fmt:message>
    </title>
    <jsp:include page="templates/head.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forum/ckconfig.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forum/viewforum.js"></script>
    <script type="text/javascript">
      var viewController = new ViewForumViewController();
    
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
          <div id="forumsTopicsList">
            <div class="forumActions forumActionsTop">
              <a title="<fmt:message key="forum.viewForumPage.indexButtonTooltip"/>" class="forumButton forumForumViewIndexButton" href="${pageContext.request.contextPath}/forum/"> </a>
              <a class="linkDisabled" title="<fmt:message key="forum.viewForumPage.followButtonTooltip"/>" class="forumButton forumForumFollowForumButton" href="${pageContext.request.contextPath}/forum/index.page"> </a>

              <div id="forumSearch">    
                <input id="forumSearchInput" type="text" />
                <div id="forumSearchResultsContainer">
                  <ul id="forumSearchResults" style="display: none"></ul>
                </div>
              </div>
            </div>
            
            <div class="forumTopicLabels">
              <div class="forumTopicLabel forumTopicSubjectLabel"><fmt:message key="forum.viewForumPage.subjectLabel"/></div>
              <div class="forumTopicLabel forumTopicAuthorLabel"><fmt:message key="forum.viewForumPage.authorLabel"/></div>
              <div class="forumTopicLabel forumTopicRepliesLabel"><fmt:message key="forum.viewForumPage.replyCountLabel"/></div>
              <div class="forumTopicLabel forumTopicReadLabel"><fmt:message key="forum.viewForumPage.viewCountLabel"/></div>
              <div class="forumTopicLabel forumTopicLastMessageLabel"><fmt:message key="forum.viewForumPage.lastMessageLabel"/></div>
            </div>
      
            <c:forEach var="topic" items="${topics}">
              <div class="forumTopic">
                <div class="forumTopicSubject">
                  <a href="${pageContext.request.contextPath}/forum/${forum.urlName}/${topic.urlName}">${topic.subject}</a>
                </div>
                <div class="forumTopicAuthor">
                  ${topic.authorName}
                </div>
                <div class="forumTopicReplies">
                  ${topic.replyCount}
                </div>
                <div class="forumTopicRead">
                  ${topic.viewCount}
                </div>
                <div class="forumTopicLastMessage">
                  <fmt:formatDate value="${topic.lastPost}"/>
                </div>
              </div>
            </c:forEach>
              
            <div id="forumNewTopicContainer" style="display: none">
              <form name="newTopic">
                <jsp:include page="/jsp/templates/fragments/form_textfield.jsp">
                  <jsp:param name="name" value="newTopicSubject"/>
                  <jsp:param name="classes" value="newTopicSubject required"/>
                  <jsp:param name="labelLocale" value="forum.viewForumPage.createTopicSubjectLabel"/>
                </jsp:include>
                
                <jsp:include page="/jsp/templates/fragments/form_memofield.jsp">
                  <jsp:param name="name" value="newTopicContent"/>
                  <jsp:param name="classes" value="required"/>
                  <jsp:param name="labelLocale" value="forum.viewForumPage.createTopicContentLabel"/>
                </jsp:include>
                
                <jsp:include page="/jsp/templates/fragments/form_submitfield.jsp">
                  <jsp:param name="name" value="createNewTopicButton"/>
                  <jsp:param name="textLocale" value="forum.viewForumPage.createTopicButton"/>
                  <jsp:param name="classes" value="formvalid formOkButton"/>
                </jsp:include>
              </form>
            </div>
          
            <c:if test="${canCreateTopic eq true}">
              <div class="forumActions forumActionsBottom">
                <input type="submit" value="<fmt:message key="forum.viewForumPage.newTopic"/>" class="forumNewTopicButton"/>
              </div>
            </c:if>
          </div>
        
          <div class="clearFloating"></div>
        </div> 
        
        <jsp:include page="templates/footer.jsp"></jsp:include>
      
      </div> <!-- end of siteWrapper -->
    </div> <!-- end of siteLayoutWrapper -->
    
    <input type="hidden" name="forumId" value="${forum.id}"/>
  </body>
</html>