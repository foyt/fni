package fi.foyt.fni.persistence.dao.illusion;

import java.util.Currency;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent_;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;

public class IllusionEventDAO extends GenericDAO<IllusionEvent> {

	private static final long serialVersionUID = 1L;

	public IllusionEvent create(String urlName, String name, String description, String xmppRoom, IllusionGroupFolder folder, IllusionEventJoinMode joinMode, Date created, Double signUpFee, Currency signUpFeeCurrency) {
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
	
  
}
