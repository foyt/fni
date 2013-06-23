package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.scribe.model.Response;

import fi.foyt.fni.dropbox.DropboxManager;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.materials.DropboxFileDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.materials.UbuntuOneFileDAO;
import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;
import fi.foyt.fni.persistence.model.materials.DropboxFile;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.UbuntuOneFile;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.ubuntuone.UbuntuOneManager;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;
import fi.foyt.fni.view.ViewControllerException;

@RequestScoped
@Stateful
public class ViewVectorImageViewController extends AbstractViewController {

  @Inject
  private Logger logger;

  @Inject
  private SessionController sessionController;

  @Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private DropboxManager dropboxManager;
  
  @Inject
  private UbuntuOneManager ubuntuOneManager;
  
	@Inject
	@DAO
	private MaterialDAO materialDAO;

  @Inject
  @DAO
	private VectorImageDAO vectorImageDAO;
	
  @Inject
  @DAO
  private UbuntuOneFileDAO ubuntuOneFileDAO;
  
  @Inject
  @DAO
  private DropboxFileDAO dropboxFileDAO;

  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    Long materialId = context.getLongParameter("materialId");
    Material material = materialDAO.findById(materialId);
    return materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material);
  }

  @Override
  public void execute(ViewControllerContext context) {
    Long materialId = context.getLongParameter("materialId");
    Locale locale = context.getRequest().getLocale();
    
    String data = null;
    Material material = materialDAO.findById(materialId);
    switch (material.getType()) {
      case DROPBOX_FILE:
        DropboxFile dropboxFile = dropboxFileDAO.findById(materialId);
        try {
          Response response = dropboxManager.getFileContent(sessionController.getLoggedUser(), dropboxFile);
          if (response.getCode() == HttpURLConnection.HTTP_OK) {
            data = response.getBody();
          } else {
            logger.log(Level.SEVERE, "Failed to download vector image from Dropbox. Error code: " + response.getCode());
            throw new ViewControllerException(Locales.getText(locale, "generic.error.dropbox.dropboxFetchError"));
          }
        } catch (IOException e1) {
          logger.log(Level.SEVERE, "Failed to download vector image from Dropbox", e1);
          throw new ViewControllerException(Locales.getText(locale, "generic.error.dropbox.dropboxFetchError"));
        }
      break;
      case UBUNTU_ONE_FILE:
        UbuntuOneFile ubuntuOneFile = ubuntuOneFileDAO.findById(materialId);
        try {
          Response response = ubuntuOneManager.getFileContent(sessionController.getLoggedUser(), ubuntuOneFile);
          if (response.getCode() == HttpURLConnection.HTTP_OK) {
            data = response.getBody();
          } else {
            logger.log(Level.SEVERE, "Failed to download vector image from Ubuntu One. Error code: " + response.getCode());
            throw new ViewControllerException(Locales.getText(locale, "generic.error.ubuntuOne.ubuntuOneFetchError"));
          }
        } catch (IOException e) {
          logger.log(Level.SEVERE, "Failed to download vector image from Ubuntu One", e);
          throw new ViewControllerException(Locales.getText(locale, "generic.error.ubuntuOne.ubuntuOneFetchError"));
        }
      break;
      case VECTOR_IMAGE:
        VectorImage vectorImage = vectorImageDAO.findById(materialId);
        data = vectorImage.getData();
      break;
      default:
      break;
    }
    
    context.getRequest().setAttribute("data", data);
    
    context.setIncludeJSP("/jsp/forge/viewvectorimage.jsp");
  }
}