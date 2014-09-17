package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;

@RequestScoped
@Stateful
public class IllusionEventNavigationController {
  
  public String getEventUrlName() {
    return eventUrlName;
  }
  
  public void setEventUrlName(String eventUrlName) {
    this.eventUrlName = eventUrlName;
  }
  
  public SelectedPage getSelectedPage() {
    return selectedPage;
  }
  
  public void setSelectedPage(SelectedPage selectedPage) {
    this.selectedPage = selectedPage;
  }

  private String eventUrlName;
  private SelectedPage selectedPage;
  
  public static enum SelectedPage {
    INDEX,
    MATERIALS,
    PARTICIPANTS,
    GROUPS
  }
}
