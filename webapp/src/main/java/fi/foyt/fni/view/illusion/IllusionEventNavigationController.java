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
  
  public String getSelectedItem() {
    return selectedItem;
  }
  
  public void setSelectedItem(SelectedItem selectedItem) {
    this.selectedItem = selectedItem.name();
  }
  
  public void setSelectedPage(Long id) {
    this.selectedItem = "PAGE-" + id;
  }

  private String eventUrlName;
  private String selectedItem;
  
  public static enum SelectedItem {
    INDEX,
    MATERIALS,
    PARTICIPANTS,
    GROUPS,
    SETTINGS
  }
}
