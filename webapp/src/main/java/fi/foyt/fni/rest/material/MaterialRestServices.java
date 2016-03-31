package fi.foyt.fni.rest.material;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import fi.foyt.fni.materials.MaterialUserController;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.BookTemplate;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.UserMaterialRole;
import fi.foyt.fni.persistence.model.oauth.OAuthAccessToken;
import fi.foyt.fni.persistence.model.oauth.OAuthClientType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.rest.Security;
import fi.foyt.fni.rest.illusion.OAuthScopes;
import fi.foyt.fni.rest.material.model.MaterialUser;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.system.TagController;
import fi.foyt.fni.users.UserController;

@Path("/material")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateful
@RequestScoped
public class MaterialRestServices {

  @Inject
  private SessionController sessionController;

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private UserController userController;
  
  @Inject
  private MaterialPermissionController materialPermissionController;
  
  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialUserController materialUserController;
  
  @Inject
  private TagController tagController;

  @Context
  private OAuthAccessToken accessToken;
  
  @GET
  @Path ("/materials/{ID:[0-9]*}")
  @Security (
    allowService = true,
    allowNotLogged = true,
    scopes = { OAuthScopes.MATERIAL_FIND_MATERIAL }
  )
  public Response findMaterial(@PathParam ("ID") Long id) {
    Material material = materialController.findMaterialById(id);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    User user = null;
    if (!sessionController.isLoggedIn()) {
      if ((accessToken != null) && (accessToken.getClient().getType() == OAuthClientType.SERVICE)) {
        user = accessToken.getClient().getServiceUser();
      }
    } else {
      user = sessionController.getLoggedUser();
    }
    
    if (!materialPermissionController.isPublic(user, material)) {
      if (!materialPermissionController.hasAccessPermission(user, material)) {
        if (user == null) {
          return Response.status(Status.UNAUTHORIZED).build();
        } else {
          return Response.status(Status.FORBIDDEN).build();
        }
      }
    }
    
    return Response.ok(createRestModel(material)).build();
  }

  @PUT
  @Path ("/materials/{ID:[0-9]*}")
  @Security (
    allowService = true,
    allowNotLogged = false,
    scopes = { OAuthScopes.MATERIAL_UPDATE_MATERIAL }
  )
  public Response updateMaterial(@PathParam ("ID") Long id, fi.foyt.fni.rest.material.model.Material payload) {
    Material material = materialController.findMaterialById(id);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    User loggedUser = sessionController.getLoggedUser();
    
    if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material)) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    Language language = null;
    if (payload.getLanguageId() != null) {
      language = systemSettingsController.findLanguageById(payload.getLanguageId());
      if (language == null) {
        return Response.status(Status.BAD_REQUEST).entity(String.format("Invalid language %d", payload.getLanguageId())).build();
      }
    }
    
    materialController.updateMaterialTitle(material, payload.getTitle(), loggedUser);
    materialController.updateMaterialDescription(material, payload.getDescription());
    materialController.updateMaterialLicense(material, payload.getLicense());
    materialController.setMaterialTags(material, payload.getTags());
    materialController.updateMaterialPublicity(material, payload.getPublicity(), loggedUser);
    materialController.updateMaterialLanguage(material, language);
    
    Long parentFolderId = material.getParentFolder() != null ? material.getParentFolder().getId() : null;
    
    if (!Objects.equals(parentFolderId, payload.getParentFolderId())) {
      Folder parentFolder = null;
      if (payload.getParentFolderId() != null) {
        parentFolder = materialController.findFolderById(payload.getParentFolderId());
        if (parentFolder == null) {
          return Response.status(Status.BAD_REQUEST).entity(String.format("Invalid parentFolder %d", payload.getParentFolderId())).build();
        } else {
          if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), parentFolder)) {
            return Response.status(Status.FORBIDDEN).build();
          }
        }
      }
      
      materialController.moveMaterial(material, parentFolder, loggedUser);
    }
    
    return Response.ok(createRestModel(material)).build();
  }
  
  @POST
  @Path ("/materials/{ID:[0-9]*}/users")
  @Security (
    allowService = true,
    allowNotLogged = false,
    scopes = { OAuthScopes.MATERIAL_CREATE_USER }
  )
  public Response createMaterialUser(@PathParam ("ID") Long materialId, MaterialUser payload) {
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material)) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    if (payload.getRole() == null) {
      return Response.status(Status.BAD_REQUEST).entity("Missing role").build();
    }
    
    User user = userController.findUserById(payload.getUserId());
    if (user == null) {
      return Response.status(Status.BAD_REQUEST).entity(String.format("Invalid userId %d", payload.getUserId())).build();
    }
    
    if (materialUserController.findUserMaterialRole(user, material) != null) {
      return Response.status(Status.BAD_REQUEST).entity("User already has existing role in this material").build();
    }
    
    return Response.ok(createRestModel(materialUserController.createUserMaterialRole(user, material, payload.getRole()))).build();
  }
  
  @GET
  @Path ("/materials/{ID:[0-9]*}/users")
  @Security (
    allowService = true,
    allowNotLogged = false,
    scopes = { OAuthScopes.MATERIAL_LIST_USERS }
  )
  public Response listMaterialUsers(@PathParam ("ID") Long materialId) {
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material)) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    return Response.ok(createRestModel(materialUserController.listMaterialUsers(material).toArray(new UserMaterialRole[0]))).build();
  }
  
  @GET
  @Path ("/materials/{MATERIALID:[0-9]*}/users/{ID:[0-9]*}")
  @Security (
    allowService = true,
    allowNotLogged = false,
    scopes = { OAuthScopes.MATERIAL_FIND_USER }
  )
  public Response listMaterialUsers(@PathParam ("MATERIALID") Long materialId, @PathParam ("ID") Long id) {
    UserMaterialRole userMaterialRole = materialUserController.findUserMaterialRoleById(id);
    if (userMaterialRole == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!material.getId().equals(userMaterialRole.getMaterial().getId())) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material)) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    return Response.ok(createRestModel(userMaterialRole)).build();
  }

  @PUT
  @Path ("/materials/{MATERIALID:[0-9]*}/users/{ID:[0-9]*}")
  @Security (
    allowService = true,
    allowNotLogged = false,
    scopes = { OAuthScopes.MATERIAL_UPDATE_USER }
  )
  public Response updateMaterialUser(@PathParam ("MATERIALID") Long materialId, @PathParam ("ID") Long id, MaterialUser payload) {
    UserMaterialRole userMaterialRole = materialUserController.findUserMaterialRoleById(id);
    if (userMaterialRole == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!material.getId().equals(userMaterialRole.getMaterial().getId())) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material)) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    if (payload.getRole() == null) {
      return Response.status(Status.BAD_REQUEST).entity("Missing role").build();
    }
    
    return Response.ok(createRestModel(materialUserController.updateUserMaterialRole(userMaterialRole, payload.getRole()))).build();
  }

  @DELETE
  @Path ("/materials/{MATERIALID:[0-9]*}/users/{ID:[0-9]*}")
  @Security (
    allowService = true,
    allowNotLogged = false,
    scopes = { OAuthScopes.MATERIAL_DELETE_USER }
  )
  public Response deleteMaterialUser(@PathParam ("MATERIALID") Long materialId, @PathParam ("ID") Long id) {
    UserMaterialRole userMaterialRole = materialUserController.findUserMaterialRoleById(id);
    if (userMaterialRole == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!material.getId().equals(userMaterialRole.getMaterial().getId())) {
      return Response.status(Status.NOT_FOUND).build();
    }

    if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material)) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    materialUserController.deleteUserMaterialRole(userMaterialRole);
    
    return Response.noContent().build();
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

  @GET
  @Path ("/tags/")
  @Security (
    allowService = true,
    allowNotLogged = true,
    scopes = { OAuthScopes.MATERIAL_LIST_TAGS }
  )
  public Response listTags() {
    return Response.ok(createRestModel(tagController.listAllTags().toArray(new fi.foyt.fni.persistence.model.common.Tag[0]))).build();
  }
  
  private List<fi.foyt.fni.rest.material.model.Tag> createRestModel(fi.foyt.fni.persistence.model.common.Tag... entities) {
    List<fi.foyt.fni.rest.material.model.Tag> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.common.Tag entity : entities) {
      result.add(createRestModel(entity));
    }
    
    return result;
  }

  @SuppressWarnings("unused")
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
  
  private List<fi.foyt.fni.rest.material.model.MaterialUser> createRestModel(fi.foyt.fni.persistence.model.materials.UserMaterialRole... entities) {
    List<fi.foyt.fni.rest.material.model.MaterialUser> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.materials.UserMaterialRole entity : entities) {
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

  private fi.foyt.fni.rest.material.model.Tag createRestModel(fi.foyt.fni.persistence.model.common.Tag entity) {
    return new fi.foyt.fni.rest.material.model.Tag(entity.getId(), entity.getText());
  }
  
  private fi.foyt.fni.rest.material.model.MaterialUser createRestModel(fi.foyt.fni.persistence.model.materials.UserMaterialRole entity) {
    return new fi.foyt.fni.rest.material.model.MaterialUser(entity.getId(), entity.getUser().getId(), entity.getRole());
  }
}
