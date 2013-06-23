package fi.foyt.fni.persistence.model.common;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class LocalizedString {
	
	public Long getId() {
		return id;
	}
	
	public MultilingualString getMultilingualString() {
		return multilingualString;
	}
	
	public void setMultilingualString(MultilingualString multilingualString) {
		this.multilingualString = multilingualString;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
	
	@ManyToOne
	private MultilingualString multilingualString;
	
	@Column (nullable = false)
  @Lob
	@NotNull
  @NotEmpty
	private String value;
	
	@Column (nullable = false)
	@NotNull
	private Locale locale;
}
