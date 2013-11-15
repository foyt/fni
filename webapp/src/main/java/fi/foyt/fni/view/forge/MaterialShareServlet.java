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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.materials.MaterialUserController;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.UserMaterialRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.servlet.RequestUtils;
import fi.foyt.fni.view.AbstractTransactionedServlet;

@WebServlet(urlPatterns = "/forge/materialShare/")
public class MaterialShareServlet extends AbstractTransactionedServlet {

	private static final long serialVersionUID = -1L;
	
  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialUserController materialUserController;

  @Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;
  
	@Inject
	private UserController userController;

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
		
		List<MaterialUser> users = new ArrayList<>();
		List<UserMaterialRole> materialUsers = materialUserController.listMaterialUsers(material);
		for (UserMaterialRole materialUser : materialUsers) {
		  users.add(new MaterialUser(materialUser.getUser().getId(), materialUser.getUser().getFullName(), materialUser.getRole()));
		}
		
    List<MaterialUser> friends = new ArrayList<>();
		List<User> userFriends = userController.listUserFriends(loggedUser);
		for (User friend : userFriends) {
      friends.add(new MaterialUser(friend.getId(), friend.getFullName(), null));
    }
		
		String contextPath = request.getContextPath();
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("users", users);
    result.put("friends", friends);
		result.put("publicUrl", RequestUtils.getRequestHostUrl(request) + contextPath + "/materials/" + material.getPath());
    result.put("public", materialPermissionController.isPublic(loggedUser, material));
		
    response.setContentType("application/json");

    PrintWriter printWriter = response.getWriter();
    try {
      ObjectMapper objectMapper = new ObjectMapper();      
      objectMapper.writeValue(printWriter, result);
    } finally {
      printWriter.flush();
    }
	}
	
	public class MaterialUser {
	  
	  public MaterialUser(Long id, String name, MaterialRole role) {
      this.id = id;
      this.name = name;
      this.role = role;
    }
	  
	  public Long getId() {
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
	  private Long id;
	}
}
