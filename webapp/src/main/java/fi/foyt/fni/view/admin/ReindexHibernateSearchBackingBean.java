package fi.foyt.fni.view.admin;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.system.SearchController;

@RequestScoped
@Named
@Stateful
@Join (path = "/admin/reindex-hibernate-search", to = "/admin/reindex-hibernate-search.jsf")
@LoggedIn
@Secure (Permission.SYSTEM_ADMINISTRATION)
public class ReindexHibernateSearchBackingBean {
  
  @Inject
  private SearchController searchController;
  
	@RequestAction
	public String load() throws InterruptedException {
	  searchController.reindexEntities();
	  return "/index.jsf";
	}

}
