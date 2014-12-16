package fi.foyt.fni.rest.users.model;

import java.util.List;

public class User {

  public User() {
  }
  
  public User(Long id, String firstName, String lastName, String nickname, String locale, List<String> emails) {
    super();
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.nickname = nickname;
    this.locale = locale;
    this.emails = emails;
  }

  /**
   * Returns user id
   * 
   * @return user id (ignored when creating or updating user)
   * @requiredField
   */
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns user's first name
   * 
   * @return user's first name
   */
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  /**
   * Returns user's last name
   * 
   * @return user's last name
   */
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  /**
   * Returns user's nickname
   * 
   * @return user's nickname
   */
  public String getNickname() {
    return nickname;
  }
  
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
  
  /**
   * Returns user's locale in ISO 639 alpha-2 format
   * 
   * @return user's locale in ISO 639 alpha-2 format
   * @default en
   */
  public String getLocale() {
    return locale;
  }
  
  public void setLocale(String locale) {
    this.locale = locale;
  }
  
  /**
   * Returns user's emails
   * 
   * @return user's emails
   * @requiredField
   */
  public List<String> getEmails() {
    return emails;
  }
  
  public void setEmails(List<String> emails) {
    this.emails = emails;
  }

  private Long id;
  private String firstName;
  private String lastName;
  private String nickname;
  private String locale;
  private List<String> emails;
}
