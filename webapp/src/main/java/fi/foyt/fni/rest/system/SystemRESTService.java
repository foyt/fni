package fi.foyt.fni.rest.system;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.foyt.fni.rest.Security;
import fi.foyt.fni.rest.illusion.OAuthScopes;
import fi.foyt.fni.system.SearchController;

@Path ("/system")
@RequestScoped
@Produces (MediaType.APPLICATION_JSON)
public class SystemRESTService {
  
  @PersistenceUnit
  private EntityManagerFactory entityManagerFactory;
  
  @Inject
  private SearchController searchController;

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
    scopes = { }
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
  
}
