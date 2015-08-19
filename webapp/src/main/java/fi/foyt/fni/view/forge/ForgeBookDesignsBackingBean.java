package fi.foyt.fni.view.forge;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.BookDesign;
import fi.foyt.fni.persistence.model.materials.BookTemplate;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;

@RequestScoped
@Named
@Stateful
@Join(path = "/forge/book-designs/{ownerId}/{urlPath}", to = "/forge/book-designs.jsf")
@LoggedIn
public class ForgeBookDesignsBackingBean {
  
  @Parameter
  @Matches("[0-9]{1,}")
  private Long ownerId;

  @Parameter
  @Matches("[a-zA-Z0-9_/.\\-:,]{1,}")
  private String urlPath;
  
  @Parameter
  private Boolean useTemplate;

  @SuppressWarnings("unused")
  @Inject
  private Logger logger;
  
  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private NavigationController navigationController;

  @RequestAction
  public String load() {
    if ((getOwnerId() == null) || (getUrlPath() == null)) {
      return navigationController.notFound();
    }

    String completePath = "/materials/" + getOwnerId() + "/" + getUrlPath();
    Material material = materialController.findMaterialByCompletePath(completePath);
    User loggedUser = sessionController.getLoggedUser();

    if (!(material instanceof BookDesign)) {
      return navigationController.notFound();
    }

    if (!materialPermissionController.hasAccessPermission(loggedUser, material)) {
      return navigationController.accessDenied();
    }
    
    materialId = material.getId();
    title = material.getTitle();
    data = ((BookDesign) material).getData();
    styles = ((BookDesign) material).getStyles();
    fonts = ((BookDesign) material).getFonts();
    folders = ForgeViewUtils.getParentList(material);
    googlePublicApiKey = systemSettingsController.getSetting(SystemSettingKey.GOOGLE_PUBLIC_API_KEY);
    
    materialController.markMaterialView(material, loggedUser);
    
    return null;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public String getUrlPath() {
    return urlPath;
  }

  public void setUrlPath(String urlPath) {
    this.urlPath = urlPath;
  }
  
  public Boolean getUseTemplate() {
    return useTemplate;
  }
  
  public void setUseTemplate(Boolean useTemplate) {
    this.useTemplate = useTemplate;
  }
  
  public Long getMaterialId() {
    return materialId;
  }
  
  public void setMaterialId(Long materialId) {
    this.materialId = materialId;
  }
  
  public String getTemplateName() {
    return templateName;
  }
  
  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }
  
  public Long getTemplateId() {
    return templateId;
  }
  
  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
  }

  public List<Folder> getFolders() {
    return folders;
  }
  
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getData() {
    return data;
  }
  
  public void setData(String data) {
    this.data = data;
  }
  
  public String getStyles() {
    return styles;
  }
  
  public void setStyles(String styles) {
    this.styles = styles;
  }
  
  public String getFonts() {
    return fonts;
  }
  
  public void setFonts(String fonts) {
    this.fonts = fonts;
  }
  
  public String getGooglePublicApiKey() {
    return googlePublicApiKey;
  }
  
  public String save() {
    BookDesign bookDesign = materialController.findBookDesign(getMaterialId());
    
    if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), bookDesign)) {
      return navigationController.accessDenied();
    }
    
    materialController.updateBookDesign(bookDesign, sessionController.getLoggedUser(), getTitle(), getData(), getStyles(), getFonts());
    Folder parentFolder = bookDesign.getParentFolder();
    Long ownerId = parentFolder != null ? parentFolder.getCreator().getId() : bookDesign.getCreator().getId();
    String urlPath = bookDesign.getPath().substring(String.valueOf(ownerId).length() + 1);
    return String.format("/forge/book-designs.jsf?faces-redirect=true&ownerId=%d&urlPath=%s", ownerId, urlPath);
  }
  
  public String publishTemplate() {
    User loggedUser = sessionController.getLoggedUser();
    BookDesign bookDesign = materialController.findBookDesign(getMaterialId());
    
    if (!materialPermissionController.hasModifyPermission(loggedUser, bookDesign)) {
      return navigationController.accessDenied();
    }
    
    String urlName = materialController.getUniqueMaterialUrlName(loggedUser, bookDesign.getParentFolder(), null, getTemplateName());    
    String description = "";
    String iconUrl = "about:blank";
    
    BookTemplate bookTemplate = materialController.createBookTemplate(bookDesign.getParentFolder(), urlName, getTemplateName(), getData(), getStyles(), getFonts(), description, iconUrl, null, loggedUser);
    materialController.updateMaterialPublicity(bookTemplate, MaterialPublicity.PUBLIC, loggedUser);

    Folder parentFolder = bookTemplate.getParentFolder();
    Long ownerId = parentFolder != null ? parentFolder.getCreator().getId() : bookTemplate.getCreator().getId();
    String urlPath = bookTemplate.getPath().substring(String.valueOf(ownerId).length() + 1);

    return String.format("/forge/book-templates.jsf?faces-redirect=true&ownerId=%d&urlPath=%s", ownerId, urlPath);
  }
  
  public String applyTemplate() {
    User loggedUser = sessionController.getLoggedUser();
    BookDesign bookDesign = materialController.findBookDesign(getMaterialId());
    
    if (!materialPermissionController.hasModifyPermission(loggedUser, bookDesign)) {
      return navigationController.accessDenied();
    }
    
    BookTemplate bookTemplate = materialController.findBookTemplate(getTemplateId());
    
    materialController.updateBookDesign(bookDesign, sessionController.getLoggedUser(), bookDesign.getTitle(), bookTemplate.getData(), bookTemplate.getStyles(), bookTemplate.getFonts());
    Folder parentFolder = bookDesign.getParentFolder();
    
    Long ownerId = parentFolder != null ? parentFolder.getCreator().getId() : bookDesign.getCreator().getId();
    String urlPath = bookDesign.getPath().substring(String.valueOf(ownerId).length() + 1);
    return String.format("/forge/book-designs.jsf?faces-redirect=true&ownerId=%d&urlPath=%s", ownerId, urlPath);
  }
  
  private Long materialId;
  private String templateName;
  private Long templateId;
  
  private List<Folder> folders;
  private String title;
  private String data;
  private String styles;
  private String fonts;
  private String googlePublicApiKey;
}
