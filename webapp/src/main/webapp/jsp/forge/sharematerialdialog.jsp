<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="dialogContent shareMaterialDialogContent">

  <form method="post" onsubmit="return false;">
    <div class="stackedLayoutContainer">
      <fieldset class="stackedLayoutFieldsContainer">
        <div class="formHelpField">
          <fmt:message key="forge.shareMaterial.helpText"/>
        </div>
        
        <div class="formField formRadioField">
          <div class="formFieldLabelContainer">
            <label><fmt:message key="forge.shareMaterial.publicityLabel"/></label>
          </div>
          <div class="formFieldEditorContainer">
            <div class="formRadioContainer">
              <c:choose>
                <c:when test="${material.publicity eq 'PRIVATE'}">
                  <input type="radio" name="publicity" value="PRIVATE" id="share-publicity-private" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input type="radio" name="publicity" value="PRIVATE" id="share-publicity-private"/>
                </c:otherwise>
              </c:choose>
              <label for="share-publicity-private"><fmt:message key="forge.shareMaterial.publicityPrivate"/></label>
            </div>
            <!-- TODO: Implement: 
            <div class="formRadioContainer">
              <c:choose>
                <c:when test="${material.publicity eq 'FRIENDS'}">
                  <input type="radio" name="publicity" value="FRIENDS" id="share-publicity-friends" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input type="radio" name="publicity" value="FRIENDS" id="share-publicity-friends"/>
                </c:otherwise>
              </c:choose>
            
              <label for="share-publicity-friends"><fmt:message key="forge.shareMaterial.publicityFriends"/></label>
            </div>
            <div class="formRadioContainer">
              <c:choose>
                <c:when test="${material.publicity eq 'LINK'}">
                  <input type="radio" name="publicity" value="LINK" id="share-publicity-link" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input type="radio" name="publicity" value="LINK" id="share-publicity-link"/>
                </c:otherwise>
              </c:choose>
              <label for="share-publicity-link"><fmt:message key="forge.shareMaterial.publicityLink"/></label>
            </div>
             -->
            <div class="formRadioContainer">
              <c:choose>
                <c:when test="${material.publicity eq 'PUBLIC'}">
                  <input type="radio" name="publicity" value="PUBLIC" id="share-publicity-public" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input type="radio" name="publicity" value="PUBLIC" id="share-publicity-public"/>
                </c:otherwise>
              </c:choose>             
              <label for="share-publicity-public"><fmt:message key="forge.shareMaterial.publicityPublic"/></label>
            </div>
            
            <div class="formFieldLabelContainer">
              <label><fmt:message key="forge.shareMaterial.publicUrlLabel"/></label>
            </div>

            <div class="formFieldEditorContainer">
              <input type="text" name="publicUrl" value="${publicUrl}" readonly="readonly" disabled="disabled"/>
            </div>
          </div>
        </div>
        
        <div class="formField formListField namedEditorsListField">
          <div class="formFieldLabelContainer">
            <label><fmt:message key="forge.shareMaterial.editorsLabel"/></label>
          </div>
          <div class="formFieldEditorContainer">

            <c:forEach var="materialUserRole" items="${materialUserRoles}">
              <div class="formListRow">
                <input type="hidden" value="${materialUserRole.user.id}" name="userId"/>
                <div class="formListRowCell">
                  ${materialUserRole.user.fullName}
                </div> 
                <div class="formListRowCell formListRowCellAlignRight">
                  <select name="role">
                    <c:choose>
                      <c:when test="${materialUserRole.role eq 'MAY_EDIT'}">
                        <option selected="selected" value="MAY_EDIT">
                          <fmt:message key="forge.shareMaterial.editorRoleMayEdit"/>
                        </option>
                        <option value="MAY_VIEW">
                          <fmt:message key="forge.shareMaterial.editorRoleMayView"/>
                        </option>
                        <option value="NONE">
                          <fmt:message key="forge.shareMaterial.editorRoleNone"/>
                        </option>
                      </c:when>
                      <c:when test="${materialUserRole.role eq 'MAY_VIEW'}">
                        <option value="MAY_EDIT">
                          <fmt:message key="forge.shareMaterial.editorRoleMayEdit"/>
                        </option>
                        <option selected="selected" value="MAY_VIEW">
                          <fmt:message key="forge.shareMaterial.editorRoleMayView"/>
                        </option>
                        <option value="NONE">
                          <fmt:message key="forge.shareMaterial.editorRoleNone"/>
                        </option>
                      </c:when>
                      <c:otherwise>
                        <option value="MAY_EDIT">
                          <fmt:message key="forge.shareMaterial.editorRoleMayEdit"/>
                        </option>
                        <option value="MAY_VIEW">
                          <fmt:message key="forge.shareMaterial.editorRoleMayView"/>
                        </option>
                        <option selected="selected" value="NONE">
                          <fmt:message key="forge.shareMaterial.editorRoleNone"/>
                        </option>
                      </c:otherwise>
                    </c:choose>
                  </select>
                </div>
              </div>
            </c:forEach>
          </div>
          
          <div class="namedAddEditorContainer"></div>
        </div>
      </fieldset>
      <fieldset class="stackedLayoutButtonsContainer">

        <jsp:include page="/jsp/templates/fragments/form_submitfield.jsp">
          <jsp:param name="name" value="cancelButton" />
          <jsp:param name="textLocale" value="forge.shareMaterial.saveButton" />
          <jsp:param name="classes" value="formvalid formSaveButton namedSaveButton" />
        </jsp:include>
        
        <jsp:include page="/jsp/templates/fragments/form_submitfield.jsp">
          <jsp:param name="name" value="cancelButton" />
          <jsp:param name="textLocale" value="forge.shareMaterial.cancelButton" />
          <jsp:param name="classes" value="formvalid formCancelButton namedCancelButton" />
        </jsp:include>

      </fieldset>
    </div>
  </form>
  
</div>