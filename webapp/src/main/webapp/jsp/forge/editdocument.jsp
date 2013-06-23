<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="forgeEditDocumentContainer">
  
  <form name="document">
    <input type="hidden" name="documentTitle" value="${documentTitle}"/>
    <input type="hidden" name="documentId" value="${documentId}"/>
    <input type="hidden" name="revision" value="${revision}"/>
    <input type="hidden" name="parentFolderId" value="${parentFolder eq null ? 'HOME' : parentFolder.id}"/>
  
    <jsp:include page="/jsp/templates/fragments/form_memofield.jsp">
      <jsp:param name="value" value="${documentContent}"/>
      <jsp:param name="classes" value="required namedContent"/>
    </jsp:include>
  </form>
  
</div>