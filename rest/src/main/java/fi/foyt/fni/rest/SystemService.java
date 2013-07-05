package fi.foyt.fni.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.system.SystemSettingsController;

@Path("/2/system")
@RequestScoped
@Stateful
@Produces ("application/json")
@Consumes ("application/json")
public class SystemService {
	
	@Inject
	private SystemSettingsController systemSettingsController;
	
	/**
	 * Lists languages.
	 * 
	 * Method does not require authentication.
	 * 
	 * @param localized sets whether only localized languages should be listed
	 * @return list of languages
	 */
	@GET
	@Path ("/languages")
	public Response listLanguages(@QueryParam ("localized") Boolean localized) {
		List<Language> languages = null;
		if (localized != null && localized) {
			languages = systemSettingsController.listLocalizedLanguages();
		} else {
			languages = systemSettingsController.listLanguages();
		}
		
		List<fi.foyt.fni.rest.entities.common.Language> entities = new ArrayList<>();
		
		for (Language language : languages) {
			entities.add(new fi.foyt.fni.rest.entities.common.Language(language.getId(), language.getLocalized(), language.getISO2(), language.getISO3()));
		}
		
		return Response.ok(entities).build();
	}
	
	
}
