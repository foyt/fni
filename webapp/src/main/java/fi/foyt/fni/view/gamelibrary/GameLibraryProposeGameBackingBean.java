package fi.foyt.fni.view.gamelibrary;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;

import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.mail.Mailer;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.PermissionController;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.temp.SessionTempController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.faces.FacesUtils;
import fi.foyt.fni.utils.images.ImageUtils;
import fi.foyt.fni.utils.licenses.CreativeCommonsUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/proposegame/", to = "/gamelibrary/proposegame.jsf")
@LoggedIn
public class GameLibraryProposeGameBackingBean {
  
  @Inject
  private Logger logger;

	@Inject
	private GameLibraryTagController gameLibraryTagController;
	
	@Inject
	private PublicationController publicationController;

	@Inject
	private SessionController sessionController;

  @Inject
	private SystemSettingsController systemSettingsController;

  @Inject
  private SessionTempController sessionTempController;

  @Inject
  private UserController userController;

  @Inject
  private PermissionController permissionController;
  
  @Inject
  private Mailer mailer;
  
  @RequestAction
  @IgnorePostback
  @Deferred
  public void defaults() {
    languageId = systemSettingsController.getDefaultLanguage().getId();
    creativeCommonsCommercial = CreativeCommonsCommercial.YES;
    creativeCommonsDerivatives = CreativeCommonsDerivatives.SHARE_ALIKE;
    authorsShare = 0d;
  }
  
  @RequestAction
  @Deferred
  public void init() {
    languages = systemSettingsController.listLanguages();
    List<String> existingTags = new ArrayList<>();
    
    List<GameLibraryTag> gameLibraryTags = gameLibraryTagController.listGameLibraryTags();
    for (GameLibraryTag gameLibraryTag : gameLibraryTags) {
      existingTags.add(gameLibraryTag.getText());
    }

    this.existingTags = StringUtils.join(existingTags, ',');
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }

  public Long getLanguageId() {
    return languageId;
  }
  
  public void setLanguageId(Long languageId) {
    this.languageId = languageId;
  }
  
  public LicenseType getLicenseType() {
    return licenseType;
  }
  
  public void setLicenseType(LicenseType licenseType) {
    this.licenseType = licenseType;
  }

  public CreativeCommonsDerivatives getCreativeCommonsDerivatives() {
    return creativeCommonsDerivatives;
  }
  
  public void setCreativeCommonsDerivatives(CreativeCommonsDerivatives creativeCommonsDerivatives) {
    this.creativeCommonsDerivatives = creativeCommonsDerivatives;
  }
  
  public CreativeCommonsCommercial getCreativeCommonsCommercial() {
    return creativeCommonsCommercial;
  }
  
  public void setCreativeCommonsCommercial(CreativeCommonsCommercial creativeCommonsCommercial) {
    this.creativeCommonsCommercial = creativeCommonsCommercial;
  }
  
  public String getLicenseOther() {
    return licenseOther;
  }
  
  public void setLicenseOther(String licenseOther) {
    this.licenseOther = licenseOther;
  }
  
  public String getTags() {
    return tags;
  }
  
  public void setTags(String tags) {
    this.tags = tags;
  }
  
  public String getDownloadableFileId() {
    return downloadableFileId;
  }
  
  public void setDownloadableFileId(String downloadableFileId) {
    this.downloadableFileId = downloadableFileId;
  }
  
  public String getDownloadableFileName() {
    return downloadableFileName;
  }
  
  public void setDownloadableFileName(String downloadableFileName) {
    this.downloadableFileName = downloadableFileName;
  }
  
  public String getDownloadableContentType() {
    return downloadableContentType;
  }
  
  public void setDownloadableContentType(String downloadableContentType) {
    this.downloadableContentType = downloadableContentType;
  }
  
  public String getPrintableFileId() {
    return printableFileId;
  }
  
  public void setPrintableFileId(String printableFileId) {
    this.printableFileId = printableFileId;
  }
  
  public String getPrintableFileName() {
    return printableFileName;
  }
  
  public void setPrintableFileName(String printableFileName) {
    this.printableFileName = printableFileName;
  }
  
  public String getPrintableContentType() {
    return printableContentType;
  }
  
  public void setPrintableContentType(String printableContentType) {
    this.printableContentType = printableContentType;
  }
  
  public String getImageFileId() {
    return imageFileId;
  }
  
  public void setImageFileId(String imageFileId) {
    this.imageFileId = imageFileId;
  }
  
  public String getImageFileName() {
    return imageFileName;
  }
  
  public void setImageFileName(String imageFileName) {
    this.imageFileName = imageFileName;
  }
  
  public String getImageContentType() {
    return imageContentType;
  }
  
  public void setImageContentType(String imageContentType) {
    this.imageContentType = imageContentType;
  }
  
  public Double getAuthorsShare() {
    return authorsShare;
  }
  
  public void setAuthorsShare(Double authorsShare) {
    this.authorsShare = authorsShare;
  }
  
  public List<Language> getLanguages() {
    return languages;
  }
  
  public String getExistingTags() {
    return existingTags;
  }

  public synchronized String send() throws IOException, MessagingException {
    TypedData imageData;

    if (StringUtils.isNotBlank(getImageFileId())) {
      byte[] fileData = sessionTempController.getTempFileData(getImageFileId());
      
      BufferedImage bufferedImage = null;
      try {
        bufferedImage = ImageUtils.readBufferedImage(new TypedData(fileData, getImageContentType()));
      } catch (Exception e) {
        logger.log(Level.WARNING, "Failed read image as buffered image", e);
      }
      
      if (bufferedImage == null) {
        FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("gamelibrary.proposegame.unsupportedImageFormatMessage"));
        return null;
      } else {
        if ((bufferedImage.getWidth() < 400)||(bufferedImage.getHeight() < 400)) {
          FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("gamelibrary.proposegame.imageTooSmallMessage"));
          return null;
        }
        
        if ((bufferedImage.getWidth() > 1024)||(bufferedImage.getHeight() > 1024)) {
          BufferedImage resizedImage = ImageUtils.resizeImage(bufferedImage, 1024, 1024, null);
          if (resizedImage == null) {
            FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("gamelibrary.proposegame.unsupportedImageFormatMessage"));
            return null;
          }
          
          imageData = ImageUtils.writeBufferedImage(resizedImage);
        } else {
          imageData = ImageUtils.writeBufferedImage(bufferedImage);
        }
      }
    } else {
      FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("gamelibrary.proposegame.imageRequiredMessage"));
      return null;
    }

    if (StringUtils.isBlank(getDownloadableFileId()) && StringUtils.isBlank(getPrintableFileId())) {
      FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("gamelibrary.proposegame.pdfFileRequiredMessage"));
      return null;
    }

    if (StringUtils.isNotBlank(getDownloadableContentType()) && !StringUtils.equals(getDownloadableContentType(), "application/pdf")) {
      FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("gamelibrary.proposegame.invalidDownloadablePdfTypeMessage"));
      return null;
    }
    
    if (StringUtils.isNotBlank(getPrintableContentType()) && !StringUtils.equals(getPrintableContentType(), "application/pdf")) {
      FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("gamelibrary.proposegame.invalidPrintablePdfTypeMessage"));
      return null;
    }
    
    Language language = systemSettingsController.findLanguageById(getLanguageId());
    
    String license = null;
    
    switch (getLicenseType()) {
      case CREATIVE_COMMONS:
        boolean derivatives = getCreativeCommonsDerivatives() != CreativeCommonsDerivatives.NO;
        boolean shareAlike = getCreativeCommonsDerivatives() == CreativeCommonsDerivatives.SHARE_ALIKE;
        boolean commercial = getCreativeCommonsCommercial() == CreativeCommonsCommercial.YES;
        license = CreativeCommonsUtils.createLicenseUrl(true, derivatives, shareAlike, commercial);
      break;
      case OTHER:
        license = getLicenseOther();
      break;
    }
    
    List<GameLibraryTag> gameLibraryTags = new ArrayList<>();
    
    for (String tag : StringUtils.split(tags, ',')) {
      GameLibraryTag gameLibraryTag = gameLibraryTagController.findTagByText(tag);
      if (gameLibraryTag == null) {
        gameLibraryTag = gameLibraryTagController.createTag(tag);
      }
      
      gameLibraryTags.add(gameLibraryTag);
    }
    
    BookPublication publication = publicationController.createBookPublication(sessionController.getLoggedUser(), getName(), getDescription(), 0d, getAuthorsShare(), null, null, null, null, null, null, license, gameLibraryTags, language);
    
    if (StringUtils.isNotBlank(getDownloadableFileId())) {
      byte[] fileData = sessionTempController.getTempFileData(getDownloadableFileId());
      publicationController.setBookPublicationDownloadableFile(publication, fileData, getDownloadableContentType(), sessionController.getLoggedUser());
    } 
    
    if (StringUtils.isNotBlank(getPrintableFileId())) {
      byte[] fileData = sessionTempController.getTempFileData(getPrintableFileId());
      publicationController.setBookPublicationPrintableFile(publication, fileData, getPrintableContentType(), sessionController.getLoggedUser());
    } 

    PublicationImage publicationImage = publicationController.createPublicationImage(publication, imageData.getData(), imageData.getContentType(), sessionController.getLoggedUser());
    publicationController.updatePublicationDefaultImage(publication, publicationImage);

    sendNotifications(publication);
    
    return "/gamelibrary/publication.jsf?faces-redirect=true&urlName=" + publication.getUrlName();
  }
  
  private void sendNotifications(BookPublication publication) throws MessagingException {
    boolean success = false;
    
    List<User> librarians = permissionController.listUsersByPermission(Permission.GAMELIBRARY_MANAGE_PUBLICATIONS);
    for (User librarian : librarians) {
      if (sendNotificationEmail(librarian, publication)) {
        success = true;
      }
    }
    
    if (!success) {
      throw new MessagingException("Could not send notification mail");
    }
  }
  
  private boolean sendNotificationEmail(User librarian, BookPublication publication) {
    String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);

    Locale locale = LocaleUtils.toLocale(librarian.getLocale());
    String librarianMail = userController.getUserPrimaryEmail(librarian);
    String librarianName = librarian.getFullName();
    User publisher = publication.getCreator();
    String publisherName = publisher.getFullName();
    String publisherEmail = userController.getUserPrimaryEmail(publisher);
    String publicationName = publication.getName();
    String publicationUrl = FacesUtils.getLocalAddress(true) + "/gamelibrary/" + publication.getUrlName();
    String libraryManagementUrl = FacesUtils.getLocalAddress(true) + "/gamelibrary/manage/";
    
    try {
      String subject = ExternalLocales.getText(locale, "gamelibrary.mail.newProposal.subject");
      String content = ExternalLocales.getText(locale, "gamelibrary.mail.newProposal.content", librarianName, publisherName, publisherEmail, publicationName, publicationUrl, libraryManagementUrl);
      
      mailer.sendMail(fromMail, fromName, librarianMail, librarianName, subject, content, "text/plain");
    } catch (MessagingException e) {
      logger.log(Level.WARNING, "Could not send an notification email", e);
      return false;
    }
    
    return true;
  }
  
	private String name;
	private String description;
  private Long languageId;
  private LicenseType licenseType;
	private List<Language> languages;
	private String existingTags;
	private CreativeCommonsDerivatives creativeCommonsDerivatives;
	private CreativeCommonsCommercial creativeCommonsCommercial;
	private String licenseOther;
	private String tags;
	private String downloadableFileId;
  private String downloadableFileName;
	private String downloadableContentType;
	private String printableFileId;
	private String printableFileName;
	private String printableContentType;
	private String imageFileId;
	private String imageFileName;
	private String imageContentType;
	private Double authorsShare;
  
 	public enum LicenseType {
		CREATIVE_COMMONS,
		OTHER
	}
 	
 	public enum CreativeCommonsDerivatives {
 		YES,
 		NO,
 		SHARE_ALIKE
 	}
 	
 	public enum CreativeCommonsCommercial {
 		YES,
 		NO
 	}
}
