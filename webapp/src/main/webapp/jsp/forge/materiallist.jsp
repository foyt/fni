<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="/jsp/templates/fragments/forge_materiallist_content.jsp">
  <jsp:param name="listVariable" value="materials" />
  <jsp:param name="showBreadcrumps" value="1" />
  <jsp:param name="noItemsMessageLocale" value="forge.indexPage.noMaterialsMessage"/>
</jsp:include>