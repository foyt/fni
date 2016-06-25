package fi.foyt.fni.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.hibernate.search.MassIndexer;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

@Dependent
public class SearchController {

  @Inject
  private Logger logger;
  
  @javax.persistence.PersistenceContext
  private EntityManager entityManager;
  
  public void reindexEntities() {
    List<Class<?>> indexedEntityClasses = listIndexedEntityClasses();
    for (Class<?> indexedEntityClass : indexedEntityClasses) {
      try {
        reindexEntity(indexedEntityClass);
      } catch (InterruptedException e) {
        logger.log(Level.SEVERE, String.format("Reindex entity %s was interrupted", indexedEntityClass.getName()), e);
      }
    }
  }

  private void reindexEntity(Class<?> entity) throws InterruptedException {
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        
    MassIndexer massIndexer = fullTextEntityManager.createIndexer(entity);
    
    massIndexer.batchSizeToLoadObjects(10);
    massIndexer.threadsToLoadObjects(1);

    massIndexer.startAndWait();
  }

  private List<Class<?>> listIndexedEntityClasses() {
    List<Class<?>> result = new ArrayList<>();
    
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
