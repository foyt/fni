package fi.foyt.fni.persistence.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.hibernate.jpa.criteria.compile.CriteriaQueryTypeQueryAdapter;

public class GenericDAO<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public GenericDAO() {
  }
	
  @SuppressWarnings("unchecked")
  public T findById(Long id) {
    EntityManager entityManager = getEntityManager();
    return (T) entityManager.find(getGenericTypeClass(), id);
  }

  @SuppressWarnings("unchecked")
  public List<T> listAll() {
    EntityManager entityManager = getEntityManager();
    Class<?> genericTypeClass = getGenericTypeClass();
    Query query = entityManager.createQuery("select o from " + genericTypeClass.getName() + " o");
    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<T> listAll(int firstResult, int maxResults) {
    EntityManager entityManager = getEntityManager();
    Class<?> genericTypeClass = getGenericTypeClass();
    Query query = entityManager.createQuery("select o from " + genericTypeClass.getName() + " o");
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    return query.getResultList();
  }

  public Integer count() {
    EntityManager entityManager = getEntityManager();
    Class<?> genericTypeClass = getGenericTypeClass();
    Query query = entityManager.createQuery("select count(o) from " + genericTypeClass.getName() + " o");
    return (Integer) query.getSingleResult();
  }

  public void delete(T o) {
    EntityManager entityManager = getEntityManager();
    entityManager.remove(entityManager.merge(o));
  }

  private Class<?> getGenericTypeClass() {
    ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
    return (Class<?>) parameterizedType.getActualTypeArguments()[0];
  }

  protected T getSingleResult(Query query) {
    @SuppressWarnings("unchecked")
    List<T> list = query.getResultList();

    if (list.size() == 0)
      return null;

    if (list.size() == 1)
      return list.get(0);

    throw new NonUniqueResultException("SingleResult query returned " + list.size() + " elements");
  }

  protected EntityManager getEntityManager() {
  	return entityManager;
  }
  
  protected T persist(T entity) {
  	getEntityManager().persist(entity);
  	return entity;
  }

  protected String getQueryHQL(Query query) {
    return ((CriteriaQueryTypeQueryAdapter<?>) query).getHibernateQuery().getQueryString();
  }
  
  @javax.persistence.PersistenceContext
  private EntityManager entityManager;
}
