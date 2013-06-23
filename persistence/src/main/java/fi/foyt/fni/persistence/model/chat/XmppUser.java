package fi.foyt.fni.persistence.model.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class XmppUser {

  public Long getId() {
    return id;
  }

  public String getUserJid() {
    return userJid;
  }

  public void setUserJid(String userJid) {
    this.userJid = userJid;
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @NotEmpty
  @NotNull
  @Column(nullable = false)
  private String userJid;
}
