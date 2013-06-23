package fi.foyt.fni.cdi;

import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

public class Resources {

  @Produces
	@PersistenceContext
	private EntityManager entityManager;

	@Produces
	public Logger produceLog(InjectionPoint injectionPoint) {
		return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
	}
	
	@Produces
	public FullTextEntityManager produceFullTextEntityManager() {
		return Search.getFullTextEntityManager(entityManager);
	}

}