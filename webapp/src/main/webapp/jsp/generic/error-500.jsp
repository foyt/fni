<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="generic.error.page.500.title"/></title>
    <jsp:include page="templates/head.jsp"></jsp:include>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/themes/default_dev/css/errors.css" />
  </head>
  <body class="siteBodyWrapper errorPage errorPage500">
    <div id="siteWrapper">
      <div id="siteLayoutWrapper">
  
        <jsp:include page="templates/header.jsp"></jsp:include>
  
        <!-- Site's Content -->
        <div id="siteContentWrapper">
          <div class="errorContainer">
            <span class="imageContainer"></span>
            <h1><fmt:message key="generic.error.page.500.title"/></h1>
            <p class="descriptionContainer"><fmt:message key="generic.error.page.500.description"/></p>
            <p class="hintContainer"><fmt:message key="generic.error.page.500.hint"/></p>
          </div>
          
          <div class="stackTraceContainer">
            <h2><fmt:message key="generic.error.page.500.stackTraceTitle"/></h2>
           
           <div class="stackTrace">
            <span class="stackTraceElementMessage">${cause.message}</span>
            <div class="stackTraceElements"> 
               <c:forEach var="stackTraceElement" items="${cause.stackTrace}">
                 <div class="errorPageStackTraceElement">
                   <fmt:message key="generic.error.page.500.stackTraceElement">
                     <fmt:param>${stackTraceElement}</fmt:param>
                   </fmt:message>
                 </div>
               </c:forEach>
             </div>
           </div>
         </div>
          
          <div class="clearFloating"></div>
        </div> 

        <jsp:include page="templates/footer.jsp"></jsp:include>
      </div> <!-- end of siteWrapper -->

    </div> <!-- end of siteLayoutWrapper -->
  </body>
  
</html>