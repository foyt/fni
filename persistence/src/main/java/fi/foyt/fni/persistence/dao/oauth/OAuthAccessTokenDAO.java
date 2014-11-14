package fi.foyt.fni.persistence.dao.oauth;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.oauth.OAuthAccessToken;
import fi.foyt.fni.persistence.model.oauth.OAuthAccessToken_;
import fi.foyt.fni.persistence.model.oauth.OAuthAuthorizationCode;

public class OAuthAccessTokenDAO extends GenericDAO<OAuthAccessToken> {

	private static final long serialVersionUID = 1L;

	public OAuthAccessToken create(OAuthAuthorizationCode authorizationCode, String accessToken, Long expires) {
	  OAuthAccessToken oAuthAccessToken = new OAuthAccessToken();
    
	  oAuthAccessToken.setAccessToken(accessToken);
	  oAuthAccessToken.setAuthorizationCode(authorizationCode);
	  oAuthAccessToken.setExpires(expires);

    return persist(oAuthAccessToken);
  }

	public OAuthAccessToken findByAccessToken(String accessToken) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<OAuthAccessToken> criteria = criteriaBuilder.createQuery(OAuthAccessToken.class);
    Root<OAuthAccessToken> root = criteria.from(OAuthAccessToken.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(OAuthAccessToken_.accessToken), accessToken)
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }
}
