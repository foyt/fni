package fi.foyt.fni.view.illusion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/", to = "/illusion/index.jsf")
public class IllusionIndexBackingBean {

  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private UserController userController;
  
  @RequestAction
  public String init() {
    upcomingEvents = new ArrayList<>();
    pastEvents = new ArrayList<>();
    
    for (IllusionEvent illusionEvent : illusionEventController.listNextIllusionEvents(1000)) {
      upcomingEvents.add(createEventPojo(illusionEvent));
    }

    for (IllusionEvent illusionEvent : illusionEventController.listPastIllusionEvents(1000)) {
      pastEvents.add(createEventPojo(illusionEvent));
    }
    
    return null;
  }
  
  private Event createEventPojo(IllusionEvent event) {
    List<EventOrganizer> organizers = new ArrayList<>();
    
    for (IllusionEventParticipant organizer : illusionEventController.listIllusionEventParticipantsByEventAndRole(event, IllusionEventParticipantRole.ORGANIZER)) {
      organizers.add(new EventOrganizer(organizer.getUser().getId(), organizer.getId(), userController.getUserDisplayName(organizer.getUser())));
    }
    
    IllusionEventParticipantRole role = null;
    
    if (sessionController.isLoggedIn()) {
      User loggedUser = sessionController.getLoggedUser();
      IllusionEventParticipant eventParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, loggedUser);
      if (eventParticipant != null) {
        role = eventParticipant.getRole();
      }
    }

    return new Event(event.getName(), event.getUrlName(), event.getDescription(), event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getEndTime(), organizers, role);
  }

  public List<Event> getUpcomingEvents() {
    return upcomingEvents;
  }
  
  public List<Event> getPastEvents() {
    return pastEvents;
  }
  
  private List<Event> upcomingEvents;
  private List<Event> pastEvents;
  
  public class Event {
    
    public Event(String name, String urlName, String description, Date startDate, Date startTime, Date endDate, Date endTime, List<EventOrganizer> organizers,
        IllusionEventParticipantRole role) {
      super();
      this.name = name;
      this.urlName = urlName;
      this.description = description;
      this.startDate = startDate;
      this.startTime = startTime;
      this.endDate = endDate;
      this.endTime = endTime;
      this.organizers = organizers;
      this.role = role;
    }

    public String getName() {
      return name;
    }
    
    public String getUrlName() {
      return urlName;
    }
    
    public String getDescription() {
      return description;
    }
    
    public Date getStartDate() {
      return startDate;
    }
    
    public Date getStartTime() {
      return startTime;
    }
    
    public Date getEndDate() {
      return endDate;
    }
    
    public Date getEndTime() {
      return endTime;
    }
    
    public List<EventOrganizer> getOrganizers() {
      return organizers;
    }
    
    public IllusionEventParticipantRole getRole() {
      return role;
    }

    private String name;
    private String urlName;
    private String description;
    private Date startDate;
    private Date startTime;
    private Date endDate;
    private Date endTime;
    private List<EventOrganizer> organizers;
    private IllusionEventParticipantRole role;
  }
  
  public class EventOrganizer {
 
    public EventOrganizer(Long userId, Long participantId, String name) {
      this.userId = userId;
      this.participantId = participantId;
      this.name = name;
    }

    public String getName() {
      return name;
    }
    
    public Long getParticipantId() {
      return participantId;
    }
    
    public Long getUserId() {
      return userId;
    }
    
    private Long userId;
    private Long participantId;
    private String name;
  }
}
