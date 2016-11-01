package fi.foyt.fni.rest.system;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.ArrayUtils;

import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.rest.Security;
import fi.foyt.fni.rest.illusion.OAuthScopes;
import fi.foyt.fni.rest.system.model.SystemSetting;
import fi.foyt.fni.system.SearchController;
import fi.foyt.fni.system.SystemSettingsController;

@Path ("/system")
@RequestScoped
@Produces (MediaType.APPLICATION_JSON)
public class SystemRESTService {
  
  private static SystemSettingKey[] PUBLIC_SETTING_KEYS = {
    SystemSettingKey.PUBLISH_GUIDE_EN,
    SystemSettingKey.PUBLISH_GUIDE_FI
  };
  
  @PersistenceUnit
  private EntityManagerFactory entityManagerFactory;
  
  @Inject
  private Logger logger;

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private SearchController searchController;

  /**
   * Returns pong
   * 
   * @return pong
   */
  @GET
  @Path ("/settings/{KEY}")
  @Security (
    allowNotLogged = true,
    scopes = {
      OAuthScopes.SYSTEM_SETTINGS_FIND
    }
  )  
  public Response getSetting(@PathParam ("KEY") SystemSettingKey key) {
    if (!ArrayUtils.contains(PUBLIC_SETTING_KEYS, key)) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    return Response.ok(new SystemSetting(key, systemSettingsController.getSetting(key))).build();
  }

  /**
   * Returns pong
   * 
   * @return pong
   */
  @GET
  @Path ("/ping")
  @Produces (MediaType.TEXT_PLAIN)
  @Security (
    allowNotLogged = true,
    scopes = {}
  )  
  public Response getPing() {
    return Response.ok("pong").build();
  }
  
  @GET
  @Path ("/jpa/cache/flush")
  @Produces (MediaType.TEXT_PLAIN)
  @Security (
    allowService = true,
    scopes = { 
      OAuthScopes.SYSTEM_JPA_CACHE_FLUSH  
    }
  ) 
  public Response flushCaches() {
    entityManagerFactory.getCache().evictAll();
    return Response.ok("ok").build();
  }
  
  @GET
  @Path ("/search/reindex")
  @Produces (MediaType.TEXT_PLAIN)
  @Security (
    allowService = true,
    scopes = { 
      OAuthScopes.SYSTEM_REINDEX_SEARCH  
    }
  ) 
  public Response reindexSearch() {
    searchController.reindexEntities();
    return Response.ok("ok").build();
  }
  
  @GET
  @Path ("/log")
  @Produces (MediaType.TEXT_PLAIN)
  @Security (
    allowService = true,
    scopes = { }
  ) 
  public Response flushCaches(@QueryParam ("text") String text) {
    if (!systemSettingsController.getTestMode()) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    logger.log(Level.INFO, text);
    return Response.ok("ok").build();
  }
}
