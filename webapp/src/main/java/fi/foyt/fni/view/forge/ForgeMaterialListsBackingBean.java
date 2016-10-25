package fi.foyt.fni.view.forge;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import fi.foyt.fni.security.LoggedIn;

@SessionScoped
@Named
@Stateful
@LoggedIn
public class ForgeMaterialListsBackingBean {

  @PostConstruct
  @TransactionAttribute (TransactionAttributeType.NOT_SUPPORTED)
  public void init() {
    setMaterialsOpen(true);
    setLastViewedOpen(true);
    setStarredOpen(true);
    setLastEditedOpen(true);
  }

  public boolean isMaterialsOpen() {
    return materialsOpen;
  }

  public void setMaterialsOpen(boolean materialsOpen) {
    this.materialsOpen = materialsOpen;
  }

  public boolean isLastViewedOpen() {
    return lastViewedOpen;
  }

  public void setLastViewedOpen(boolean lastViewedOpen) {
    this.lastViewedOpen = lastViewedOpen;
  }

  public boolean isStarredOpen() {
    return starredOpen;
  }

  public void setStarredOpen(boolean starredOpen) {
    this.starredOpen = starredOpen;
  }

  public boolean isLastEditedOpen() {
    return lastEditedOpen;
  }

  public void setLastEditedOpen(boolean lastEditedOpen) {
    this.lastEditedOpen = lastEditedOpen;
  }

  public void closeMaterialsList() {
    setMaterialsOpen(false);
  }

  public void openMaterialsList() {
    setMaterialsOpen(true);
  }

  public void closeLastViewedList() {
    setLastViewedOpen(false);
  }

  public void openLastViewedList() {
    setLastViewedOpen(true);
  }

  public void closeLastEditedList() {
    setLastEditedOpen(false);
  }

  public void openLastEditedList() {
    setLastEditedOpen(true);
  }

  public void closeStarredList() {
    setStarredOpen(false);
  }

  public void openStarredList() {
    setStarredOpen(true);
  }
  
  private boolean materialsOpen;
  private boolean lastViewedOpen;
  private boolean starredOpen;
  private boolean lastEditedOpen;
}
