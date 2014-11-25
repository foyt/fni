package fi.foyt.fni.dropbox;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.materials.DropboxRootFolderDAO;
import fi.foyt.fni.persistence.model.materials.DropboxRootFolder;

@Singleton
public class DropboxSynchronizationScheduler {

  @Inject
  private Logger logger;

  @Inject
  private MaterialController materialController;

  @Inject
  private DropboxRootFolderDAO dropboxRootFolderDAO;

  @Schedule(dayOfWeek = "*", hour = "*", minute = "15,45", second = "0", year = "*", persistent = false)
  public synchronized void synchronizeFolders() {
    logger.info("Scheduled Dropbox folders synchronization started");
    
    List<DropboxRootFolder> dropboxRootFolders = dropboxRootFolderDAO.listAllSortByAscLastSynchronized(0, 10);
    for (DropboxRootFolder dropboxRootFolder : dropboxRootFolders) {
      logger.info("Synchronizing Dropbox folder of user " + dropboxRootFolder.getCreator().getId() + " - last synchronized " + dropboxRootFolder.getLastSynchronized());
      materialController.synchronizeDropboxFolder(dropboxRootFolder);
      logger.info("Dropbox folder of user " + dropboxRootFolder.getCreator().getId() + " synchronized");
    }

    logger.info("Scheduled Dropbox folders synchronization ended");
  }

}
