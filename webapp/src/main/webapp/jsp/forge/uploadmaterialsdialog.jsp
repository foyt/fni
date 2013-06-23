<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="dialogContent uploadMaterialsDialogContent">


  <form method="post" enctype="multipart/form-data" accept-charset="utf-8" target="__upload_frame__" action="${pageContext.request.contextPath}/v1/materials/-/upload">
    <div class="stackedLayoutContainer">
      <fieldset class="stackedLayoutFieldsContainer">
        <div class="formHelpField">
          <fmt:message key="forge.uploadMaterials.helpText"/>
        </div>
        
        <div class="formField formSelectFolder">
          <div class="formFieldLabelContainer">
            <label><fmt:message key="forge.uploadMaterials.parentFolder"/></label>
          </div>
          <div class="formFieldEditorContainer namedSelectFolder">
		  </div>    
        </div>
        
        <div class="namedUploadFiles"></div>
        
        <input type="hidden" name="parentFolderId" value=""/>
      </fieldset>
      <fieldset class="stackedLayoutButtonsContainer">
        <jsp:include page="/jsp/templates/fragments/form_submitfield.jsp">
          <jsp:param name="name" value="renameDocumentOkButton" />
          <jsp:param name="textLocale" value="forge.uploadMaterials.uploadButton" />
          <jsp:param name="classes" value="formvalid namedUploadButton" />
        </jsp:include>
      </fieldset>
    </div>
  </form>

  <iframe src="about:blank" class="namedUploadFrame" width="0" height="0" name="__upload_frame__"></iframe>
</div>