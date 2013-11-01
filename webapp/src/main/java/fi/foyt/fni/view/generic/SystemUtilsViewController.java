package fi.foyt.fni.view.generic;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;

@RequestScoped
@Stateful
public class SystemUtilsViewController {
//	
//	private static final String REINDEX_ENTITY_CLASSES = "__reindexEntityClasses__";
//
//	@Inject
//  private SessionController sessionController;
//
//	@PersistenceContext
//  private EntityManager entityManager;
//	
//	@Override
//	public boolean checkPermissions(ViewControllerContext context) {
//		User loggedUser = sessionController.getLoggedUser();
//    if (loggedUser == null) {
//    	return false;
//    }
//    
//  	return loggedUser.getRole() == UserRole.ADMINISTRATOR;
//	}
//
//	@Override
//	public void execute(ViewControllerContext context) {
//		Action action = Action.valueOf(context.getStringParameter("action"));
//		switch (action) {
//		  case ENTITY_REINDEX:
//		  	entityReindexAction(context);
//		  break;
//		}
//	}
//
//	private void entityReindexAction(ViewControllerContext context) {
//		HttpSession session = context.getRequest().getSession();
//		@SuppressWarnings("unchecked") List<Class<?>> entityClasses = (List<Class<?>>) session.getAttribute("__reindexEntityClasses__");
//		if (entityClasses == null) {
//			entityClasses = listIndexedEntityClasses();
//			session.setAttribute(REINDEX_ENTITY_CLASSES, entityClasses);
//		} 
//		
//		if (entityClasses.size() > 0) {
//		  Class<?> entityClass = entityClasses.get(0);
//		  entityClasses.remove(0);
//		
//		  try {
//        reindexEntity(entityClass);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//		
//		  session.setAttribute(REINDEX_ENTITY_CLASSES, entityClasses);
//		  context.getRequest().setAttribute("remainingClasses", entityClasses);
//		  context.setIncludeJSP("/jsp/generic/systemutils/entityreindex.jsp");
//		} else {
//			session.removeAttribute(REINDEX_ENTITY_CLASSES);
//			context.setRedirect(context.getBasePath() + "/", false);
//		}
//	}
//	
//	private void reindexEntity(Class<?> entity) throws InterruptedException {
//		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
//				
//    MassIndexer massIndexer = fullTextEntityManager.createIndexer(entity);
//    
//    massIndexer.batchSizeToLoadObjects(10);
//    massIndexer.threadsForSubsequentFetching(1);
//    massIndexer.threadsToLoadObjects(1);
//
//    massIndexer.startAndWait();
//	}
//
//	private List<Class<?>> listIndexedEntityClasses() {
//		List<Class<?>> result = new ArrayList<Class<?>>();
//		
//		Metamodel metamodel = entityManager.getMetamodel();
//		Set<EntityType<?>> entityTypes = metamodel.getEntities();
//		for (EntityType<?> entityType : entityTypes) {
//			if (isIndexed(entityType.getJavaType())) {
//				result.add(entityType.getJavaType());
//			}
//		}
//		
//		return result;
//	}
//	
//	private boolean isIndexed(Class<?> entityClass) {
//    if (entityClass.isAnnotationPresent(Indexed.class)) {
//      return true;
//    }
//
//    if (entityClass.equals(Object.class))
//      return false;
//
//    return isIndexed(entityClass.getSuperclass());
//  }
//	
//	private enum Action {
//		ENTITY_REINDEX
//	}
}