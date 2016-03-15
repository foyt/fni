package fi.foyt.fni.rest.material;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.BookTemplate;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.oauth.OAuthAccessToken;
import fi.foyt.fni.persistence.model.oauth.OAuthClientType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.rest.Security;
import fi.foyt.fni.rest.illusion.OAuthScopes;
import fi.foyt.fni.session.SessionController;

@Path("/material")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateful
@RequestScoped
public class MaterialRestServices {

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialPermissionController materialPermissionController;
  
  @Inject
  private MaterialController materialController;

  @Context
  private OAuthAccessToken accessToken;
  
  @GET
  @Path ("/materials/{ID:[0-9]*}")
  @Security (
    allowService = true,
    allowNotLogged = true,
    scopes = { OAuthScopes.MATERIAL_FIND }
  )
  public Response findMaterial(@PathParam ("ID") Long id) {
    Material material = materialController.findMaterialById(id);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!materialPermissionController.isPublic(sessionController.getLoggedUser(), material)) {
      if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material)) {
        return Response.status(Status.FORBIDDEN).build();
      }
    }
    
    return Response.ok(createRestModel(material)).build();
  }
  
  @GET
  @Path ("/bookTemplates/")
  @Security (
    allowService = true,
    allowNotLogged = false,
    scopes = { OAuthScopes.MATERIAL_LIST_BOOK_TEMPLATES }
  )
  public Response listBookTemplates(@QueryParam ("publicity") String publicity) {
    switch (publicity) {
      case "PUBLIC":
        List<BookTemplate> bookTemplates = materialController.listPublicBookTemplates();
        return Response.ok(createRestModel(bookTemplates.toArray(new BookTemplate[0]))).build();
      default:
        return Response.status(Status.BAD_REQUEST).entity(String.format("Publicity %s is not supported", publicity)).build();
    }
  }

  @GET
  @Path ("/documents/{ID:[0-9]*}")
  @Security (
    allowService = true,
    allowNotLogged = false,
    scopes = { OAuthScopes.MATERIAL_FIND_DOCUMENT }
  )
  public Response findDocument(@PathParam ("ID") Long documentId) {
    Document document = materialController.findDocumentById(documentId);
    if (document == null) {
      return Response.status(Status.NOT_FOUND).entity("Not Found").build();
    }
    
    User user = null;
    if (!sessionController.isLoggedIn()) {
      if (accessToken.getClient().getType() != OAuthClientType.SERVICE) {
        return Response.status(Status.FORBIDDEN).entity(String.format("Invalid client type %s", accessToken.getClient().getType().toString())).build();
      }
      
      user = accessToken.getClient().getServiceUser();
      if (user == null) {
        return Response.status(Status.FORBIDDEN).entity("Client does not have an service user").build();
      }
    } else {
      user = sessionController.getLoggedUser();
    }
    
    if (!materialPermissionController.hasAccessPermission(user, document)) {
      if (user == null) {
        return Response.status(Status.UNAUTHORIZED).build();
      } else {
        return Response.status(Status.FORBIDDEN).build();
      }
    }
    
    return Response.ok(createRestModel(document)).build();
  }
  
  @GET
  @Path ("/images/{ID:[0-9]*}")
  @Security (
    allowService = true,
    allowNotLogged = false,
    scopes = { OAuthScopes.MATERIAL_FIND_IMAGE }
  )
  public Response findImage(@PathParam ("ID") Long imageId) {
    Image image = materialController.findImageById(imageId);
    if (image == null) {
      return Response.status(Status.NOT_FOUND).entity("Not Found").build();
    }
    
    User user = null;
    if (!sessionController.isLoggedIn()) {
      if (accessToken.getClient().getType() != OAuthClientType.SERVICE) {
        return Response.status(Status.FORBIDDEN).entity(String.format("Invalid client type %s", accessToken.getClient().getType().toString())).build();
      }
      
      user = accessToken.getClient().getServiceUser();
      if (user == null) {
        return Response.status(Status.FORBIDDEN).entity("Client does not have an service user").build();
      }
    } else {
      user = sessionController.getLoggedUser();
    }
    
    if (!materialPermissionController.hasAccessPermission(user, image)) {
      if (user == null) {
        return Response.status(Status.UNAUTHORIZED).build();
      } else {
        return Response.status(Status.FORBIDDEN).build();
      }
    }
    
    return Response.ok(createRestModel(image)).build();
  }
  
  private List<fi.foyt.fni.rest.material.model.Material> createRestModel(fi.foyt.fni.persistence.model.materials.Material... entities) {
    List<fi.foyt.fni.rest.material.model.Material> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.materials.Material entity : entities) {
      result.add(createRestModel(entity));
    }
    
    return result;
  }
  
  private List<fi.foyt.fni.rest.material.model.BookTemplate> createRestModel(fi.foyt.fni.persistence.model.materials.BookTemplate... entities) {
    List<fi.foyt.fni.rest.material.model.BookTemplate> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.materials.BookTemplate entity : entities) {
      result.add(createRestModel(entity));
    }
    
    return result;
  }

  @SuppressWarnings("unused")
  private List<fi.foyt.fni.rest.material.model.Document> createRestModel(fi.foyt.fni.persistence.model.materials.Document... entities) {
    List<fi.foyt.fni.rest.material.model.Document> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.materials.Document entity : entities) {
      result.add(createRestModel(entity));
    }
    
    return result;
  }
  
  @SuppressWarnings("unused")
  private List<fi.foyt.fni.rest.material.model.Image> createRestModel(fi.foyt.fni.persistence.model.materials.Image... entities) {
    List<fi.foyt.fni.rest.material.model.Image> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.materials.Image entity : entities) {
      result.add(createRestModel(entity));
    }
    
    return result;
  }
  
  private fi.foyt.fni.rest.material.model.Material createRestModel(fi.foyt.fni.persistence.model.materials.Material entity) {
    Long languageId = entity.getLanguage() != null ? entity.getLanguage().getId() : null;
    Long creatorId = entity.getCreator() != null ? entity.getCreator().getId() : null;
    Long modifierId = entity.getModifier() != null ? entity.getModifier().getId() : null;
    Long parentFolderId = entity.getParentFolder() != null ? entity.getParentFolder().getId() : null;
    List<String> tags = materialController.getMaterialTags(entity);
    
    return new fi.foyt.fni.rest.material.model.Material(entity.getId(), entity.getType(), entity.getUrlName(), 
        entity.getPath(), entity.getTitle(), entity.getDescription(), entity.getPublicity(), languageId, entity.getModified(), 
        entity.getCreated(), creatorId, modifierId, parentFolderId, entity.getLicense(), tags);
  }
  
  private fi.foyt.fni.rest.material.model.BookTemplate createRestModel(fi.foyt.fni.persistence.model.materials.BookTemplate entity) {
    Long languageId = entity.getLanguage() != null ? entity.getLanguage().getId() : null;
    Long creatorId = entity.getCreator() != null ? entity.getCreator().getId() : null;
    Long modifierId = entity.getModifier() != null ? entity.getModifier().getId() : null;
    Long parentFolderId = entity.getParentFolder() != null ? entity.getParentFolder().getId() : null;
    List<String> tags = materialController.getMaterialTags(entity);
    
    return new fi.foyt.fni.rest.material.model.BookTemplate(entity.getId(), entity.getType(), entity.getUrlName(), 
        entity.getPath(), entity.getTitle(), entity.getPublicity(), languageId, entity.getModified(), 
        entity.getCreated(), creatorId, modifierId, parentFolderId, entity.getData(), entity.getStyles(), 
        entity.getFonts(), entity.getIconUrl(), entity.getDescription(), entity.getLicense(), tags);
  }
  
  private fi.foyt.fni.rest.material.model.Document createRestModel(fi.foyt.fni.persistence.model.materials.Document entity) {
    Long languageId = entity.getLanguage() != null ? entity.getLanguage().getId() : null;
    Long creatorId = entity.getCreator() != null ? entity.getCreator().getId() : null;
    Long modifierId = entity.getModifier() != null ? entity.getModifier().getId() : null;
    Long parentFolderId = entity.getParentFolder() != null ? entity.getParentFolder().getId() : null;
    List<String> tags = materialController.getMaterialTags(entity);

    return new fi.foyt.fni.rest.material.model.Document(entity.getId(), entity.getType(), entity.getUrlName(), 
        entity.getPath(), entity.getTitle(), entity.getDescription(), entity.getPublicity(), languageId, entity.getModified(), 
        entity.getCreated(), creatorId, modifierId, parentFolderId, entity.getData(), entity.getLicense(), tags);
  }
  
  private fi.foyt.fni.rest.material.model.Image createRestModel(fi.foyt.fni.persistence.model.materials.Image entity) {
    Long languageId = entity.getLanguage() != null ? entity.getLanguage().getId() : null;
    Long creatorId = entity.getCreator() != null ? entity.getCreator().getId() : null;
    Long modifierId = entity.getModifier() != null ? entity.getModifier().getId() : null;
    Long parentFolderId = entity.getParentFolder() != null ? entity.getParentFolder().getId() : null;
    List<String> tags = materialController.getMaterialTags(entity);
    
    return new fi.foyt.fni.rest.material.model.Image(entity.getId(), entity.getType(), entity.getUrlName(), entity.getPath(), 
        entity.getTitle(), entity.getDescription(), entity.getPublicity(), languageId, entity.getModified(), entity.getCreated(), 
        creatorId, modifierId, parentFolderId, entity.getLicense(), tags);
  }
  
}
