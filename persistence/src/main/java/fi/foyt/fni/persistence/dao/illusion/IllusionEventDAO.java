package fi.foyt.fni.persistence.dao.illusion;

import java.util.Currency;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventType;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent_;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.oauth.OAuthClient;

public class IllusionEventDAO extends GenericDAO<IllusionEvent> {

	private static final long serialVersionUID = 1L;

	public IllusionEvent create(String urlName, String name, String location, String description, String xmppRoom, IllusionEventFolder folder, IllusionEventJoinMode joinMode, Date created, Double signUpFee, Currency signUpFeeCurrency, Date startDate, Date startTime, Date endDate, Date endTime, OAuthClient oAuthClient, Integer ageLimit, Boolean beginnerFriendly, String imageUrl, IllusionEventType type, Date registrationStartDate, Date registrationEndDate) {
		IllusionEvent illusionEvent = new IllusionEvent();
		
		illusionEvent.setName(name);
		illusionEvent.setDescription(description);
		illusionEvent.setUrlName(urlName);
		illusionEvent.setXmppRoom(xmppRoom);
		illusionEvent.setCreated(created);
		illusionEvent.setFolder(folder);
		illusionEvent.setJoinMode(joinMode);
		illusionEvent.setSignUpFee(signUpFee);
		illusionEvent.setSignUpFeeCurrency(signUpFeeCurrency);
		illusionEvent.setLocation(location);
		illusionEvent.setStartDate(startDate);
		illusionEvent.setStartTime(startTime);
		illusionEvent.setEndDate(endDate);
		illusionEvent.setEndTime(endTime);
		illusionEvent.setOAuthClient(oAuthClient);
		illusionEvent.setAgeLimit(ageLimit);
		illusionEvent.setBeginnerFriendly(beginnerFriendly);
		illusionEvent.setImageUrl(imageUrl);
		illusionEvent.setType(type);
		illusionEvent.setRegistrationStartDate(registrationStartDate);
		illusionEvent.setRegistrationEndDate(registrationEndDate);
		
		return persist(illusionEvent);
	}

	public IllusionEvent findByUrlName(String urlName) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEvent> criteria = criteriaBuilder.createQuery(IllusionEvent.class);
    Root<IllusionEvent> root = criteria.from(IllusionEvent.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(IllusionEvent_.urlName), urlName)
    );

    return getSingleResult(entityManager.createQuery(criteria));
	}

  public IllusionEvent findByFolder(IllusionEventFolder folder) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEvent> criteria = criteriaBuilder.createQuery(IllusionEvent.class);
    Root<IllusionEvent> root = criteria.from(IllusionEvent.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionEvent_.folder), folder)
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public IllusionEvent findByDomain(String domain) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEvent> criteria = criteriaBuilder.createQuery(IllusionEvent.class);
    Root<IllusionEvent> root = criteria.from(IllusionEvent.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionEvent_.domain), domain)
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public IllusionEvent findByOAuthClient(OAuthClient oAuthClient) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEvent> criteria = criteriaBuilder.createQuery(IllusionEvent.class);
    Root<IllusionEvent> root = criteria.from(IllusionEvent.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionEvent_.oAuthClient), oAuthClient)
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<IllusionEvent> listByDomainNotNull() {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEvent> criteria = criteriaBuilder.createQuery(IllusionEvent.class);
    Root<IllusionEvent> root = criteria.from(IllusionEvent.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.isNotNull(root.get(IllusionEvent_.domain))
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<IllusionEvent> listByStartDateGEOrEndDateGESortByStartDateAndStartTime(Date startDate, Date endDate, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEvent> criteria = criteriaBuilder.createQuery(IllusionEvent.class);
    Root<IllusionEvent> root = criteria.from(IllusionEvent.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.or(
        criteriaBuilder.greaterThanOrEqualTo(root.get(IllusionEvent_.startDate), startDate),
        criteriaBuilder.greaterThanOrEqualTo(root.get(IllusionEvent_.endDate), endDate)
      )
    );
    
    criteria.orderBy(criteriaBuilder.asc(root.get(IllusionEvent_.startDate)), criteriaBuilder.asc(root.get(IllusionEvent_.startTime)));

    TypedQuery<IllusionEvent> query = entityManager.createQuery(criteria);
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

  public List<IllusionEvent> listByStartDateLTAndEndDateLTSortByEndDateEndTimeStartDateStartTime(Date startDate, Date endDate, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEvent> criteria = criteriaBuilder.createQuery(IllusionEvent.class);
    Root<IllusionEvent> root = criteria.from(IllusionEvent.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.lessThan(root.get(IllusionEvent_.startDate), startDate),
        criteriaBuilder.lessThan(root.get(IllusionEvent_.endDate), endDate)
      )
    );
    
    criteria.orderBy(
      criteriaBuilder.desc(root.get(IllusionEvent_.startDate)), 
      criteriaBuilder.desc(root.get(IllusionEvent_.startTime)), 
      criteriaBuilder.desc(root.get(IllusionEvent_.endDate)), 
      criteriaBuilder.desc(root.get(IllusionEvent_.endTime))
    );

    TypedQuery<IllusionEvent> query = entityManager.createQuery(criteria);
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }

    return query.getResultList();
  }

  public IllusionEvent updateName(IllusionEvent illusionEvent, String name) {
    illusionEvent.setName(name);
    return persist(illusionEvent);
  }

  public IllusionEvent updateJoinMode(IllusionEvent illusionEvent, IllusionEventJoinMode joinMode) {
    illusionEvent.setJoinMode(joinMode);
    return persist(illusionEvent);
  }
  
  public IllusionEvent updateSignUpFee(IllusionEvent illusionEvent, Double signUpFee) {
    illusionEvent.setSignUpFee(signUpFee);
    return persist(illusionEvent);
  }
  
  public IllusionEvent updateSignUpFeeCurrency(IllusionEvent illusionEvent, Currency signUpFeeCurrency) {
    illusionEvent.setSignUpFeeCurrency(signUpFeeCurrency);
    return persist(illusionEvent);
  }

  public IllusionEvent updateUrlName(IllusionEvent illusionEvent, String urlName) {
    illusionEvent.setUrlName(urlName);
    return persist(illusionEvent);
  }

  public IllusionEvent updateDescription(IllusionEvent illusionEvent, String description) {
    illusionEvent.setDescription(description);
    return persist(illusionEvent);
  }

  public IllusionEvent updateLocation(IllusionEvent illusionEvent, String location) {
    illusionEvent.setLocation(location);
    return persist(illusionEvent);
  }

  public IllusionEvent updateStartDate(IllusionEvent illusionEvent, Date startDate) {
    illusionEvent.setStartDate(startDate);
    return persist(illusionEvent);
  }

  public IllusionEvent updateStartTime(IllusionEvent illusionEvent, Date startTime) {
    illusionEvent.setStartTime(startTime);
    return persist(illusionEvent);
  }

  public IllusionEvent updateEndDate(IllusionEvent illusionEvent, Date endDate) {
    illusionEvent.setEndDate(endDate);
    return persist(illusionEvent);
  }

  public IllusionEvent updateEndTime(IllusionEvent illusionEvent, Date endTime) {
    illusionEvent.setEndTime(endTime);
    return persist(illusionEvent);
  }

  public IllusionEvent updateOAuthClient(IllusionEvent illusionEvent, OAuthClient oAuthClient) {
    illusionEvent.setOAuthClient(oAuthClient);
    return persist(illusionEvent);
  }

  public IllusionEvent updateDomain(IllusionEvent illusionEvent, String domain) {
    illusionEvent.setDomain(domain);
    return persist(illusionEvent);
  }

  public IllusionEvent updateBeginnerFriendly(IllusionEvent illusionEvent, Boolean beginnerFriendly) {
    illusionEvent.setBeginnerFriendly(beginnerFriendly);
    return persist(illusionEvent);
  }

  public IllusionEvent updateImageUrl(IllusionEvent illusionEvent, String imageUrl) {
    illusionEvent.setImageUrl(imageUrl);
    return persist(illusionEvent);
  }

  public IllusionEvent updateType(IllusionEvent illusionEvent, IllusionEventType type) {
    illusionEvent.setType(type);
    return persist(illusionEvent);
  }

  public IllusionEvent updateRegistrationStartDate(IllusionEvent illusionEvent, Date registrationStartDate) {
    illusionEvent.setRegistrationStartDate(registrationStartDate);
    return persist(illusionEvent);
  }

  public IllusionEvent updateRegistrationEndDate(IllusionEvent illusionEvent, Date registrationEndDate) {
    illusionEvent.setRegistrationEndDate(registrationEndDate);
    return persist(illusionEvent);
  }
  

}
