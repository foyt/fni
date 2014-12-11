package fi.foyt.fni.rest.users.model;

import java.util.List;

public class UserInfo {

  public UserInfo() {
  }

  public UserInfo(Long id, String firstName, String lastName, List<String> emails) {
    super();
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.emails = emails;
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

  public List<String> getEmails() {
    return emails;
  }

  public void setEmails(List<String> emails) {
    this.emails = emails;
  }

  private Long id;
  private String firstName;
  private String lastName;
  private List<String> emails;
}
