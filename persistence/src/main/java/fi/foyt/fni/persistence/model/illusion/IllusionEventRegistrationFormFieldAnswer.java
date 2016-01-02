package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "field_id", "participant_id" }) })
public class IllusionEventRegistrationFormFieldAnswer {

  public Long getId() {
    return id;
  }

  public IllusionEventRegistrationFormField getField() {
    return field;
  }

  public void setField(IllusionEventRegistrationFormField field) {
    this.field = field;
  }

  public IllusionEventParticipant getParticipant() {
    return participant;
  }

  public void setParticipant(IllusionEventParticipant participant) {
    this.participant = participant;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private IllusionEventRegistrationFormField field;

  @ManyToOne
  private IllusionEventParticipant participant;

  @Lob
  private String value;
}