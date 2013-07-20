package fi.foyt.fni.api;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import fi.foyt.fni.persistence.dao.illusion.IllusionSessionDAO;
import fi.foyt.fni.persistence.dao.maps.MapDAO;
import fi.foyt.fni.persistence.dao.maps.MapVectorImageLayerDAO;
import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;

@Path("/map")
@RequestScoped
@Stateful
@Produces ("application/json")
public class MapRESTService extends RESTService {
  
	@Inject
	private MapDAO mapDAO;

  @Inject
  private VectorImageDAO vectorImageDAO;

  @Inject
  private MapVectorImageLayerDAO mapVectorImageLayerDAO;
  
  @Inject
	private IllusionSessionDAO illusionSessionDAO;

//	@PUT
//	@POST
//	@Path ("/createMap")
//	public Response createMap(
//      @FormParam ("illusionSessionId") Long illusionSessionId,
//	    @FormParam ("name") String name,
//			@Context UriInfo uriInfo,
//			@Context HttpHeaders httpHeaders) {
//			
//		Locale browserLocale = getBrowserLocale(httpHeaders);
//		User loggedUser = getLoggedUser(httpHeaders);
//
//		if (!hasRole(loggedUser, UserRole.USER)) {
//		  return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
//		}
//		
//		IllusionSession illusionSession = illusionSessionDAO.findById(illusionSessionId);
//		if (illusionSession == null) {
//		  return Response.status(Status.NOT_FOUND).build();
//		}
//		
//		Map map = mapDAO.create(illusionSession, name, loggedUser);
//		
//		TranquilityBuilder tranquilityBuilder = tranquilityBuilderFactory.createBuilder();
//		Tranquility tranquility = tranquilityBuilder.createTranquility()
//		    .addInstruction(tranquilityBuilder.createPropetyTypeInstruction(TranquilModelType.COMPLETE));
//		
//		return Response.status(Response.Status.OK).entity(
//		  new ApiResult<TranquilModelEntity>(
//		    tranquility.entity(map)
//		  )
//		).build();
//	}
	
//	@PUT
//  @POST
//  @Path ("/createVectorImageLayer")
//  public Response createVectorImageLayer(
//      @FormParam ("mapId") Long mapId,
//      @FormParam ("vectorImageId") Long vectorImageId,
//      @FormParam ("name") String name,
//      @Context UriInfo uriInfo,
//      @Context HttpHeaders httpHeaders) {
//      
//    Locale browserLocale = getBrowserLocale(httpHeaders);
//    User loggedUser = getLoggedUser(httpHeaders);
//
//    if (!hasRole(loggedUser, UserRole.USER)) {
//      return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
//    }
//    
//    // TODO: Permission to map?
//    // TODO: Permission to vector image
//    
//    Map map = mapDAO.findById(mapId);
//    if (map == null) {
//      return Response.status(Status.NOT_FOUND).build();
//    }
//
//    VectorImage vectorImage = vectorImageDAO.findById(vectorImageId);
//    if (vectorImage == null) {
//      return Response.status(Status.NOT_FOUND).build();
//    }
//    
//    MapVectorImageLayer mapVectorImageLayer = mapVectorImageLayerDAO.create(map, vectorImage, 0);
//
//    TranquilityBuilder tranquilityBuilder = tranquilityBuilderFactory.createBuilder();
//    Tranquility tranquility = tranquilityBuilder.createTranquility()
//        .addInstruction(tranquilityBuilder.createPropetyTypeInstruction(TranquilModelType.COMPLETE));
//    
//    return Response.status(Response.Status.OK).entity(
//      new ApiResult<TranquilModelEntity>(
//          tranquility.entity(mapVectorImageLayer)    
//      )
//    ).build();
//  }
  /**
  @GET
  @Path ("/listSessionMaps")
  public Response listSessionMaps(
      @QueryParam ("illusionSessionId") Long illusionSessionId,
      @Context UriInfo uriInfo,
      @Context HttpHeaders httpHeaders) {
      
    Locale browserLocale = getBrowserLocale(httpHeaders);
    User loggedUser = getLoggedUser(httpHeaders);
  
    if (!hasRole(loggedUser, UserRole.USER)) {
      return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
    }
    
    // TODO: Is member of session?
    
    IllusionSession illusionSession = illusionSessionDAO.findById(illusionSessionId);
    if (illusionSession == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    List<Map> maps = mapDAO.listByIllusionSession(illusionSession);
    
    TranquilityBuilder tranquilityBuilder = tranquilityBuilderFactory.createBuilder();
    Tranquility tranquility = tranquilityBuilder.createTranquility()
        .addInstruction(tranquilityBuilder.createPropertyTypeInstruction(TranquilModelType.COMPLETE))
        .addInstruction("layers", tranquilityBuilder.createPropertyTypeInstruction(TranquilModelType.COMPACT));
    
    return Response.status(Response.Status.OK).entity(
      new ApiResult<Collection<TranquilModelEntity>>(
          tranquility.entities(maps)
      )
    ).build();
  }
  **/
}
