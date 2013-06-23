<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>
      <fmt:message key="forum.viewTopicPage.pageTitle">
        <fmt:param>${topic.subject}</fmt:param>
      </fmt:message>
    </title>
    <jsp:include page="templates/head.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forum/ckconfig.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scripts/gui/forum/viewtopic.js"></script>
    <script type="text/javascript">
      var viewController = new ViewTopicViewController();
    
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
        
          <div id="forumsPostsList">
            <div class="forumActions forumActionsTop">
              <a title="<fmt:message key="forum.viewTopicPage.indexButtonTooltip"/>" class="forumButton forumForumViewIndexButton" href="${pageContext.request.contextPath}/forum/"> </a>
              <a title="<fmt:message key="forum.viewTopicPage.forumButtonTooltip"/>" class="forumButton forumForumViewForumButton" href="${pageContext.request.contextPath}/forum/${topic.forum.urlName}"> </a>
              <a title="<fmt:message key="forum.viewTopicPage.followButtonTooltip"/>" class="forumButton forumForumFollowTopicButton incompleteFeature" href="${pageContext.request.contextPath}/forum/index.page"> </a>
 
              <div id="forumSearch">    
                <input id="forumSearchInput" type="text" />
                <div id="forumSearchResultsContainer">
                  <ul id="forumSearchResults" style="display: none"></ul>
                </div>
              </div>
            </div>
            
            <div class="headerRow">
              <div class="forumTopicAuthorLabel"><fmt:message key="forum.viewTopicPage.authorLabel"/></div>
              <div class="forumTopicSubject">
                <c:out value="${topic.subject}"/>
              </div>
            </div>
           
            <div class="forumPosts">
              <c:forEach var="post" items="${posts}">
                <div class="forumPost">
                  <a name="p${post.id}"></a>
                  <div class="forumPostCreated">
                    <fmt:message key="forum.viewTopicPage.postSentLabel">
                      <fmt:param><fmt:formatDate value="${post.created}" type="both"/></fmt:param>
                    </fmt:message>
                  </div>
                  <c:if test="${post.created ne post.modified}">
                    <div class="forumPostModified">
                      <fmt:message key="forum.viewTopicPage.postModifiedLabel">
                        <fmt:param><fmt:formatDate value="${post.modified}" type="both"/></fmt:param>
                      </fmt:message>
                    </div>
                  </c:if>
                  <div class="forumPostAuthor">
                    <div class="forumPostAuthorImageContainer">
                      <div class="forumPostAuthorImageInnerContainer">
                        <c:choose>
                          <c:when test="${post.hasAuthorImage}">
                            <img src="${pageContext.request.contextPath}/v1/users/${post.authorId}/profileImage/128x128"/>
                          </c:when>
                          <c:otherwise>
                            <div class="forumPostAuthorImageDefault"></div>
                          </c:otherwise>
                        </c:choose>
                      </div>
                    </div>
                    
                    <div class="forumPostAuthorName">
                      ${post.authorName}
                    </div>
                    
                    <div class="forumPostAuthorPosts">
                      <fmt:message key="forum.viewTopicPage.authorPostCountLabel">
                        <fmt:param>${post.authorPostCount}</fmt:param>
                      </fmt:message>
                    </div>
                  </div>
                  <div class="forumPostContent">${post.content}</div>
                </div>
              </c:forEach>
              
              <div id="forumReplyTopicContainer" style="display: none">
                <form name="replyTopic">
                  
                  <jsp:include page="/jsp/templates/fragments/form_memofield.jsp">
                    <jsp:param name="id" value="forumReplyTopic"/>
                    <jsp:param name="classes" value="required"/>
                  </jsp:include>
                  
                  <jsp:include page="/jsp/templates/fragments/form_submitfield.jsp">
                    <jsp:param name="classes" value="formvalid formOkButton"/>
                    <jsp:param name="name" value="postReplyButton"/>
                    <jsp:param name="textLocale" value="forum.viewTopicPage.postReply"/>
                  </jsp:include>
                </form>
              </div>
              
              <c:if test="${canCreatePost eq true}">
                <div class="forumActions forumActionsBottom">
                  <input type="submit" value="<fmt:message key="forum.viewTopicPage.replyTopic"/>" class="forumPostReplyButton"/>
                </div>
              </c:if>              
            </div>
          </div>
        
          <div class="clearFloating"></div>
        </div> 
        
        <jsp:include page="templates/footer.jsp"></jsp:include>
        
      </div> <!-- end of siteWrapper -->
    </div> <!-- end of siteLayoutWrapper -->
    
    <input type="hidden" name="topicId" value="${topic.id}"/>
  </body>
</html>