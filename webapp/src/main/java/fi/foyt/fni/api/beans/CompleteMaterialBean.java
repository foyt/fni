package fi.foyt.fni.api.beans;

import java.util.Date;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.materials.GoogleDocumentType;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;

public class CompleteMaterialBean {
	
	public CompleteMaterialBean(Long id, MaterialType type, String subtype, String urlName, String title, MaterialPublicity publicity, CompactLanguageBean language, Date modified, Date created,
      CompactUserBean creator, CompactUserBean modifier, CompactFolderBean parentFolder, TypeProperties typeProperties, Boolean mayView, Boolean mayEdit, Boolean mayDelete) {
	  this.id = id;
	  this.type = type;
	  this.subtype = subtype;
	  this.urlName = urlName;
	  this.title = title;
	  this.publicity = publicity;
	  this.language = language;
	  this.modified = modified;
	  this.created = created;
	  this.creator = creator;
	  this.modifier = modifier;
	  this.parentFolder = parentFolder;
	  this.mayView = mayView;
	  this.mayEdit = mayEdit;
	  this.mayDelete = mayDelete;
	  this.typeProperties = typeProperties;
  }

	public Long getId() {
  	return id;
  }

	public void setId(Long id) {
  	this.id = id;
  }

	public MaterialType getType() {
  	return type;
  }

	public void setType(MaterialType type) {
  	this.type = type;
  }
	
	public String getSubtype() {
	  return subtype;
  }
	
	public void setSubtype(String subtype) {
	  this.subtype = subtype;
  }

	public String getUrlName() {
  	return urlName;
  }

	public void setUrlName(String urlName) {
  	this.urlName = urlName;
  }

	public String getTitle() {
  	return title;
  }

	public void setTitle(String title) {
  	this.title = title;
  }

	public MaterialPublicity getPublicity() {
  	return publicity;
  }

	public void setPublicity(MaterialPublicity publicity) {
  	this.publicity = publicity;
  }

	public CompactLanguageBean getLanguage() {
	  return language;
  }
	
	public void setLanguage(CompactLanguageBean language) {
	  this.language = language;
  }

	public Date getModified() {
  	return modified;
  }

	public void setModified(Date modified) {
  	this.modified = modified;
  }

	public Date getCreated() {
  	return created;
  }

	public void setCreated(Date created) {
  	this.created = created;
  }
	
	public CompactUserBean getCreator() {
	  return creator;
  }
	
	public void setCreator(CompactUserBean creator) {
	  this.creator = creator;
  }
	
	public CompactUserBean getModifier() {
	  return modifier;
  }
	
	public void setModifier(CompactUserBean modifier) {
	  this.modifier = modifier;
  }
	
	public CompactFolderBean getParentFolder() {
	  return parentFolder;
  }
	
	public void setParentFolder(CompactFolderBean parentFolder) {
	  this.parentFolder = parentFolder;
  }

	public Boolean getMayEdit() {
	  return mayEdit;
  }
	
	public void setMayEdit(Boolean mayEdit) {
	  this.mayEdit = mayEdit;
  }
	
	public Boolean getMayView() {
	  return mayView;
  }
	
	public void setMayView(Boolean mayView) {
	  this.mayView = mayView;
  }
	
	public Boolean getMayDelete() {
	  return mayDelete;
  }
	
	public void setMayDelete(Boolean mayDelete) {
	  this.mayDelete = mayDelete;
  }
	
	public static CompleteMaterialBean fromEntity(Material material, boolean mayEdit, boolean mayView, boolean mayDelete) {
		return fromEntity(null, material, mayEdit, mayView, mayDelete);
	}
	
	public TypeProperties getTypeProperties() {
	  return typeProperties;
  }
	
	public void setTypeProperties(TypeProperties typeProperties) {
	  this.typeProperties = typeProperties;
  }
	
	public static CompleteMaterialBean fromEntity(User user, Material entity, boolean mayEdit, boolean mayView, boolean mayDelete) {
		if (entity == null)
			return null;

		String subtype = null;
    if (entity.getType() == MaterialType.GOOGLE_DOCUMENT) {
      GoogleDocumentType googleDocumentType = ((GoogleDocument) entity).getDocumentType();
      subtype = googleDocumentType.toString();
    }
    
    Boolean printableToPdf = entity.getType() == MaterialType.DOCUMENT;
    boolean editableType = false;
    switch (entity.getType()) {
      case DOCUMENT:
      case FOLDER:
      case VECTOR_IMAGE:
      	editableType = true;
      break;
      default:
      break;
    }
		
		TypeProperties typeProperties = new TypeProperties(printableToPdf, editableType);
    
		return new CompleteMaterialBean(entity.getId(), entity.getType(), subtype, entity.getUrlName(), entity.getTitle(), entity.getPublicity(), 
				CompactLanguageBean.fromEntity(entity.getLanguage()), entity.getModified(), entity.getCreated(), 
				CompactUserBean.fromEntity(entity.getCreator()), CompactUserBean.fromEntity(entity.getModifier()), 
				CompactFolderBean.fromEntity(entity.getParentFolder()), typeProperties, mayView, mayEdit, mayDelete);
	}

	private Long id;
  
  private MaterialType type;

  private String subtype;

  private String urlName;
  
  private String title;
  
  private MaterialPublicity publicity;
  
  private CompactLanguageBean language;
  
  private Date modified;
  
  private Date created;

  private CompactUserBean creator;
  
  private CompactUserBean modifier;
  
  private CompactFolderBean parentFolder;
  
  private Boolean mayEdit;
  
  private Boolean mayView;
  
  private Boolean mayDelete;
  
  private TypeProperties typeProperties;
  
  public static class TypeProperties {
  	
  	public TypeProperties(Boolean printableToPdf, Boolean editable) {
	    this.printableToPdf = printableToPdf;
	    this.editable = editable;
    }
  	
  	public Boolean getEditable() {
	    return editable;
    }
  	
  	public void setEditable(Boolean editable) {
	    this.editable = editable;
    }
  	
  	public Boolean getPrintableToPdf() {
	    return printableToPdf;
    }
  	
  	public void setPrintableToPdf(Boolean printableToPdf) {
	    this.printableToPdf = printableToPdf;
    }

		private Boolean printableToPdf;
  	
  	private Boolean editable;
  }
}
