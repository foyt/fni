package fi.foyt.fni.view.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.hibernate.search.MassIndexer;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;

@RequestScoped
@Named
@Stateful
@Join (path = "/admin/reindex-hibernate-search", to = "/admin/reindex-hibernate-search.jsf")
@LoggedIn
@Secure (Permission.SYSTEM_ADMINISTRATION)
public class ReindexHibernateSearchBackingBean {
  
  @javax.persistence.PersistenceContext
  private EntityManager entityManager;

	@RequestAction
	public String load() throws InterruptedException {
	  List<Class<?>> indexedEntityClasses = listIndexedEntityClasses();
	  for (Class<?> indexedEntityClass : indexedEntityClasses) {
	    reindexEntity(indexedEntityClass);
	  }
	  
	  return "/index.jsf";
	}

	private void reindexEntity(Class<?> entity) throws InterruptedException {
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        
    MassIndexer massIndexer = fullTextEntityManager.createIndexer(entity);
    
    massIndexer.batchSizeToLoadObjects(10);
    massIndexer.threadsToLoadObjects(1);

    massIndexer.startAndWait();
  }

  private List<Class<?>> listIndexedEntityClasses() {
    List<Class<?>> result = new ArrayList<Class<?>>();
    
    Metamodel metamodel = entityManager.getMetamodel();
    Set<EntityType<?>> entityTypes = metamodel.getEntities();
    for (EntityType<?> entityType : entityTypes) {
      if (isIndexed(entityType.getJavaType())) {
        result.add(entityType.getJavaType());
      }
    }
    
    return result;
  }
  
  private boolean isIndexed(Class<?> entityClass) {
    if (entityClass.isAnnotationPresent(Indexed.class)) {
      return true;
    }

    if (entityClass.equals(Object.class))
      return false;

    return isIndexed(entityClass.getSuperclass());
  }
}
