<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="dialogContent editFolderDialogContent">

  <form method="post" onsubmit="return false;">
    <div class="stackedLayoutContainer">
      <fieldset class="stackedLayoutFieldsContainer">
        <div class="formHelpField">
          <fmt:message key="forge.editFolder.helpText"/>
        </div>
        
        <jsp:include page="/jsp/templates/fragments/form_textfield.jsp">
          <jsp:param name="name" value="title" />
          <jsp:param name="classes" value="required" />
          <jsp:param name="value" value="${folder.title}" />
          <jsp:param name="labelLocale" value="forge.editFolder.titleLabel" />
        </jsp:include>
      </fieldset>
      <fieldset class="stackedLayoutButtonsContainer">

        <jsp:include page="/jsp/templates/fragments/form_submitfield.jsp">
          <jsp:param name="name" value="cancelButton" />
          <jsp:param name="textLocale" value="forge.editFolder.saveButton" />
          <jsp:param name="classes" value="formvalid formSaveButton namedSaveButton" />
        </jsp:include>
        
        <jsp:include page="/jsp/templates/fragments/form_submitfield.jsp">
          <jsp:param name="name" value="cancelButton" />
          <jsp:param name="textLocale" value="forge.editFolder.cancelButton" />
          <jsp:param name="classes" value="formvalid formCancelButton namedCancelButton" />
        </jsp:include>

      </fieldset>
    </div>
  </form>
  
</div>