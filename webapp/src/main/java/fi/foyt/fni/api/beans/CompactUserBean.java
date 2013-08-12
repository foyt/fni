package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;

public class CompactUserBean {
	
	public CompactUserBean(Long id, String firstName, String lastName, String nickname, UserRole role, Date registrationDate, String locale) {
	  super();
	  this.id = id;
	  this.firstName = firstName;
	  this.lastName = lastName;
	  this.nickname = nickname;
	  this.role = role;
	  this.registrationDate = registrationDate;
	  this.locale = locale;
  }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFullName() {
		return getFirstName() + ' ' + getLastName();
	}
	
	public void setFullName(String fullName) {
		// Read only property
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public static CompactUserBean fromEntity(User entity) {
		if (entity == null)
			return null;
		
		return new CompactUserBean(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getNickname(), entity.getRole(), entity.getRegistrationDate(), entity.getLocale());
	}
	
	public static List<CompactUserBean> fromEntities(List<User> users) {
		List<CompactUserBean> beans = new ArrayList<CompactUserBean>(users.size());
		
		for (User user : users) {
			beans.add(fromEntity(user));
		}
		
		return beans;
	}

	private Long id;

	private String firstName;

	private String lastName;

	private String nickname;

	private UserRole role;

	private Date registrationDate;

	private String locale;
}