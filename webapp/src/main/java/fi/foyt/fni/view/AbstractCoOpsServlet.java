package fi.foyt.fni.view;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.coops.CoOpsConflictException;
import fi.foyt.fni.coops.CoOpsForbiddenException;
import fi.foyt.fni.coops.CoOpsInternalErrorException;
import fi.foyt.fni.coops.CoOpsNotFoundException;
import fi.foyt.fni.coops.CoOpsNotImplementedException;
import fi.foyt.fni.coops.CoOpsUsageException;
import fi.foyt.fni.coops.model.File;
import fi.foyt.fni.coops.model.Join;
import fi.foyt.fni.coops.model.Patch;
import fi.foyt.fni.session.SessionController;

public abstract class AbstractCoOpsServlet extends AbstractTransactionedServlet {

  private static final long serialVersionUID = 8935720662817093544L;
  
  protected final static String COOPS_PROTOCOL_VERSION = "1.0.0draft2";
  protected final static String[] COOPS_SUPPORTED_EXTENSIONS = { "x-http-method-override" };

  @Inject
  private SessionController sessionController;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    synchronized (syncObject) {
      String pathInfo = StringUtils.removeStart(request.getPathInfo(), "/");
      String[] path = pathInfo.split("/");
      if (path.length < 1) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
        return;
      }
      
      if (!StringUtils.isNumeric(path[0])) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
        return;
      }
      
      if (!sessionController.isLoggedIn()) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
      
      String fileId = path[0];
      if (StringUtils.isBlank(fileId)) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
        return;
      }
      
      if (path.length == 2 && "join".equals(path[1])) {
        handleJoinRequest(request, response, fileId);
      } else if (path.length == 2 && "update".equals(path[1])) {
        handleUpdateRequest(request, response, fileId);
      } else if (path.length == 1) {
        handleFileRequest(request, response, fileId);
      }
    }
  }

  @Override
  protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String pathInfo = StringUtils.removeStart(request.getPathInfo(), "/");
    String[] path = pathInfo.split("/");
    if (path.length < 1) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return;
    }
    
    if (!StringUtils.isNumeric(path[0])) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return;
    }
    
    String fileId = path[0];
    if (StringUtils.isBlank(fileId)) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
      return;
    }

    if (!sessionController.isLoggedIn()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    
    ServletInputStream inputStream = request.getInputStream();
    
    ObjectMapper objectMapper = new ObjectMapper();
    Patch patch = objectMapper.readValue(inputStream, Patch.class);

    if (patch == null) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return;
    }
    
    if (patch.getRevisionNumber() == null) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return;
    }

    handlePatchRequest(request, response, fileId, patch);
  }
  
  protected abstract Join handleJoin(HttpServletRequest request, HttpServletResponse response, String protocolVersion, String[] algorithms, String fileId) throws CoOpsNotFoundException, CoOpsForbiddenException, CoOpsNotImplementedException, CoOpsUsageException, CoOpsInternalErrorException;
  protected abstract List<Patch> handleUpdate(HttpServletRequest request, HttpServletResponse response, Long revisionNumber, String fileId) throws CoOpsNotFoundException, CoOpsForbiddenException, CoOpsNotImplementedException, CoOpsUsageException, CoOpsInternalErrorException;
  protected abstract File handleFile(HttpServletRequest request, HttpServletResponse response, Long revisionNumber, String fileId) throws CoOpsNotFoundException, CoOpsForbiddenException, CoOpsNotImplementedException, CoOpsUsageException, CoOpsInternalErrorException;
  protected abstract void handlePatch(HttpServletRequest request, HttpServletResponse response, Patch patch, String fileId) throws CoOpsNotFoundException, CoOpsForbiddenException, CoOpsConflictException, CoOpsNotImplementedException, CoOpsUsageException, CoOpsInternalErrorException;

  private void handleJoinRequest(HttpServletRequest request, HttpServletResponse response, String fileId) throws IOException {
    String[] algorithms = request.getParameterValues("algorithm");
    String protocolVersion = request.getParameter("protocolVersion");
    
    if ((algorithms == null)||(algorithms.length == 0)) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return;
    }
    
    if (StringUtils.isBlank(protocolVersion)) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return;
    }
    
    try {
      Join join = handleJoin(request, response, protocolVersion, algorithms, fileId);
      writeJsonResponse(response, join);          
    } catch (CoOpsNotFoundException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
      return;
    } catch (CoOpsForbiddenException e) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
      return;
    } catch (CoOpsNotImplementedException e) {
      response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
      return;
    } catch (CoOpsUsageException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return;
    } catch (CoOpsInternalErrorException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
      return;
    }
  }

  private void handleUpdateRequest(HttpServletRequest request, HttpServletResponse response, String fileId) throws IOException {
    Long revisionNumber = NumberUtils.createLong(request.getParameter("revisionNumber"));
    if (revisionNumber == null) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return;
    }
    
    try {
      List<Patch> patches = handleUpdate(request, response, revisionNumber, fileId);
      if (patches == null) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        return;
      }
      
      writeJsonResponse(response, patches);
    } catch (CoOpsNotFoundException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
      return;
    } catch (CoOpsForbiddenException e) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
      return;
    } catch (CoOpsNotImplementedException e) {
      response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
      return;
    } catch (CoOpsUsageException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return;
    } catch (CoOpsInternalErrorException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
      return;
    }
  }

  private void handleFileRequest(HttpServletRequest request, HttpServletResponse response, String fileId) throws IOException {
    Long revisionNumber = NumberUtils.createLong(request.getParameter("revisionNumber"));
    try {
      File file = handleFile(request, response, revisionNumber, fileId);
      if (file == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
        return;
      } else {
        writeJsonResponse(response, file);
      }
      
    } catch (CoOpsNotFoundException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
      return;
    } catch (CoOpsForbiddenException e) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
      return;
    } catch (CoOpsNotImplementedException e) {
      response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
      return;
    } catch (CoOpsUsageException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return;
    } catch (CoOpsInternalErrorException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
      return;
    }
  }
  
  private void handlePatchRequest(HttpServletRequest request, HttpServletResponse response, String fileId, Patch patch) throws IOException {
    try {
      handlePatch(request, response, patch, fileId);
    } catch (CoOpsNotFoundException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
      return;
    } catch (CoOpsForbiddenException e) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
      return;
    } catch (CoOpsConflictException e) {
      response.sendError(HttpServletResponse.SC_CONFLICT, "Patching failed");
      return;
    } catch (CoOpsNotImplementedException e) {
      response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
      return;
    } catch (CoOpsUsageException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
      return;
    } catch (CoOpsInternalErrorException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
      return;
    }
    
    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
  }
  
  private void writeJsonResponse(HttpServletResponse response, Object object) throws IOException {
    ServletOutputStream outputStream = response.getOutputStream();
    try {
      response.setContentType("application/json; charset=utf-8");
      response.setStatus(HttpServletResponse.SC_OK);
      
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.writeValue(outputStream, object);
    } finally {
      outputStream.flush();
    }
  }
  
  private Object syncObject = new Object();
}
