package fi.foyt.fni.users;

import java.util.Comparator;

import fi.foyt.fni.persistence.model.users.User;

public class FirstNameComparator implements Comparator<User> {

	@Override
  public int compare(User user, User user2) {
		return user.getFirstName().compareTo(user2.getFirstName());
  }

}
