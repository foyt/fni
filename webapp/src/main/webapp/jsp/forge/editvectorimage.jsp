<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="forgeEditVectorImageContainer">
  
  <form name="vectorImage">
    <input type="hidden" name="vectorImageTitle" value="${vectorImageTitle}"/>
    <input type="hidden" name="vectorImageId" value="${vectorImageId}"/>
    <input type="hidden" name="parentFolderId" value="${parentFolder eq null ? 'HOME' : parentFolder.id}"/>
    <input type="hidden" name="data" value="${fn:escapeXml(vectorImageData)}"/>
    
    <div class="namedEditorContainer">
    </div>
  </form>
  
</div>