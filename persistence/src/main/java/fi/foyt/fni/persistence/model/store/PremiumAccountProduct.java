package fi.foyt.fni.persistence.model.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

@Entity
@PrimaryKeyJoinColumn (name="id")
public class PremiumAccountProduct extends Product {

	public Integer getMonths() {
		return months;
	}
	
	public void setMonths(Integer months) {
		this.months = months;
	}
	
  @Column (nullable=false)
  @NotNull
	private Integer months;
}
