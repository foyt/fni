package fi.foyt.fni.view.forge.old;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.materials.MaterialArchetype;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.TitleComparator;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.materials.StarredMaterialDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.view.ViewUtils;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.PageViewController;
import fi.foyt.fni.view.ViewControllerContext;
import fi.foyt.fni.view.ViewControllerException;

//@RequestScoped
//@Stateful
public class ForgeIndexViewController extends PageViewController {

  @Inject
  private SessionController sessionController;
  
  @Inject
  private MaterialController materialController;

  @Inject
  private ForgeWorkspaceManager workspaceManager;
  
	@Inject
  private MaterialDAO materialDAO;
	
	@Inject
  private StarredMaterialDAO starredMaterialDAO;
	
  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return sessionController.isLoggedIn();
  }

  @Override
  public void execute(ViewControllerContext context) {
  	super.execute(context);

    User loggedUser = sessionController.getLoggedUser();
    
    List<Material> materials = materialController.listMaterialsByFolder(loggedUser, null);
    List<Material> starredMaterials = materialController.listStarredMaterialsByUser(loggedUser, 0, 5);
    List<Material> viewedMaterials = materialController.listViewedMaterialsByUser(loggedUser, 0, 5);
    List<Material> recentlyModifiedMaterials = materialDAO.listByModifierExcludingTypesSortByModified(loggedUser, Arrays.asList(new MaterialType[] {MaterialType.FOLDER}), 0, 5);
    
    Collections.sort(materials, new TitleComparator());
    
    List<WorkspaceMaterialBean> materialBeans = new ArrayList<WorkspaceMaterialBean>();
    List<WorkspaceMaterialBean> starredMaterialBeans = new ArrayList<WorkspaceMaterialBean>();
    List<WorkspaceMaterialBean> recentlyViewedMaterialBeans = new ArrayList<WorkspaceMaterialBean>();
    List<WorkspaceMaterialBean> recentlyModifiedMaterialBeans = new ArrayList<WorkspaceMaterialBean>();
    
    for (Material material : materials) {
      materialBeans.add(workspaceManager.createBeanFromMaterial(context.getRequest().getLocale(), loggedUser, material));
    }
    
    for (Material starredMaterial : starredMaterials) {
      starredMaterialBeans.add(workspaceManager.createBeanFromMaterial(context.getRequest().getLocale(), loggedUser, starredMaterial));
    }

    for (Material viewedMaterial : viewedMaterials) {
      recentlyViewedMaterialBeans.add(workspaceManager.createBeanFromMaterial(context.getRequest().getLocale(), loggedUser, viewedMaterial));
    }
    
    for (Material recentlyModifiedMaterial : recentlyModifiedMaterials) {
      recentlyModifiedMaterialBeans.add(workspaceManager.createBeanFromMaterial(context.getRequest().getLocale(), loggedUser, recentlyModifiedMaterial));
    }
    
    workspaceManager.sortMaterials(materialBeans);

    List<WorkspaceMaterialBean> parentFolders = new ArrayList<WorkspaceMaterialBean>();
    parentFolders.add(workspaceManager.createRootBean(context.getRequest().getLocale()));
    
    Long starredMaterialCount = starredMaterials.size() >= 5 ? starredMaterialDAO.countByUser(loggedUser) : starredMaterials.size(); 

  	handleActions(context);

  	context.getRequest().setAttribute("parentFolders", parentFolders);
    context.getRequest().setAttribute("materials", materialBeans);
    context.getRequest().setAttribute("recentlyModifiedMaterials", recentlyModifiedMaterialBeans);
    context.getRequest().setAttribute("recentlyViewedMaterials", recentlyViewedMaterialBeans);
    context.getRequest().setAttribute("starredMaterialCount", starredMaterialCount);
    context.getRequest().setAttribute("starredMaterials", starredMaterialBeans);
    context.getRequest().setAttribute("connectedToDropbox", workspaceManager.getConnectedToDropbox(loggedUser));
    context.getRequest().setAttribute("connectedToUbuntuOne", workspaceManager.getConnectedToUbuntuOne(loggedUser));
    
    context.setIncludeJSP("/jsp/forge/index.jsp");
  }

	private void handleActions(ViewControllerContext context) {
	  String actionParam = context.getStringParameter("a");
  	if (StringUtils.isNotBlank(actionParam)) {
  		Action action = Action.getByName(actionParam);
  		Map<String, String> parameters = ViewUtils.explodeActionParameters(context.getStringParameter("ap"));
  		
  		switch (action) {
  			case VIEW_MATERIAL:
  				handleViewMaterialAction(parameters);
  			break;
  			case EDIT_MATERIAL:
  				handleEditMaterialAction(parameters);
  			break;
  			case IMPORT_GOOGLE_DOCUMENTS:
  				handleImportGoogleDocumentsAction(parameters);
  			break;
  			default:
  				return;
  		}
  		
			try {
				context.setJsVariable("actionParameters", ViewUtils.implodeActionParameters(parameters));
      } catch (UnsupportedEncodingException e) {
      	throw new ViewControllerException(Locales.getText(context.getRequest().getLocale(), "error.generic.configurationError"));
      }
  		
  		context.setJsVariable("action", actionParam);
  	}
  }

	private void handleViewMaterialAction(Map<String, String> parameters) {
	  Long materialId = NumberUtils.createLong(parameters.get("materialId"));
	  if (materialId != null) {
	  	Material material = materialDAO.findById(materialId);
	  	MaterialType materialType = material.getType();
	  	String materialTitle = material.getTitle();
	  	MaterialArchetype archetype = materialController.getMaterialArchetype(material);
	  	String path = material.getPath();
	  	
	  	parameters.put("materialType", materialType.name());
	  	parameters.put("materialArchetype", archetype.name());
	  	parameters.put("materialTitle", materialTitle);
	  	parameters.put("materialPath", path);
	  } 
  }

	private void handleEditMaterialAction(Map<String, String> parameters) {
	  Long materialId = NumberUtils.createLong(parameters.get("materialId"));
	  if (materialId != null) {
	  	Material material = materialDAO.findById(materialId);
	  	MaterialType materialType = material.getType();
	  	String materialTitle = material.getTitle();
	  	parameters.put("materialType", materialType.name());
	  	parameters.put("materialTitle", materialTitle);
	  }
  }

	private void handleImportGoogleDocumentsAction(Map<String, String> parameters) {
	  // Everything we need is in the request
  }

	private enum Action {
		VIEW_MATERIAL 					("viewmaterial"),
		EDIT_MATERIAL 					("editmaterial"),
		IMPORT_GOOGLE_DOCUMENTS ("importgoogledocuments");
		
		private Action(String name) {
	    this.name = name;
    }
		
		public String getName() {
	    return name;
    }
		
		public static Action getByName(String name) {
			for (Action action : values()) {
				if (action.getName().equals(name))
					return action;
			}
			
			return null;
		}
	
		private String name;
	}
	
}