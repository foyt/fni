package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table (
  uniqueConstraints = {
    @UniqueConstraint (columnNames = { "form_id", "name" })   
  }
)
public class IllusionEventRegistrationFormField {

  public Long getId() {
    return id;
  }

  public IllusionEventRegistrationForm getForm() {
    return form;
  }
  
  public void setForm(IllusionEventRegistrationForm form) {
    this.form = form;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private IllusionEventRegistrationForm form;

  @NotNull
  @Column (nullable = false)
  private String name;
}