package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.materials.FolderController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.materials.TitleComparator;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.AbstractTransactionedServlet;

@WebServlet(urlPatterns = "/forge/folderbrowser/")
public class FolderBrowserServlet extends AbstractTransactionedServlet {

	private static final long serialVersionUID = -1L;
	
	@Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;

  @Inject
  private FolderController folderController;

  @Inject
  private MaterialController materialController;

  @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!sessionController.isLoggedIn()) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		User loggedUser = sessionController.getLoggedUser();
		Folder parentFolder = null;
		
		Long folderId = NumberUtils.createLong(request.getParameter("parent"));
		if (folderId != null) {
			parentFolder = folderController.findFolderById(folderId);
			if (!materialPermissionController.hasAccessPermission(loggedUser, parentFolder)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
		}
		
		List<Material> materials = materialController.listMaterialsByFolderAndTypes(loggedUser, parentFolder, Arrays.asList(new MaterialType[]{
      MaterialType.FOLDER
		}));
		
		Collections.sort(materials, new TitleComparator());
		
		List<FolderBean> folders = new ArrayList<>();
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, sessionController.getLocale());
    String rootTitle = ExternalLocales.getText(request.getLocale(), "forge.folderBrowser.homeFolder");

    if (parentFolder != null) {
      Folder grandParent = parentFolder.getParentFolder();
      
      if (parentFolder.getParentFolder() == null) {
        folders.add(new FolderBean(null, rootTitle, "parent", null, null));
      } else {
        folders.add(new FolderBean(grandParent.getId(), grandParent.getTitle(), "parent", dateFormat.format(grandParent.getModified()), grandParent.getCreator().getFullName()));
      }
    }
    
		for (Material material : materials) {
      folders.add(new FolderBean(material.getId(), material.getTitle(), "folder", dateFormat.format(material.getModified()), material.getCreator().getFullName()));
		}
		
		List<FolderBean> parents = new ArrayList<>();
		Folder folder = parentFolder;
		while (folder != null) {
		  parents.add(0, new FolderBean(folder.getId(), folder.getTitle(), null, null, null));
		  folder = folder.getParentFolder();
		}
		
    parents.add(0, new FolderBean(null, rootTitle, null, null, null));
    
    Map<String, Object> result = new HashMap<>();
    result.put("folders", folders);
    result.put("parents", parents);
		
		response.setContentType("application/json");

    PrintWriter printWriter = response.getWriter();
    try {
      ObjectMapper objectMapper = new ObjectMapper();      
      objectMapper.writeValue(printWriter, result);
    } finally {
      printWriter.flush();
    }
	}
  
  public class FolderBean {
    
    public FolderBean(Long id, String title, String type, String modified, String creator) {
      this.id = id;
      this.type = type;
      this.title = title;
      this.modified = modified;
      this.creator = creator;
    }

    public Long getId() {
      return id;
    }
    
    public String getType() {
      return type;
    }
    
    public String getCreator() {
      return creator;
    }
    
    public String getModified() {
      return modified;
    }
    
    public String getTitle() {
      return title;
    }
    
    private Long id;
    private String type;
    private String title;
    private String modified;
    private String creator;
  }
}
