package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.gdata.data.Link;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.acl.AclScope.Type;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.FolderEntry;
import com.google.gdata.util.ServiceException;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.common.LanguageDAO;
import fi.foyt.fni.persistence.dao.materials.GoogleDocumentDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.GoogleDocumentType;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.AuthUtils;
import fi.foyt.fni.utils.gdocs.GoogleDocumentsClient;
import fi.foyt.fni.utils.gdocs.GoogleDocumentsUtils;
import fi.foyt.fni.utils.gdocs.GooleDocumentsException;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;
import fi.foyt.fni.view.ViewControllerException;

@RequestScoped
@Stateful
public class ImportGoogleDocumentsDialogViewController extends AbstractViewController {
  
  private static final String DOCUMENTS_SCOPE = "https://docs.google.com/feeds/";

	@Inject
	private SystemSettingsController systemSettingsController;

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialController materialController;

	@Inject
	@DAO
	private GoogleDocumentDAO googleDocumentDAO;

	@Inject
	@DAO
	private LanguageDAO languageDAO;
  
	@Inject
  @DAO	
	private UserTokenDAO userTokenDAO;
	
  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return sessionController.isLoggedIn();
  }

  @Override
  public void execute(ViewControllerContext context) {
    UserToken userToken = sessionController.getLoggedUserToken();
    
    Locale locale = context.getRequest().getLocale();
    
    // TODO: We need to be logged with google
    
    if (AuthUtils.getInstance().isExpired(userToken) || !AuthUtils.getInstance().isGrantedScope(userToken, DOCUMENTS_SCOPE)) {
      try {
        String redirectUrl = URLEncoder.encode(context.getBasePath() + "/forge/?a=importgoogledocuments", "UTF-8");
        String loginUrl = context.getBasePath() + "/login/?loginMethod=GOOGLE&redirectUrl=" + redirectUrl + "&extraScopes=" + DOCUMENTS_SCOPE;
        String warningMessage = StringEscapeUtils.escapeJava(Locales.getText(locale, "forge.importGoogleDocuments.authenticationWarning"));
        context.getRequest().setAttribute("action", "authenticate");
        context.getRequest().setAttribute("authenticationUrl", loginUrl);
        context.getRequest().setAttribute("warningMessage", warningMessage);
        context.setIncludeJSP("/jsp/forge/importgoogledocumentsdialog.jsp");
      } catch (UnsupportedEncodingException e) {
        throw new ViewControllerException(Locales.getText(locale, "generic.error.configurationError"), e);
      }
    } else {
      String googleDocsUser = systemSettingsController.getSetting("materials.googleDocs.username");
      User loggedUser = userToken.getUserIdentifier().getUser();
      Folder parentFolder = null;
      
      GoogleDocumentsClient googleDocumentsClient = new GoogleDocumentsClient(userToken.getToken());
      if (context.getStringParameter("import") != null) {
        Set<String> entryIds = context.getStringParameters("entryIds");
        for (String entryId : entryIds) {
          if (googleDocumentDAO.findByCreatorAndDocumentId(loggedUser, entryId) == null) {
            try {
              DocumentListEntry entry = googleDocumentsClient.getDocumentListEntry(entryId);
              if (isAcceptedType(entry, false)) {
                if (!googleDocumentsClient.hasAclRole(entryId, googleDocsUser, Arrays.asList(new AclRole[]{ AclRole.OWNER, AclRole.READER, AclRole.WRITER }))) {
                  googleDocumentsClient.addAclRole(AclRole.READER, new AclScope(Type.USER, googleDocsUser), entryId);
                }
                
                GoogleDocumentType documentType = GoogleDocumentsUtils.getTypeByName(googleDocumentsClient.getResourceIdPrefix(entry.getResourceId()));
                String title = entry.getTitle().getPlainText();
                Language language = null;
                if (entry.getContent() == null) {
                  language = languageDAO.findByIso2(entry.getContent().getLang());
                }
                String urlName = materialController.getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
                
                googleDocumentDAO.create(loggedUser, language, parentFolder, urlName, title, entryId, documentType, MaterialPublicity.PRIVATE);
              }
            } catch (GooleDocumentsException e) {
              throw new ViewControllerException(Locales.getText(locale, "generic.error.googledocuments.googleDocumentsError"), e);
            } catch (IOException e) {
              throw new ViewControllerException(Locales.getText(locale, "generic.error.googledocuments.googleCommunicationError"), e);
            } catch (ServiceException e) {
              throw new ViewControllerException(Locales.getText(locale, "generic.error.googledocuments.googleRequestError"), e);
            }              
          }
        }
        context.getRequest().setAttribute("action", "close");        
      } else if (context.getStringParameter("cancel") != null) {
        context.getRequest().setAttribute("action", "close");        
      } else {
        List<GoogleDocumentEntryBean> entryBeans = new ArrayList<ImportGoogleDocumentsDialogViewController.GoogleDocumentEntryBean>();
        
        try {
          String folderId = context.getStringParameter("folderId");
          if (folderId == null) {
            context.getRequest().setAttribute("rootFolder", true);
            
            DocumentListFeed documents = googleDocumentsClient.getHomeDocumentListFeed();
            List<DocumentListEntry> documentEntries = documents.getEntries();
            for (DocumentListEntry documentEntry : documentEntries) {
              if (isAcceptedType(documentEntry, true)) {
                String entryId = googleDocumentsClient.getResourceIdSuffix(documentEntry.getResourceId());
                if (googleDocumentDAO.findByCreatorAndDocumentId(loggedUser, entryId) == null) {
                  entryBeans.add(GoogleDocumentEntryBean.fromEntry(googleDocumentsClient, documentEntry));
                }
              }
            } 
          } else {
            context.getRequest().setAttribute("rootFolder", false);
            List<String> parentIds = new ArrayList<String>();
            
            DocumentListFeed folderFeed = googleDocumentsClient.getFolderDocsListFeed(folderId);
            List<DocumentListEntry> folderEntries = folderFeed.getEntries();
            for (DocumentListEntry folderEntry : folderEntries) {
              List<Link> parentLinks = folderEntry.getParentLinks();
              for (Link parentLink : parentLinks) {
                String parentLinkHref = parentLink.getHref().toString();
                String parentId = googleDocumentsClient.getResourceIdSuffix(parentLinkHref);
                if (!parentIds.contains(folderId))
                  parentIds.add(parentId);
              }
            }
            
            for (String parentId : parentIds) {
              FolderEntry parentFolderEntry = googleDocumentsClient.getFolderEntry(parentId);
              entryBeans.add(GoogleDocumentEntryBean.fromEntry(googleDocumentsClient, parentFolderEntry));
            }
            
            for (DocumentListEntry documentEntry : folderEntries) {
              if (isAcceptedType(documentEntry, true)) {
                String entryId = googleDocumentsClient.getResourceIdSuffix(documentEntry.getResourceId());
                if (googleDocumentDAO.findByCreatorAndDocumentId(loggedUser, entryId) == null) {
                  entryBeans.add(GoogleDocumentEntryBean.fromEntry(googleDocumentsClient, documentEntry));
                }
              }
            }
          }
        } catch (GooleDocumentsException e) {
          throw new ViewControllerException(Locales.getText(locale, "generic.error.googledocuments.googleDocumentsError"), e);
        } catch (IOException e) {
          throw new ViewControllerException(Locales.getText(locale, "generic.error.googledocuments.googleCommunicationError"), e);
        } catch (ServiceException e) {
          throw new ViewControllerException(Locales.getText(locale, "generic.error.googledocuments.googleRequestError"), e);
        }    
      
        context.getRequest().setAttribute("action", "list");
        context.getRequest().setAttribute("documentEntries", entryBeans);
      }

      context.setIncludeJSP("/jsp/forge/importgoogledocumentsdialog.jsp");
    }
  }
  
  private boolean isAcceptedType(DocumentListEntry entry, boolean acceptFolder) {
    GoogleDocumentType documentType = GoogleDocumentsUtils.getTypeByName(entry.getType());
    if (documentType != null) {
      if (documentType == GoogleDocumentType.DOCUMENT)
        return true;
      if (documentType == GoogleDocumentType.DRAWING)
        return true;
      if (documentType == GoogleDocumentType.PRESENTATION)
        return true;
      if (documentType == GoogleDocumentType.SPREADSHEET)
        return true;
      if (documentType == GoogleDocumentType.FOLDER && acceptFolder)
        return true;
    }
    
    return false;
  }
  
  public static class GoogleDocumentEntryBean {
  
    public GoogleDocumentEntryBean(String id, String title, GoogleDocumentType type) {
      this.id = id;
      this.title = title;
      this.type = type;
    }
   
    public String getId() {
      return id;
    }
    
    public String getTitle() {
      return title;
    }
    
    public GoogleDocumentType getType() {
      return type;
    }
    
    public static GoogleDocumentEntryBean fromEntry(GoogleDocumentsClient googleDocumentsClient, DocumentListEntry entry) throws GooleDocumentsException {
      String id = googleDocumentsClient.getResourceIdSuffix(entry.getId());
      return new GoogleDocumentEntryBean(id, entry.getTitle().getPlainText(), GoogleDocumentsUtils.getTypeByName(entry.getType()));
    }
    
    private String id;
    private String title;
    private GoogleDocumentType type;
  }
}