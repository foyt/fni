package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import fi.foyt.fni.persistence.model.materials.Material;

@Entity
@Table (
  uniqueConstraints = {
    @UniqueConstraint (columnNames = { "material_id", "participant_id", "settingKey" } )  
  }
)
public class IllusionEventMaterialParticipantSetting {
	
	public Long getId() {
		return id;
	}
	
	public IllusionEventParticipant getParticipant() {
    return participant;
  }
	
	public void setParticipant(IllusionEventParticipant participant) {
    this.participant = participant;
  }
	
	public Material getMaterial() {
    return material;
  }
	
	public void setMaterial(Material material) {
    this.material = material;
  }
	
	public String getValue() {
    return value;
  }
	
	public void setValue(String value) {
    this.value = value;
  }
	
	public void setKey(IllusionEventMaterialParticipantSettingKey key) {
    this.key = key;
  }
	
	public IllusionEventMaterialParticipantSettingKey getKey() {
    return key;
  }
 
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
 
  @ManyToOne
  private Material material;

  @ManyToOne
  private IllusionEventParticipant participant;

  @Column (nullable = false, name = "settingKey")
  @Enumerated (EnumType.STRING)
  private IllusionEventMaterialParticipantSettingKey key;
  
  @Lob
  private String value;
}
