<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="/jsp/templates/fragments/forge_materiallist_content.jsp">
  <jsp:param name="listVariable" value="recentlyViewedMaterials" />
  <jsp:param name="showMoreLink" value="0"/>
  <jsp:param name="showMoreLocale" value="forge.indexPage.showMoreRecentlyViewedMaterialsLabel"/>
  <jsp:param name="noItemsMessageLocale" value="forge.indexPage.noRecentlyViewedMaterialsMessage"/>
  <jsp:param name="showParentFolder" value="true"/>
</jsp:include>