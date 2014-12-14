package fi.foyt.fni.rest.coops;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fi.foyt.coops.CoOpsConflictException;
import fi.foyt.coops.CoOpsForbiddenException;
import fi.foyt.coops.CoOpsInternalErrorException;
import fi.foyt.coops.CoOpsNotFoundException;
import fi.foyt.coops.CoOpsNotImplementedException;
import fi.foyt.coops.CoOpsUsageException;
import fi.foyt.coops.model.File;
import fi.foyt.coops.model.Patch;
import fi.foyt.fni.coops.CoOpsApiDocument;
import fi.foyt.fni.coops.CoOpsSessionController;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.rest.PATCH;
import fi.foyt.fni.session.SessionController;

@Path ("/coops")
@RequestScoped
@Produces (MediaType.APPLICATION_JSON)
public class CoOpsDocumentRESTService {

  @Inject
  private CoOpsApiDocument coOpsApiDocument;

  @Inject
  private CoOpsSessionController coOpsSessionController;

  @Inject
  private SessionController sessionController;
  
  /**
   * Returns a file and file meta-information
   * 
   * @see https://github.com/foyt/coops-spec/#get--load-request
   * 
   * @param fileId file id
   * @param revisionNumber specifies file revision to be returned (defaults to latest)
   * @return Response
   */
  @GET
  @Path ("/document/{FILEID}")
  public Response load(@PathParam ("FILEID") String fileId, @QueryParam ("revisionNumber") Long revisionNumber) {
    try {
      File file = coOpsApiDocument.fileGet(fileId, revisionNumber);
      return Response.ok(file).build();
    } catch (CoOpsNotImplementedException e) {
      return Response.status(Status.NOT_IMPLEMENTED).build();
    } catch (CoOpsNotFoundException e) {
      return Response.status(Status.NOT_FOUND).build();
    } catch (CoOpsUsageException e) {
      return Response.status(Status.BAD_REQUEST).build();
    } catch (CoOpsInternalErrorException e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    } catch (CoOpsForbiddenException e) {      
      return Response.status(Status.FORBIDDEN).build();
    } 
  }
  
  /**
   * Patches a file.
   * 
   * @see https://github.com/foyt/coops-spec/#patch--patch-request
   * @param fileId file id
   * @param patch patch object
   * @return Response
   */
  @PATCH
  @Path ("/document/{FILEID}")
  public Response patch(@PathParam ("FILEID") String fileId, Patch patch) {
    try {
      CoOpsSession coOpsSession = coOpsSessionController.findSessionBySessionId(patch.getSessionId());
      if (coOpsSession == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      
      if (!coOpsSession.getUser().getId().equals(sessionController.getLoggedUserId())) {
        return Response.status(Status.FORBIDDEN).build();
      }
      
      coOpsApiDocument.filePatch(fileId, patch.getSessionId(), patch.getRevisionNumber(), patch.getPatch(), patch.getProperties(), patch.getExtensions());
      return Response.noContent().build();
    } catch (CoOpsNotFoundException e) {
      return Response.status(Status.NOT_FOUND).build();
    } catch (CoOpsUsageException e) {
      return Response.status(Status.BAD_REQUEST).build();
    } catch (CoOpsInternalErrorException e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    } catch (CoOpsForbiddenException e) {      
      return Response.status(Status.FORBIDDEN).build();
    } catch (CoOpsConflictException e) {
      return Response.status(Status.CONFLICT).build();
    } 
  }
  
  /**
   * Returns updates to the file.
   * 
   * @see https://github.com/foyt/coops-spec/#get-update-update-request 
   * @param fileId file id
   * @param sessionId session id 
   * @param revisionNumber revision after the updates are returned
   * @return
   */
  @GET
  @Path ("/document/{FILEID}/update")
  public Response update(@PathParam ("FILEID") String fileId, @QueryParam ("sessionId") String sessionId, @QueryParam ("revisionNumber") Long revisionNumber) {
    CoOpsSession coOpsSession = coOpsSessionController.findSessionBySessionId(sessionId);
    if (coOpsSession == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!coOpsSession.getUser().getId().equals(sessionController.getLoggedUserId())) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    try {
      List<Patch> patches = coOpsApiDocument.fileUpdate(fileId, sessionId, revisionNumber);
      if (patches.isEmpty()) {
        return Response.noContent().build();
      } else {
        return Response.ok(patches).build();
      }
    } catch (CoOpsNotFoundException e) {
      return Response.status(Status.NOT_FOUND).build();
    } catch (CoOpsUsageException e) {
      return Response.status(Status.BAD_REQUEST).build();
    } catch (CoOpsInternalErrorException e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    } catch (CoOpsForbiddenException e) {      
      return Response.status(Status.FORBIDDEN).build();
    } 
  }
  
  /**
   * Client joins the collaboration session
   * 
   * @see https://github.com/foyt/coops-spec/#get-join-join-request
   * @param fileId file id
   * @param algorithms supported algorithms
   * @param protocolVersion version of protocol client is using
   * @return Response
   */
  @GET
  @Path ("/document/{FILEID}/join")
  public Response join(@PathParam ("FILEID") String fileId, @QueryParam("algorithm") List<String> algorithms, @QueryParam ("protocolVersion") String protocolVersion) {
    try {
      return Response.ok(coOpsApiDocument.fileJoin(fileId, algorithms, protocolVersion)).build();
    } catch (CoOpsNotFoundException e) {
      return Response.status(Status.NOT_FOUND).build();
    } catch (CoOpsUsageException e) {
      return Response.status(Status.BAD_REQUEST).build();
    } catch (CoOpsInternalErrorException e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    } catch (CoOpsForbiddenException e) {      
      return Response.status(Status.FORBIDDEN).build();
    } catch (CoOpsNotImplementedException e) {
      return Response.status(Status.NOT_IMPLEMENTED).build();
    } 
  }
  
}
