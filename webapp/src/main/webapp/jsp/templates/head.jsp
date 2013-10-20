<%@page import="fi.foyt.fni.view.ViewControllerContext"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/themes/default_dev/css/common.css" />
<script type="text/javascript">
  var CONTEXTPATH = '${pageContext.request.contextPath}';
  var THEMEPATH = '${pageContext.request.contextPath}/themes/${theme}/';
</script>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/prototype/1.7.0/prototype.js"></script>
<script type="text/javascript" src="http://cdn.jsdelivr.net/ckeditor/3.6.3/ckeditor.js"></script>

<c:set var="jsVaribleScript"/>
<c:forEach var="jsVariable" items="${jsVariables}">
  <c:set var="jsVaribleScript">
    ${jsVaribleScript}
    window._jsVariables.set("${fn:replace(jsVariable.key, '"', '\\"')}", "${fn:replace(jsVariable.value, '"', '\\"')}");
  </c:set>
</c:forEach>

<script type="text/javascript">
  window._jsVariables = new Hash();
  window.getJsVariable = function (key) {
    return window._jsVariables.get(key);
  };
  
  ${jsVaribleScript}
</script>

<script src="${pageContext.request.contextPath}/resources/scripts/scripty2/s2.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/pfvlib/pfvlib.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/cookie/cookie.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/prototype-datepicker-widget/datepicker.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/forge/ckconfig.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/fnievents/fnievents.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/fnilocale/fnilocale.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/notificationqueue.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/jsonutils.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/api.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/tooltip.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/tooltipcontroller.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/selectautocompleter.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/modaldialogcontroller.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/confirmdialogcontroller.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/guicomponent.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/sitemenubarcontroller.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/messagesmenubarwidget.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/chatmenubarwidget.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/friendsmenubarwidget.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/guicomponent.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/uploadcomponent.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/treecomponent.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/scripts/gui/common/viewmessages.js" type="text/javascript"></script>
