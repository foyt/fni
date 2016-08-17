package fi.foyt.fni.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.foyt.fni.temp.SessionTempController;

@MultipartConfig
@WebServlet (urlPatterns = "/tempUpload")
public class TempUploadServlet extends HttpServlet {

  private static final long serialVersionUID = -5449901544771220795L;
  
  @Inject
  private SessionTempController sessionTempController;
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Part file = req.getPart("file");
    if (file == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    
    String fileId = sessionTempController.createTempFile(file.getInputStream());
    
    Map<String, String> result = new HashMap<>();
    result.put("fileId", fileId);

    resp.setContentType("application/json");
    ServletOutputStream servletOutputStream = resp.getOutputStream();
    try {
      (new ObjectMapper()).writeValue(servletOutputStream, result);
    } finally {
      servletOutputStream.flush();
    }
  }
  
}
