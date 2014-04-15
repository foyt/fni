package fi.foyt.fni.persistence.dao.common;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.common.Language_;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;

public class LanguageDAO extends GenericDAO<Language> {

	private static final long serialVersionUID = 1L;

	public Language create(String iso2, String iso3) {
    EntityManager entityManager = getEntityManager();

    Language language = new Language();
    language.setISO2(iso2);
    language.setISO3(iso3);

    entityManager.persist(language);

    return language;
  }

  public Language findByIso2(String iso2) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Language> criteria = criteriaBuilder.createQuery(Language.class);
    Root<Language> root = criteria.from(Language.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Language_.ISO2), iso2));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public Language findByIso3(String iso3) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Language> criteria = criteriaBuilder.createQuery(Language.class);
    Root<Language> root = criteria.from(Language.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Language_.ISO3), iso3));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<Language> listByLocalized(Boolean localized) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Language> criteria = criteriaBuilder.createQuery(Language.class);
    Root<Language> root = criteria.from(Language.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Language_.localized), localized));

    return entityManager.createQuery(criteria).getResultList();
  }

}
