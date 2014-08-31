package fi.foyt.fni.users;

import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;

import fi.foyt.fni.persistence.model.users.User;

public class LastNameComparator implements Comparator<User> {

	@Override
  public int compare(User user, User user2) {
    return ObjectUtils.compare(user.getLastName(), user2.getLastName());
  }

}
