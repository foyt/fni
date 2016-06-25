package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.materials.MaterialTypeComparator;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

@WebServlet(urlPatterns = "/forge/ckbrowserconnector/", name = "forge-ckbrowser")
@Transactional
public class CKBrowserConnectorServlet extends HttpServlet {

  private static final long serialVersionUID = -1L;
  
  private static final MaterialType[] ALLOWED_MATERIAL_TYPES = { MaterialType.IMAGE, MaterialType.FOLDER, MaterialType.DROPBOX_ROOT_FOLDER, MaterialType.DROPBOX_FILE, MaterialType.DROPBOX_FOLDER };

  @Inject
  private Logger logger;

  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (!sessionController.isLoggedIn()) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    Long folderId = NumberUtils.createLong(request.getParameter("parent"));
    if (folderId != null) {
      Folder parentFolder = materialController.findFolderById(folderId);
      if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), parentFolder)) {
        sendError(response, HttpServletResponse.SC_FORBIDDEN);
        return;
      }
    }

    Action action = Action.valueOf(request.getParameter("action"));
    if (action == Action.LIST_MATERIALS) {
      try {
        handleListMaterials(request, response);
      } catch (UnsupportedEncodingException e) {
        logger.log(Level.SEVERE, "Unsupported encoding", e);
        sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void handleListMaterials(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Dialog dialog = Dialog.valueOf(request.getParameter("dialog").toUpperCase());
    Long folderId = NumberUtils.createLong(request.getParameter("parent"));
    User loggedUser = sessionController.getLoggedUser();
    boolean rootFolder = false;

    Folder parentFolder = null;
    if (folderId == null) {
      rootFolder = true;
    } else {
      parentFolder = materialController.findFolderById(folderId);
    }

    String contextPath = request.getContextPath();

    List<MaterialBean> materialBeans = new ArrayList<>();

    if (!rootFolder) {
      Long grandParentId = parentFolder.getParentFolder() == null ? null : parentFolder.getParentFolder().getId();
      String title = grandParentId == null ? ExternalLocales.getText(request.getLocale(), "forge.ckconnector.homeFolder") : parentFolder.getParentFolder().getTitle();
      materialBeans.add(createMaterialBean(
          grandParentId, 
          title, 
          String.format("%s/materials/%s", contextPath, parentFolder.getPath()), 
          "ParentFolder", 
          "up", 
          parentFolder.getModified(),
          parentFolder.getCreator()));
    }

    List<Material> materials = null;

    switch (dialog) {
      case LINK:
        materials = materialController.listMaterialsByFolder(loggedUser, parentFolder);
      break;
      case IMAGE:
        materials = materialController.listMaterialsByFolderAndTypes(loggedUser, parentFolder, Arrays.asList(ALLOWED_MATERIAL_TYPES));
      break;
      default:
        sendError(response, HttpServletResponse.SC_BAD_REQUEST, String.format("Invalid dialog parameter value %s", dialog));
        return;
    }

    if (materials != null) {
      Collections.sort(materials, ComparatorUtils.chainedComparator(Arrays.asList(new MaterialTypeComparator(MaterialType.DROPBOX_ROOT_FOLDER), new MaterialTypeComparator(MaterialType.FOLDER))));
  
      for (Material material : materials) {
        boolean folder = material instanceof Folder;
        if (dialog == Dialog.LINK) {
          if (folder) {
            materialBeans.add(createMaterialBean(material.getId(), material.getTitle(), contextPath + "/materials/" + material.getPath(), "Folder",
                getIcon(material), material.getModified(), material.getCreator()));
          } else {
            materialBeans.add(createMaterialBean(material.getId(), material.getTitle(), contextPath + "/materials/" + material.getPath(), "Normal",
                getIcon(material), material.getModified(), material.getCreator()));
          }
        } else if (dialog == Dialog.IMAGE) {
          if (folder) {
            materialBeans.add(createMaterialBean(material.getId(), material.getTitle(), contextPath + "/materials/" + material.getPath(), "Folder",
                getIcon(material), material.getModified(), material.getCreator()));
          } else {
            materialBeans.add(createMaterialBean(material.getId(), material.getTitle(), contextPath + "/materials/" + material.getPath(), "Normal",
                getIcon(material), material.getModified(), material.getCreator()));
          }
        }
      }
    }
    
    ServletOutputStream outputStream = response.getOutputStream();
    try {
      response.setContentType("application/json; charset=utf-8");
      response.setStatus(HttpServletResponse.SC_OK);
      
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.writeValue(outputStream, materialBeans);
    } finally {
      outputStream.flush();
    }
  }

  private String getIcon(Material material) {
    if (material.getType() == MaterialType.DROPBOX_ROOT_FOLDER)
      return "dropbox-folder";
    
    if (material instanceof Folder) {
      return "folder";
    }
    
    return getIcon(material.getType());
  }
  
  private String getIcon(MaterialType materialType) {
    switch (materialType) {
      case DOCUMENT:
        return "document";
      case FILE:
        return "file";
      case IMAGE:
        return "image";
      case PDF:
        return "pdf";
      case VECTOR_IMAGE:
        return "vectorimage";
      default:
        return "file";
    }
  }

  private MaterialBean createMaterialBean(Long id, String name, String path, String type, String icon, Date date, User creator) {
    return new MaterialBean(id, name, path, type, icon, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date), creator.getFullName());
  }
  
  protected void sendError(HttpServletResponse response, int status) {
    sendError(response, status, null);
  }
  
  protected void sendError(HttpServletResponse response, int status, String message) {
    response.setStatus(status);
    if (StringUtils.isNotBlank(message)) {
      PrintWriter writer;
      try {
        writer = response.getWriter();
        writer.write(message);
        writer.flush();
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to send error", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
    }
  }

  public class MaterialBean {
    
    public MaterialBean() {
    }
    
    public MaterialBean(Long id, String name, String path, String type, String icon, String date, String creator) {
      this.id = id;
      this.name = name;
      this.path = path;
      this.type = type;
      this.icon = icon;
      this.date = date;
      this.creator = creator;
    }

    public Long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getPath() {
      return path;
    }

    public String getType() {
      return type;
    }

    public String getIcon() {
      return icon;
    }

    public String getDate() {
      return date;
    }

    public String getCreator() {
      return creator;
    }
    
    private Long id;
    private String name;
    private String path;
    private String type;
    private String icon;
    private String date;
    private String creator;
  }

  private enum Action {
    LIST_MATERIALS
  }

  private enum Dialog {
    LINK, IMAGE
  }
}
