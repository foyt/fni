package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class IllusionEventParticipantSetting {
	
	public Long getId() {
		return id;
	}
	
	public IllusionEventParticipant getParticipant() {
    return participant;
  }
	
	public void setParticipant(IllusionEventParticipant participant) {
    this.participant = participant;
  }
	
	public IllusionEventSettingKey getKey() {
    return key;
  }
	
	public void setKey(IllusionEventSettingKey key) {
    this.key = key;
  }
	
	public String getValue() {
    return value;
  }
	
	public void setValue(String value) {
    this.value = value;
  }
 
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
 
  @ManyToOne
  private IllusionEventParticipant participant;

  @Enumerated (EnumType.STRING)
  @NotNull
  @Column (nullable = false, name="settingKey")
  private IllusionEventSettingKey key;
  
  private String value;
}
