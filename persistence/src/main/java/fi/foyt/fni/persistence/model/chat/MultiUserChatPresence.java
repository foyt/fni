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

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class MultiUserChatPresence {

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
  
  public String getRoomJid() {
    return roomJid;
  }
  
  public void setRoomJid(String roomJid) {
    this.roomJid = roomJid;
  }

  public Date getSent() {
    return sent;
  }

  public void setSent(Date sent) {
    this.sent = sent;
  }

  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public void setMode(String mode) {
    this.mode = mode;
  }
  
  public String getMode() {
    return mode;
  }
  
  public String getStatusMessage() {
    return statusMessage;
  }
  
  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private XmppUser from;

  @ManyToOne
  private XmppUser to;
  
  @Column (nullable = false)
  @NotEmpty
  @NotNull
  private String roomJid;

  @Temporal(TemporalType.TIMESTAMP)
  private Date sent;

  @Column (nullable = false)
  @NotNull
  @NotEmpty
  private String type;
  
  private String mode;
  
  private String statusMessage;
}
