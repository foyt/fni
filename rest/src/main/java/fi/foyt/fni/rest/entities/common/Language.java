package fi.foyt.fni.rest.entities.common;

public class Language {

	public Language() {
	}
	
	public Language(Long id, Boolean localized, String iSO2, String iSO3) {
		super();
		this.id = id;
		this.localized = localized;
		ISO2 = iSO2;
		ISO3 = iSO3;
	}

	public Long getId() {
		return id;
	}

	public String getISO2() {
		return ISO2;
	}

	public void setISO2(String ISO2) {
		this.ISO2 = ISO2;
	}

	public String getISO3() {
		return ISO3;
	}

	public void setISO3(String ISO3) {
		this.ISO3 = ISO3;
	}

	public Boolean getLocalized() {
		return localized;
	}

	public void setLocalized(Boolean localized) {
		this.localized = localized;
	}

	private Long id;

	private Boolean localized;

	private String ISO2;

	private String ISO3;
}
