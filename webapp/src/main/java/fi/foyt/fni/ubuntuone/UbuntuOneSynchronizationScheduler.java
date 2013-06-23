package fi.foyt.fni.ubuntuone;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.materials.UbuntuOneRootFolderDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.model.materials.UbuntuOneRootFolder;

@Stateless
public class UbuntuOneSynchronizationScheduler {

  @Inject
  private Logger logger;

  @Inject
  private UbuntuOneManager ubuntuOneManager;
  
  @Inject
  @DAO
  private UserDAO userDAO;
  
  @Inject
  @DAO
  private UbuntuOneRootFolderDAO ubuntuOneRootFolderDAO;

  @Schedule(dayOfWeek = "*", hour = "*", minute = "0,30", second = "0", year = "*", persistent = false)
  public synchronized void synchronizeFolders() {
    logger.info("Scheduled Ubuntu One folders synchronization started");
    
    List<UbuntuOneRootFolder> ubuntuOneRootFolders = ubuntuOneRootFolderDAO.listAllSortByAscLastSynchronized(0, 10);
    for (UbuntuOneRootFolder ubuntuOneRootFolder : ubuntuOneRootFolders) {
      logger.info("Synchronizing Ubuntu One folder of user " + ubuntuOneRootFolder.getCreator().getId() + " - last synchronized " + ubuntuOneRootFolder.getLastSynchronized());
      ubuntuOneManager.synchronizeFolder(ubuntuOneRootFolder);
      logger.info("Ubuntu One folder of user " + ubuntuOneRootFolder.getCreator().getId() + " synchronized");
    }

    logger.info("Scheduled Ubuntu One folders synchronization ended");
  }

}
