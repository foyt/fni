package fi.foyt.fni.persistence.model.chat;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class MultiUserChatMessage {

  public Long getId() {
    return id;
  }

  public XmppUser getFrom() {
    return from;
  }

  public void setFrom(XmppUser from) {
    this.from = from;
  }

  public String getRoomJid() {
    return roomJid;
  }

  public void setRoomJid(String roomJid) {
    this.roomJid = roomJid;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Date getSent() {
    return sent;
  }

  public void setSent(Date sent) {
    this.sent = sent;
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private XmppUser from;

  @NotEmpty
  @Column(nullable = false)
  private String roomJid;

  @Temporal(TemporalType.TIMESTAMP)
  private Date sent;

  private String subject;

  @Column (length=1073741824)
  private String body;
}
