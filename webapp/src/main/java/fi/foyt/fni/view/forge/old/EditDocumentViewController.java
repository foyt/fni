package fi.foyt.fni.view.forge.old;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.materials.DocumentDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentRevisionDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialSettingDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialSettingKeyDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialTagDAO;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.MaterialSetting;
import fi.foyt.fni.persistence.model.materials.MaterialSettingKey;
import fi.foyt.fni.persistence.model.materials.MaterialTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.ViewControllerContext;
import fi.foyt.fni.view.ViewControllerException;

//@RequestScoped
//@Stateful
public class EditDocumentViewController extends AbstractViewController {

  @Inject
  private SessionController sessionController;

  @Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private DocumentDAO documentDAO;

	@Inject
	private DocumentRevisionDAO documentRevisionDAO;

	@Inject
	private MaterialTagDAO materialTagDAO;
	
	@Inject
	private MaterialSettingKeyDAO materialSettingKeyDAO;
	
	@Inject
	private MaterialSettingDAO materialSettingDAO;
	
	
  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    User loggedUser = sessionController.getLoggedUser();
    if (loggedUser == null) {
    	return false;
    }
    
    if ("NEW".equals(context.getStringParameter("documentId"))) {
    	return true;
    }
    
    Long documentId = context.getLongParameter("documentId");
    Document document = documentDAO.findById(documentId);
    
  	return materialPermissionController.hasModifyPermission(loggedUser, document);
  }

  @Override
  public void execute(ViewControllerContext context) {
    if ("NEW".equals(context.getStringParameter("documentId"))) {
      context.getRequest().setAttribute("documentContent", "");
      context.getRequest().setAttribute("documentId", "");
      context.getRequest().setAttribute("revision", "0");
      context.getRequest().setAttribute("documentTitle", Locales.getText(context.getRequest().getLocale(), "forge.editDocument.untitledDocument"));
      context.getRequest().setAttribute("parentFolder", null);
    } else {
      Long documentId = context.getLongParameter("documentId");
      if (documentId == null) {
      	throw new ViewControllerException(Locales.getText(context.getRequest().getLocale(), "generic.error.missingParameter", "documentId"));
      }
      
      Document document = documentDAO.findById(documentId);
      
    	StringBuilder keywordsBuilder = new StringBuilder();
    	List<MaterialTag> materialTags = materialTagDAO.listByMaterial(document);
    	for (int i = 0, l = materialTags.size(); i < l; i++) {
    		keywordsBuilder.append(StringEscapeUtils.escapeHtml4(materialTags.get(i).getTag().getText()));
    		if (i < (l - 1))
    			keywordsBuilder.append(',');
    	}
    	
      String content = document.getData();
      HtmlBuilder htmlBuilder = new HtmlBuilder(null, content);
      htmlBuilder.addHeaderTag("title", document.getTitle());
      htmlBuilder.addHeaderTag("meta", null, "name=\"keywords\" content=\"" + keywordsBuilder.toString() + "\"");
      htmlBuilder.addHeaderTag("meta", null, "name=\"description\" content=\"" + StringEscapeUtils.escapeHtml4(getDocumentSetting(document, "document.metaDescription")) + "\"");
      htmlBuilder.addHeaderTag("meta", null, "http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"");
      
      if (document.getLanguage() != null) {
      	String langCode = document.getLanguage().getISO2();
      	htmlBuilder.addHtmlAttribute("xml:lang", langCode);
      	htmlBuilder.addHtmlAttribute("lang", langCode);
      }
      
      StringBuilder bodyStyleBuilder = new StringBuilder();
      
      addBodyStyle(document, bodyStyleBuilder, "document.textColor", "color");
      addBodyStyle(document, bodyStyleBuilder, "document.backgroundColor", "background-color");
  		addBodyStyle(document, bodyStyleBuilder, "document.backgroundImage", "background-image");
  		addBodyStyle(document, bodyStyleBuilder, "document.backgroundAttachment", "background-attachment");
  		addBodyStyle(document, bodyStyleBuilder, "document.pageMarginLeft", "margin-left");
  		addBodyStyle(document, bodyStyleBuilder, "document.pageMarginTop", "margin-top");
  	  addBodyStyle(document, bodyStyleBuilder, "document.pageMarginRight", "margin-right");
  	  addBodyStyle(document, bodyStyleBuilder, "document.pageMarginBottom", "margin-bottom");
      
      String langDir = getDocumentSetting(document, "document.langDir");
      if (StringUtils.isNotBlank(langDir)) {
      	htmlBuilder.addBodyAttribute("dir", langDir);
      	htmlBuilder.addBodyAttribute("style", bodyStyleBuilder.toString());
      }
      
      context.getRequest().setAttribute("documentContent", htmlBuilder.getHtml());
      
      Long revision = documentRevisionDAO.maxRevisionByDocument(document);
      if (revision == null)
      	revision = 0l;
      
      context.getRequest().setAttribute("documentId", document.getId());
      context.getRequest().setAttribute("revision", revision);
      context.getRequest().setAttribute("documentTitle", document.getTitle());
      context.getRequest().setAttribute("parentFolder", document.getParentFolder());
    }
    
    context.setIncludeJSP("/jsp/forge/editdocument.jsp");
  }
  
  private void addBodyStyle(Document document, StringBuilder bodyStyleBuilder, String settingKeyName, String styleName) {
  	String documentSetting = getDocumentSetting(document, settingKeyName);
  	if (StringUtils.isNotBlank(documentSetting)) {
  		if (bodyStyleBuilder.length() > 0) {
  			bodyStyleBuilder.append(';');
  		}
  		
  		bodyStyleBuilder.append(styleName);
  		bodyStyleBuilder.append(':');
  		bodyStyleBuilder.append(documentSetting);
  	}
  }

	private String getDocumentSetting(Document document, String settingKeyName) {
  	MaterialSettingKey settingKey = materialSettingKeyDAO.findByName(settingKeyName);
  	if (settingKey != null) {
  		MaterialSetting materialSetting = materialSettingDAO.findByMaterialAndKey(document, settingKey);
  		if (materialSetting != null)
  			return materialSetting.getValue();
  	}
  	
  	return null;
  }
	
	private class HtmlBuilder {
		
		public HtmlBuilder(String headContent, String bodyContent) {
		  this.headContent = headContent != null ? new StringBuilder(headContent) : new StringBuilder();
		  this.bodyContent = bodyContent != null ? new StringBuilder(bodyContent) : new StringBuilder();
	  }

		public void addHtmlAttribute(String name, String value) {
			if (htmlAttributes == null) {
				htmlAttributes = new StringBuilder(' ');
			}

			htmlAttributes.append(StringEscapeUtils.escapeHtml4(name));
			htmlAttributes.append('=');
			htmlAttributes.append('"');
			htmlAttributes.append(StringEscapeUtils.escapeHtml4(value));
			htmlAttributes.append('"');
		}
		
		@SuppressWarnings("unused")
    public void addHeadAttribute(String name, String value) {
			if (headAttributes == null) {
				headAttributes = new StringBuilder(' ');
			}
			
			headAttributes.append(StringEscapeUtils.escapeHtml4(name));
			headAttributes.append('=');
			headAttributes.append('"');
			headAttributes.append(StringEscapeUtils.escapeHtml4(value));
			headAttributes.append('"');
		}

		public void addBodyAttribute(String name, String value) {
			if (bodyAttributes == null) {
				bodyAttributes = new StringBuilder(' ');
			}

			bodyAttributes.append(StringEscapeUtils.escapeHtml4(name));
			bodyAttributes.append('=');
			bodyAttributes.append('"');
			bodyAttributes.append(StringEscapeUtils.escapeHtml4(value));
			bodyAttributes.append('"');
		}

		public void addHeaderTag(String tagName, String value, String attributes) {
		  headContent.append(buildTag(tagName, value, attributes));
	  }
		
		public void addHeaderTag(String tagName, String value) {
			addHeaderTag(tagName, value, null);
	  }
		
		@SuppressWarnings("unused")
    public void addBodyTag(String tagName, String value) {
			addBodyTag(tagName, value, null);
		}

		public void addBodyTag(String tagName, String value, String attributes) {
		  bodyContent.append(buildTag(tagName, value, attributes));
	  }
		
		private String buildTag(String tagName, String value, String attributes) {
			StringBuilder tagBuilder = new StringBuilder();
			
			if (StringUtils.isNotBlank(value)) {
	  		tagBuilder.append("<").append(tagName);
	  		if (StringUtils.isNotBlank(attributes)) {
	  			tagBuilder.append(' ').append(attributes);
	  		}
	  		tagBuilder.append(">");
	  		tagBuilder.append(value);
	  		tagBuilder.append("</").append(tagName).append(">");
			} else {
	  		tagBuilder.append("<").append(tagName);
	  		if (StringUtils.isNotBlank(attributes)) {
	  			tagBuilder.append(' ').append(attributes);
	  		}
	  		tagBuilder.append("/>");
			}
			
			return tagBuilder.toString();
		}

		public String getHtml() {
			StringBuilder htmlBuilder = new StringBuilder();
			htmlBuilder.append(DOCTYPE);
			htmlBuilder.append(HTML_START_TAG);
			if (StringUtils.isNotBlank(htmlAttributes)) {
				htmlBuilder.append(' ').append(htmlAttributes);
			}
			htmlBuilder.append('>');
			
			if (StringUtils.isNotBlank(headContent)) {
				htmlBuilder.append(HEAD_START_TAG);
				if (StringUtils.isNotBlank(headAttributes)) {
					htmlBuilder.append(' ').append(headAttributes);
				}
				
				htmlBuilder.append('>');
				htmlBuilder.append(headContent);
				htmlBuilder.append(HEAD_END_TAG);
			}
			
			htmlBuilder.append(BODY_START_TAG);
			if (StringUtils.isNotBlank(bodyAttributes)) {
				htmlBuilder.append(' ').append(bodyAttributes);
			}
			htmlBuilder.append('>');
			
			htmlBuilder.append(bodyContent);
			htmlBuilder.append(BODY_END_TAG);
			htmlBuilder.append(HTML_END_TAG);
			
			return htmlBuilder.toString();
		}

		private StringBuilder htmlAttributes;
		private StringBuilder headAttributes;
		private StringBuilder bodyAttributes;
		private StringBuilder bodyContent;
		private StringBuilder headContent;
		
		private static final String DOCTYPE = "<!DOCTYPE HTML>";
		private static final String HTML_START_TAG = "<html";
		private static final String HTML_END_TAG = "</html>";
		private static final String HEAD_START_TAG = "<head";
		private static final String HEAD_END_TAG = "</head>";
		private static final String BODY_START_TAG = "<body";
		private static final String BODY_END_TAG = "</body>";
	}
}