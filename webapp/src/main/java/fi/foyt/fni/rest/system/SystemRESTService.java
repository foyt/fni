package fi.foyt.fni.rest.system;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.foyt.fni.rest.Unsecure;

@Path ("/system")
@RequestScoped
@Produces (MediaType.APPLICATION_JSON)
public class SystemRESTService {

  @GET
  @Unsecure
  @Path ("/ping")
  @Produces (MediaType.TEXT_PLAIN)
  public Response getPing() {
    return Response.ok("pong").build();
  }
  
}
