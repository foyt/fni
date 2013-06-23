<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>
      <fmt:message key="userProfile.pageTitle">
        <fmt:param>${user.fullName}</fmt:param>
      </fmt:message>
    </title>
    <jsp:include page="templates/head.jsp"></jsp:include>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/themes/default_dev/css/root.css" />
  </head>
  <body class="siteBodyWrapper">
    <div id="siteWrapper">
      <div id="siteLayoutWrapper">
  
        <jsp:include page="templates/header.jsp"></jsp:include>
  
        <!-- Site's Content -->
        <div id="siteContentWrapper">
          <div id="userProfile">
            <div class="userProfileBasicInfoContainer">
              <h2 class="userProfileName">${user.fullName}</h2>
              <div class="userProfileImageContainer">
                <div class="userProfileImageInnerContainer">
                  <c:choose>
                    <c:when test="${user.profileImage ne null}">
                      <img class="userProfileImage" src="${pageContext.request.contextPath}/v1/users/${user.id}/profileImage/128x128"/>
                    </c:when>
                    <c:otherwise>
                      <div class="userProfileImage userProfileNoImage"></div>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
          
              <div class="userProfileNickname">${user.nickname}</div>
            </div>

            <h3 class="userProfileFriendsTitle"><fmt:message key="userProfile.friendsTitle" /></h3>            
            <div class="userProfileFriendsContainer">
              <c:forEach var="friend" items="${friends}">
                <div class="userProfileFriendContainer">
                  <div class="userProfileFriendImageContainer">
                    <div class="userProfileFriendImageInnerContainer">
                      <c:choose>
                        <c:when test="${friend.profileImage ne null}">
                          <img class="userProfileFriendImage" src="${pageContext.request.contextPath}/v1/users/${friend.id}/profileImage/64x64"/>
                        </c:when>
                        <c:otherwise>
                          <div class="userProfileFriendImage userProfileFriendNoImage"></div>
                        </c:otherwise>
                      </c:choose>
                    </div>                 
                  </div>
                  <a class="userProfileFriendName" href="${pageContext.request.contextPath}/users/${friend.id}">${friend.fullName}</a>
                </div>
              </c:forEach>
            </div>
          </div>
          
          <div class="clearFloating"></div>
        </div>
  
        <jsp:include page="templates/footer.jsp"></jsp:include>
  
      </div>
      <!-- end of siteWrapper -->
    </div>
    <!-- end of siteLayoutWrapper -->
  </body>
</html>