package fi.foyt.fni.persistence.dao.users;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Country;
import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.AddressType;
import fi.foyt.fni.persistence.model.users.Address_;
import fi.foyt.fni.persistence.model.users.User;

public class AddressDAO extends GenericDAO<Address> {

	private static final long serialVersionUID = 1L;

	public Address create(User user, AddressType addressType, String street1, String street2, String postalCode, String city, Country country) {
    Address address = new Address();
    address.setAddressType(addressType);
    address.setCity(city);
    address.setCountry(country);
    address.setPostalCode(postalCode);
    address.setStreet1(street1);
    address.setStreet2(street2);
    address.setUser(user);

    return persist(address);
  }

  public Address findByUserAndAddressType(User user, AddressType addressType) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Address> criteria = criteriaBuilder.createQuery(Address.class);
    Root<Address> root = criteria.from(Address.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.and(
  	      criteriaBuilder.equal(root.get(Address_.user), user),
  	      criteriaBuilder.equal(root.get(Address_.addressType), addressType)
  		)
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

	public Address updateCity(Address address, String city) {
		address.setCity(city);
		return persist(address);
	}

	public Address updateCountry(Address address, Country country) {
		address.setCountry(country);
		return persist(address);
	}

	public Address updatePostalCode(Address address, String postalCode) {
		address.setPostalCode(postalCode);
		return persist(address);
	}

	public Address updateStreet1(Address address, String street1) {
		address.setStreet1(street1);
		return persist(address);
	}
	
	public Address updateStreet2(Address address, String street2) {
		address.setStreet2(street2);
		return persist(address);
	}
  
}
