package fi.foyt.fni.persistence.dao.oauth;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.oauth.OAuthAuthorizationCode;
import fi.foyt.fni.persistence.model.oauth.OAuthAuthorizationCode_;
import fi.foyt.fni.persistence.model.oauth.OAuthClient;
import fi.foyt.fni.persistence.model.users.User;

public class OAuthAuthorizationCodeDAO extends GenericDAO<OAuthAuthorizationCode> {

	private static final long serialVersionUID = 1L;

	public OAuthAuthorizationCode create(OAuthClient client, User user, String code) {
	  OAuthAuthorizationCode oAuthAuthorizationCode = new OAuthAuthorizationCode();

	  oAuthAuthorizationCode.setClient(client);
	  oAuthAuthorizationCode.setCode(code);
	  oAuthAuthorizationCode.setUser(user);
	  
    return persist(oAuthAuthorizationCode);
  }

  public OAuthAuthorizationCode findByClientAndCode(OAuthClient client, String code) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<OAuthAuthorizationCode> criteria = criteriaBuilder.createQuery(OAuthAuthorizationCode.class);
    Root<OAuthAuthorizationCode> root = criteria.from(OAuthAuthorizationCode.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(OAuthAuthorizationCode_.client), client),
        criteriaBuilder.equal(root.get(OAuthAuthorizationCode_.code), code)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

}
