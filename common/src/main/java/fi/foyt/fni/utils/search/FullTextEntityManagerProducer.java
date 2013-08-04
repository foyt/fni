package fi.foyt.fni.utils.search;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

public class FullTextEntityManagerProducer {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Produces
	public FullTextEntityManager produceFullTextEntityManager() {
		return Search.getFullTextEntityManager(entityManager);
	}

}
