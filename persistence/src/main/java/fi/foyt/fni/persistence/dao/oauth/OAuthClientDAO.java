package fi.foyt.fni.persistence.dao.oauth;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.oauth.OAuthClient;
import fi.foyt.fni.persistence.model.oauth.OAuthClientType;
import fi.foyt.fni.persistence.model.oauth.OAuthClient_;
import fi.foyt.fni.persistence.model.users.User;

public class OAuthClientDAO extends GenericDAO<OAuthClient> {

	private static final long serialVersionUID = 1L;

	public OAuthClient create(String name, OAuthClientType type, String clientId, String clientSecret, String redirectUrl, User serviceUser) {
	  OAuthClient oAuthClient = new OAuthClient();

	  oAuthClient.setClientId(clientId);
	  oAuthClient.setClientSecret(clientSecret);
	  oAuthClient.setName(name);
	  oAuthClient.setRedirectUrl(redirectUrl);
	  oAuthClient.setType(type);
	  oAuthClient.setServiceUser(serviceUser);
	  
	  return persist(oAuthClient);
  }

  public OAuthClient findByClientId(String clientId) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<OAuthClient> criteria = criteriaBuilder.createQuery(OAuthClient.class);
    Root<OAuthClient> root = criteria.from(OAuthClient.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(OAuthClient_.clientId), clientId)
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public OAuthClient findByClientIdAndClientSecret(String clientId, String clientSecret) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<OAuthClient> criteria = criteriaBuilder.createQuery(OAuthClient.class);
    Root<OAuthClient> root = criteria.from(OAuthClient.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(OAuthClient_.clientId), clientId),
        criteriaBuilder.equal(root.get(OAuthClient_.clientSecret), clientSecret)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

}
