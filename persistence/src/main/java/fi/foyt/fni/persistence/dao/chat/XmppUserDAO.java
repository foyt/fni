package fi.foyt.fni.persistence.dao.chat;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.chat.XmppUser;
import fi.foyt.fni.persistence.model.chat.XmppUser_;

@RequestScoped
@DAO
public class XmppUserDAO extends GenericDAO<XmppUser> {
	
	XmppUserDAO() {
  }

  public XmppUser create(String userJid) {
    XmppUser xmppUser = new XmppUser();
    xmppUser.setUserJid(userJid);
    getEntityManager().persist(xmppUser);
    
    return xmppUser;
  }

  public XmppUser findByUserJid(String userJid) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<XmppUser> criteria = criteriaBuilder.createQuery(XmppUser.class);
    Root<XmppUser> xmppUserRoot = criteria.from(XmppUser.class);
    criteria.select(xmppUserRoot);
    criteria.where(criteriaBuilder.equal(xmppUserRoot.get(XmppUser_.userJid), userJid));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public Long countByUserJid(String userJid) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<XmppUser> xmppUserRoot = criteria.from(XmppUser.class);
    criteria.select(criteriaBuilder.count(xmppUserRoot));
    criteria.where(criteriaBuilder.equal(xmppUserRoot.get(XmppUser_.userJid), userJid));
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
}