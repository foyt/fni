package fi.foyt.fni.view.forge;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.materials.TagWithCount;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.MaterialTag;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.utils.licenses.CreativeCommonsLicense;
import fi.foyt.fni.utils.licenses.CreativeCommonsUtils;

public abstract class AbstractForgePublicViewBackingBean {

  protected static final MaterialType[] SUPPORTED_TYPES = { 
      MaterialType.DOCUMENT, 
      MaterialType.IMAGE, 
      MaterialType.PDF,
      MaterialType.FILE, 
      MaterialType.BINARY, 
      MaterialType.VECTOR_IMAGE, 
      MaterialType.GOOGLE_DOCUMENT,
      MaterialType.DROPBOX_FILE 
  };

  protected static final int TAG_COUNT = 30;
  
  @Inject
  private MaterialController materialController;
  
  protected PublicMaterialBean toMaterialBean(fi.foyt.fni.persistence.model.materials.Material material) {
    String icon = materialController.getMaterialIcon(material.getType());
    String license = material.getLicense();
    CreativeCommonsLicense commonsLicence = CreativeCommonsUtils.parseLicenseUrl(license);
    String creatorName = material.getCreator().getFullName();
    String modifierName = material.getModifier().getFullName();
    List<MaterialTag> materialTags = materialController.listMaterialTags(material);
    List<String> tags = new ArrayList<>(materialTags.size());
    
    for (MaterialTag materialTag : materialTags) {
      tags.add(materialTag.getTag().getText());
    }
    
    String viewPath = String.format("/materials/%s", material.getPath());
    String editPath = materialController.getForgeMaterialViewerUrl(material);
    String downloadLink = String.format("/materials/%s?download=true", material.getPath());
    String path = material.getPath();
    
    String description = material.getDescription();
    if (StringUtils.isBlank(description) && (material instanceof Document)) {
      description = StringUtils.abbreviate(((Document) material).getContentPlain(), 250);
    }
    
    Boolean viewable;
    switch (material.getType()) {
      case DOCUMENT:
      case IMAGE:
      case PDF:
      case VECTOR_IMAGE:
        viewable = true;
      break;
      default:
        viewable = false;
      break;
    }
    
    Boolean printable = materialController.isPrintableAsPdfType(material.getType());
    
    return new PublicMaterialBean(
        material.getId(), 
        material.getTitle(), 
        description,
        icon, 
        license,
        commonsLicence != null ? commonsLicence.getIconUrl(true) : null, 
        material.getCreator().getId(),
        creatorName, 
        material.getCreated(),
        material.getModifier().getId(),
        modifierName,
        material.getModified(), 
        tags,
        viewPath,
        editPath,
        downloadLink,
        path,
        viewable,
        printable);
  }
  
  protected List<PublicMaterialBean> toMaterialBeans(List<fi.foyt.fni.persistence.model.materials.Material> materials) {
    List<PublicMaterialBean> result = new ArrayList<>(materials.size());

    for (fi.foyt.fni.persistence.model.materials.Material material : materials) {
      result.add(toMaterialBean(material));
    }

    return result;
  }
  
  protected List<PublicTagBean> toTagBeans(List<TagWithCount> tagsWithCounts) {
    List<PublicTagBean> result = new ArrayList<>(tagsWithCounts.size());
    
    for (TagWithCount tagWithCount : tagsWithCounts) {
      result.add(new PublicTagBean(tagWithCount.getCount(), tagWithCount.getTag().getText()));
    }
    
    return result;
  }
  
}
