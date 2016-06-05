package fi.foyt.fni.persistence.dao.materials;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.MaterialShareGroup;
import fi.foyt.fni.persistence.model.materials.MaterialShareGroup_;
import fi.foyt.fni.persistence.model.materials.MaterialShareUser;
import fi.foyt.fni.persistence.model.materials.MaterialShareUser_;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.MaterialView;
import fi.foyt.fni.persistence.model.materials.MaterialView_;
import fi.foyt.fni.persistence.model.materials.Material_;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserGroupMember;
import fi.foyt.fni.persistence.model.users.UserGroupMember_;

public class MaterialDAO extends GenericDAO<Material> {

	private static final long serialVersionUID = 1L;


	public Material findByParentFolderAndUrlName(Folder parentFolder, String urlName) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(Material_.parentFolder), parentFolder),
          criteriaBuilder.equal(root.get(Material_.urlName), urlName)
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public Material findByRootFolderAndUrlName(User creator, String urlName) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.isNull(root.get(Material_.parentFolder)),
          criteriaBuilder.equal(root.get(Material_.urlName), urlName),
          criteriaBuilder.equal(root.get(Material_.creator), creator)
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<Material> listByParentFolder(Folder parentFolder) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(Material_.parentFolder), parentFolder)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public List<Material> listByParentFolderAndTypes(Folder parentFolder, Collection<MaterialType> types) {
	  if ((types == null) || (types.isEmpty())) {
      return Collections.emptyList();
    }
    
	  EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Material_.parentFolder), parentFolder),
        root.get(Material_.type).in(types)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
	
  public List<Material> listByRootFolder() {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.isNull(root.get(Material_.parentFolder))
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<Material> listByRootFolderAndCreator(User creator) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.isNull(root.get(Material_.parentFolder)),
        criteriaBuilder.equal(root.get(Material_.creator), creator)
       )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
	
  public List<Material> listByRootFolderAndTypesAndCreator(Collection<MaterialType> types, User creator) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.and(
    	  criteriaBuilder.isNull(root.get(Material_.parentFolder)),
        root.get(Material_.type).in(types),
        criteriaBuilder.equal(root.get(Material_.creator), creator)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<Material> listByPublicityAndCreatorAndAndTypes(MaterialPublicity publicity, User creator, List<MaterialType> types) {
    if ((types == null) || (types.isEmpty())) {
      return Collections.emptyList();
    }
    
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Material_.publicity), publicity),
        criteriaBuilder.equal(root.get(Material_.creator), creator),
        root.get(Material_.type).in(types)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<Material> listByModifierExcludingTypesSortByModified(User modifier, Collection<MaterialType> types, int firstResult, int maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(root);
    
    
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Material_.modifier), modifier),
        criteriaBuilder.not(root.get(Material_.type).in(types))
      )
    );
    
    criteria.orderBy(criteriaBuilder.desc(root.get(Material_.modified)));
    
    TypedQuery<Material> query = entityManager.createQuery(criteria);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }

  public List<Material> listByPublicityOrderByModified(MaterialPublicity publicity, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(Material_.publicity), publicity)
    );
    
    criteria.orderBy(criteriaBuilder.desc(root.get(Material_.modified)));
    
    TypedQuery<Material> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }
    
    return query.getResultList();
  }

  public List<Material> listByPublicityOrderByViews(MaterialPublicity publicity, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<MaterialView> root = criteria.from(MaterialView.class);
    Join<MaterialView, Material> materialJoin = root.join(MaterialView_.material);
    
    criteria.select(root.get(MaterialView_.material));
    criteria.where(
      criteriaBuilder.equal(materialJoin.get(Material_.publicity), publicity)
    );
    
    criteria.groupBy(root.get(MaterialView_.material), root.get(MaterialView_.count));
    criteria.orderBy(criteriaBuilder.desc(root.get(MaterialView_.count)));
    
    TypedQuery<Material> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }
    
    return query.getResultList();
  }
  
  public List<Material> listByModifiedAfter(Date modifiedAfter) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.greaterThan(root.get(Material_.modified), modifiedAfter)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<Material> listByParentFolderIsNullAndShared(User user) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    
    criteria.select(root);
    criteria.where(
      criteriaBuilder.or(
        criteriaBuilder.and(
          criteriaBuilder.isNull(root.get(Material_.parentFolder)),
          criteriaBuilder.equal(root.get(Material_.creator), user)
        ),
        root.in(subqueryGroupShares(criteriaBuilder, criteria, user)),
        root.in(subqueryUserShares(criteriaBuilder, criteria, user))
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  private Subquery<Material> subqueryGroupShares(CriteriaBuilder criteriaBuilder, CriteriaQuery<Material> criteria, User user) {
    Subquery<Material> subquery = criteria.subquery(Material.class);
    Root<MaterialShareGroup> groupRoot = subquery.from(MaterialShareGroup.class);
    Root<UserGroupMember> memberRoot = subquery.from(UserGroupMember.class);
    Join<MaterialShareGroup, Material> materialJoin = groupRoot.join(MaterialShareGroup_.material);
    subquery.select(materialJoin);
    
    subquery.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(memberRoot.get(UserGroupMember_.user), user),
        criteriaBuilder.equal(groupRoot.get(MaterialShareGroup_.userGroup), memberRoot.get(UserGroupMember_.group)),
        groupRoot.get(MaterialShareGroup_.role).in(MaterialRole.MAY_EDIT, MaterialRole.MAY_VIEW),
        criteriaBuilder.isNull(materialJoin.get(Material_.parentFolder))
      )
    );
    
    return subquery;
  }

  private Subquery<Material> subqueryUserShares(CriteriaBuilder criteriaBuilder, CriteriaQuery<Material> criteria, User user) {
    Subquery<Material> subquery = criteria.subquery(Material.class);
    Root<MaterialShareUser> userRoot = subquery.from(MaterialShareUser.class);
    Join<MaterialShareUser, Material> materialJoin = userRoot.join(MaterialShareUser_.material);
    subquery.select(materialJoin);
    
    subquery.where(
      criteriaBuilder.equal(userRoot.get(MaterialShareUser_.user), user),
      userRoot.get(MaterialShareUser_.role).in(MaterialRole.MAY_EDIT, MaterialRole.MAY_VIEW),
      criteriaBuilder.isNull(materialJoin.get(Material_.parentFolder))
    );
    
    return subquery;
  }

  public List<Material> listByFolderIsNullAndSharedAndTypes(User user, Collection<MaterialType> types) {
    if (types == null || types.isEmpty()) {
      return Collections.emptyList();
    }
    
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    
    criteria.select(root);
    criteria.where(
      criteriaBuilder.or(
        criteriaBuilder.and(
          criteriaBuilder.isNull(root.get(Material_.parentFolder)),
          criteriaBuilder.equal(root.get(Material_.creator), user),
          root.get(Material_.type).in(types)
        ),
        root.in(subqueryGroupSharesTypes(criteriaBuilder, criteria, user, types)),
        root.in(subqueryUserSharesTypes(criteriaBuilder, criteria, user, types))
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  private Subquery<Material> subqueryGroupSharesTypes(CriteriaBuilder criteriaBuilder, CriteriaQuery<Material> criteria, User user, Collection<MaterialType> types) {
    Subquery<Material> subquery = criteria.subquery(Material.class);
    Root<MaterialShareGroup> groupRoot = subquery.from(MaterialShareGroup.class);
    Root<UserGroupMember> memberRoot = subquery.from(UserGroupMember.class);
    Join<MaterialShareGroup, Material> materialJoin = groupRoot.join(MaterialShareGroup_.material);
    subquery.select(materialJoin);
    
    subquery.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(memberRoot.get(UserGroupMember_.user), user),
        criteriaBuilder.equal(groupRoot.get(MaterialShareGroup_.userGroup), memberRoot.get(UserGroupMember_.group)),
        groupRoot.get(MaterialShareGroup_.role).in(MaterialRole.MAY_EDIT, MaterialRole.MAY_VIEW),
        criteriaBuilder.isNull(materialJoin.get(Material_.parentFolder)),
        materialJoin.get(Material_.type).in(types)
      )
    );
    
    return subquery;
  }

  private Subquery<Material> subqueryUserSharesTypes(CriteriaBuilder criteriaBuilder, CriteriaQuery<Material> criteria, User user, Collection<MaterialType> types) {
    Subquery<Material> subquery = criteria.subquery(Material.class);
    Root<MaterialShareUser> userRoot = subquery.from(MaterialShareUser.class);
    Join<MaterialShareUser, Material> materialJoin = userRoot.join(MaterialShareUser_.material);
    subquery.select(materialJoin);
    
    subquery.where(
      criteriaBuilder.equal(userRoot.get(MaterialShareUser_.user), user),
      userRoot.get(MaterialShareUser_.role).in(MaterialRole.MAY_EDIT, MaterialRole.MAY_VIEW),
      criteriaBuilder.isNull(materialJoin.get(Material_.parentFolder)),
      materialJoin.get(Material_.type).in(types)
    );
    
    return subquery;
  }

  public Long countByCreator(User creator) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.equal(root.get(Material_.creator), creator)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public List<Material> listRandomMaterialsByPublicity(MaterialPublicity publicity, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();
    TypedQuery<Material> query = entityManager.createQuery("from Material where publicity = :publicity order by rand()", Material.class);
    query.setParameter("publicity", publicity);
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }
    
    return query.getResultList();
  }

  public Material updatePublicity(Material material, MaterialPublicity publicity, User modifier) {
  	material.setPublicity(publicity);
  	material.setModified(new Date());
		material.setModifier(modifier);
		
  	getEntityManager().persist(material);
  	return material;
  }

	public Material updateParentFolder(Material material, Folder parentFolder, User modifier) {
		material.setParentFolder(parentFolder);
		material.setModified(new Date());
		material.setModifier(modifier);
		
  	getEntityManager().persist(material);
  	
  	return material;
  }

  public Material updateUrlName(Material material, String urlName, User modifier) {
    EntityManager entityManager = getEntityManager();

    material.setUrlName(urlName);
    material.setModified(new Date());
    material.setModifier(modifier);
    
    entityManager.persist(material);
    return material;
  }

  
  public Material updateTitle(Material material, String title, User modifier) {
    EntityManager entityManager = getEntityManager();

    material.setTitle(title);
    material.setModified(new Date());
    material.setModifier(modifier);
    
    entityManager.persist(material);
    return material;
  }

  public Material updateDescription(Material material, String description) {
    material.setDescription(description);
    return persist(material);
  }

  public Material updateLicense(Material material, String license) {
    material.setLicense(license);
    return persist(material);
  }

  public Material updateLanguage(Material material, Language language) {
    material.setLanguage(language);
    return persist(material);
  }
  
}
