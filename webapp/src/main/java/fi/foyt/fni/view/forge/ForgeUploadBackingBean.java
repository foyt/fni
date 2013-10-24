package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.security.LoggedIn;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
  @URLMapping(
	  id = "forge-upload", 
		pattern = "/forge/upload", 
		viewId = "/forge/upload.jsf"
  )
})
public class ForgeUploadBackingBean {

	@URLAction
	@LoggedIn
	public void load() throws FileNotFoundException {

	}

}
