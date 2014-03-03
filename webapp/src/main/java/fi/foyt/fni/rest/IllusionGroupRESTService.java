package fi.foyt.fni.rest;

import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUser;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

@Path ("/illusion/group/{GROUPURLNAME}")
@RequestScoped
@Produces (MediaType.APPLICATION_JSON)
public class IllusionGroupRESTService {

  @Inject
  private IllusionGroupController illusionGroupController;
  
  @Inject
  private SessionController sessionController;
  
  @GET
  @Path ("/settings/")
  public Response getSettings(@PathParam ("GROUPURLNAME") String groupUrlName) {
    if (StringUtils.isBlank(groupUrlName)) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!sessionController.isLoggedIn()) {
      return Response.status(Status.UNAUTHORIZED).build();
    }

    User loggedUser = sessionController.getLoggedUser();
    IllusionGroup group = illusionGroupController.findIllusionGroupByUrlName(groupUrlName);
    if (group == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    IllusionGroupUser groupUser = illusionGroupController.findIllusionGroupUserByUserAndGroup(group, loggedUser);
    if (groupUser == null) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    Map<IllusionGroupSettingKey, Object> settings = illusionGroupController.getIllusionGroupUserSettings(groupUser);
    
    return Response.ok(settings).build();
  }

  @GET
  @Path ("/settings/{KEY}")
  public Response getSetting(@PathParam ("GROUPURLNAME") String groupUrlName, @PathParam ("KEY") IllusionGroupSettingKey key) {
    if (StringUtils.isBlank(groupUrlName)) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!sessionController.isLoggedIn()) {
      return Response.status(Status.UNAUTHORIZED).build();
    }

    User loggedUser = sessionController.getLoggedUser();
    IllusionGroup group = illusionGroupController.findIllusionGroupByUrlName(groupUrlName);
    if (group == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    IllusionGroupUser groupUser = illusionGroupController.findIllusionGroupUserByUserAndGroup(group, loggedUser);
    if (groupUser == null) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    return Response.ok(illusionGroupController.getIllusionGroupUserSetting(groupUser, key)).build();
  }

  @PUT
  @Path ("/settings/{KEY}")
  public Response saveSetting(@PathParam ("GROUPURLNAME") String groupUrlName, @PathParam ("KEY") IllusionGroupSettingKey key, String value) {
    if (StringUtils.isBlank(groupUrlName)) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!sessionController.isLoggedIn()) {
      return Response.status(Status.UNAUTHORIZED).build();
    }

    User loggedUser = sessionController.getLoggedUser();
    IllusionGroup group = illusionGroupController.findIllusionGroupByUrlName(groupUrlName);
    if (group == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    IllusionGroupUser groupUser = illusionGroupController.findIllusionGroupUserByUserAndGroup(group, loggedUser);
    if (groupUser == null) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    illusionGroupController.setIllusionGroupSettingValue(groupUser, key, value);
    
    
    return Response.noContent().build();
  }
  
}
