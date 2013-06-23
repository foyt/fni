<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<jsp:include page="../../templates/header.jsp"></jsp:include>

<!-- Site's Header -->
<div id="siteHeaderWrapper" class="indexHeaderWrapper">

  <c:choose>
    <c:when test="${loggedIn ne true}">
      <div id="indexHeaderLoginRegisterWrapper">
        <div id="indexHeaderLogin"><span class="indexHeaderBigFont"><a href="${pageContext.request.contextPath}/login"><fmt:message key="index.header.loginLink"/></a></span> <fmt:message key="index.header.loginHint"/></div>
        <div id="indexHeaderGuestLogin"><span class="indexHeaderBigFont"><a href="${pageContext.request.contextPath}/login?loginMethod=GUEST"><fmt:message key="index.header.guestLoginLink"/></a></span> <fmt:message key="index.header.guestLoginHint"/></div>
      </div>
    </c:when>
    <c:otherwise>
      <div id="indexHeaderUserInfoWrapper">
        <c:choose>
          <c:when test="${loggedUser.profileImage ne null}">
            <img src="${pageContext.request.contextPath}/v1/users/SELF/profileImage/48x48"/>
          </c:when>
          <c:otherwise>
            <div class="indexHeaderNoProfileImage"></div>
          </c:otherwise>
        </c:choose>
        
        <div id="indexHeaderUserInfo">
          <fmt:message key="index.header.loggedInAs">
            <fmt:param>${loggedUser.fullName}</fmt:param>
          </fmt:message>
          <span class="indexHeaderBigFont"><a id="indexHeaderLogoutLink" href="${pageContext.request.contextPath}/auth/logout.page"><fmt:message key="index.header.logoutLink"/></a></span> 
          <fmt:message key="index.header.or"/> 
          <span class="indexHeaderBigFont">
            <a href="${pageContext.request.contextPath}/editprofile.jsf"><fmt:message key="index.header.editProfileLink"/></a>
          </span>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</div>