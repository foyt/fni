<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- Site's Footer -->
<div id="siteFooterWrapper">
  <div id="siteFooterBackground"></div>
  <a id="siteFooterTwitterIcon" class="siteFooterIconContainer" target="_blank" href="https://twitter.com/forgeillusion"></a>
  <a id="siteFooterFacebookIcon" class="siteFooterIconContainer" target="_blank" href="https://www.facebook.com/pages/Forge-Illusion/82322819727"></a>
  <a id="siteFooterRSSIcon" class="siteFooterIconContainer" target="_blank"></a>
  <a id="siteFooterGoogleCodeIcon" class="siteFooterIconContainer" target="_blank" href="http://code.google.com/p/fni/"></a>
</div>  
<div id="siteFooterTextWrapper">
  <div class="siteFooterTextContainer"><a href="https://twitter.com/forgeillusion"><fmt:message key="generic.footer.followUsTwitter"/></a></div>
  <div class="siteFooterTextContainer"><a href="https://www.facebook.com/pages/Forge-Illusion/82322819727"><fmt:message key="generic.footer.followUsFacebook"/></a></div>
  <div class="siteFooterTextContainer"><fmt:message key="generic.footer.followUsRSS"/></div>
  <div class="siteFooterTextContainer"><a href="http://code.google.com/p/fni/"><fmt:message key="generic.footer.followUsGoogleCode"/></a></div>
  <div class="siteFooterTextContainer followUsText"><fmt:message key="generic.footer.followUs"/></div>
</div>  

<jsp:include page="../../templates/footer.jsp"></jsp:include>