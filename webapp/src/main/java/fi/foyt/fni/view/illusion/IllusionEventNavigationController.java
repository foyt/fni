package fi.foyt.fni.view.illusion;

import javax.enterprise.context.RequestScoped;

import fi.foyt.fni.illusion.IllusionEventPage;

@RequestScoped
public class IllusionEventNavigationController {
  
  public String getEventUrlName() {
    return eventUrlName;
  }
  
  public void setEventUrlName(String eventUrlName) {
    this.eventUrlName = eventUrlName;
  }
  
  public String getSelectedPage() {
    return selectedPage;
  }
  
  public void setSelectedPage(String selectedPage) {
    this.selectedPage = selectedPage;
  }

  public void setSelectedPage(IllusionEventPage.Static selectedItem) {
    setSelectedPage(selectedItem.name());
  }
  
  private String eventUrlName;
  private String selectedPage;
}
