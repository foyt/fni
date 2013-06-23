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
import javax.validation.constraints.NotNull;

@Entity
public class ChatMessage {

  public Long getId() {
    return id;
  }

  public XmppUser getFrom() {
    return from;
  }

  public void setFrom(XmppUser from) {
    this.from = from;
  }

  public XmppUser getTo() {
    return to;
  }

  public void setTo(XmppUser to) {
    this.to = to;
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
  
  public Boolean getReceived() {
    return received;
  }
  
  public void setReceived(Boolean received) {
    this.received = received;
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private XmppUser from;

  @ManyToOne
  private XmppUser to;

  @Temporal(TemporalType.TIMESTAMP)
  private Date sent;

  private String subject;

  @Column (length=1073741824)
  private String body;
  
  @Column (nullable = false, columnDefinition = "BIT")
  @NotNull
  private Boolean received;
}
