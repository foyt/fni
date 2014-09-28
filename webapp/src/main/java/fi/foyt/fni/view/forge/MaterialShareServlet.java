package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.materials.MaterialUserController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.UserMaterialRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.servlet.RequestUtils;

@WebServlet(urlPatterns = "/forge/materialShare/", name = "forge-materialshare")
@Transactional
public class MaterialShareServlet extends HttpServlet {

  private static final long serialVersionUID = -1L;

  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialUserController materialUserController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private UserController userController;

  @Inject
  private SessionController sessionController;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (!sessionController.isLoggedIn()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    Long materialId = NumberUtils.createLong(request.getParameter("materialId"));
    if (materialId == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    User loggedUser = sessionController.getLoggedUser();

    if (!materialPermissionController.hasModifyPermission(loggedUser, material)) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    List<MaterialCollaborator> collaborators = new ArrayList<>();
    
    for (UserMaterialRole materialUser : materialUserController.listMaterialUsers(material)) {
      collaborators.add(new MaterialCollaborator("U" +   materialUser.getUser().getId(), materialUser.getUser().getFullName(), materialUser.getRole()));
    }
    
    String contextPath = request.getContextPath();
    
    List<Collaborator> invitables = new ArrayList<>();
    
    IllusionEventFolder illusionEventFolder = getIllusionEventFolder(material);
    if (illusionEventFolder != null) {
      IllusionEvent event = illusionEventController.findIllusionEventByFolder(illusionEventFolder);
      List<IllusionEventParticipant> participants = illusionEventController.listIllusionEventParticipantsByEvent(event);
     
      for (IllusionEventParticipant participant : participants) {
        User user = participant.getUser();
        invitables.add(new Collaborator("U" + user.getId(), userController.getUserDisplayName(user)));
      }
      
      // TODO: Add groups
      
    } else {
      for (User user : userController.listUsers()) {
        invitables.add(new Collaborator("U" + user.getId(), userController.getUserDisplayName(user)));
      }
    }

    Map<String, Object> result = new HashMap<String, Object>();
    result.put("collaborators", collaborators);
    result.put("publicUrl", RequestUtils.getRequestHostUrl(request) + contextPath + "/materials/" + material.getPath());
    result.put("public", materialPermissionController.isPublic(loggedUser, material));
    result.put("invitables", invitables);

    response.setContentType("application/json");

    PrintWriter printWriter = response.getWriter();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.writeValue(printWriter, result);
    } finally {
      printWriter.flush();
    }
  }

  private IllusionEventFolder getIllusionEventFolder(Material material) {
    Folder parent = material.getParentFolder();
    while (parent != null) {
      if (parent.getType() == MaterialType.ILLUSION_GROUP_FOLDER) {
        return (IllusionEventFolder) parent;
      }

      parent = material.getParentFolder();
    }

    return null;
  }

  public class MaterialCollaborator {

    public MaterialCollaborator(String id, String name, MaterialRole role) {
      this.id = id;
      this.name = name;
      this.role = role;
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public MaterialRole getRole() {
      return role;
    }

    private MaterialRole role;
    private String name;
    private String id;
  }

  public class Collaborator {
    
    public Collaborator(String value, String label) {
      super();
      this.value = value;
      this.label = label;
    }

    public String getLabel() {
      return label;
    }
    
    public String getValue() {
      return value;
    }

    private String value;
    private String label;
  }
}
