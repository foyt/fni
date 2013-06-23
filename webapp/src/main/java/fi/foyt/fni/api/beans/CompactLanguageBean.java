package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.List;

import fi.foyt.fni.persistence.model.common.Language;

public class CompactLanguageBean {

	public CompactLanguageBean(Long id, Boolean localized, String ISO2, String ISO3) {
		super();
		this.id = id;
		this.localized = localized;
		this.ISO2 = ISO2;
		this.ISO3 = ISO3;
	}

	public static CompactLanguageBean fromEntity(Language entity) {
		if (entity == null)
			return null;
		
		return new CompactLanguageBean(entity.getId(), entity.getLocalized(), entity.getISO2(), entity.getISO3());
	}

	public static List<CompactLanguageBean> fromEntities(List<Language> entities) {
		List<CompactLanguageBean> beans = new ArrayList<CompactLanguageBean>(entities.size());

		for (Language entity : entities) {
			beans.add(fromEntity(entity));
		}

		return beans;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getLocalized() {
		return localized;
	}

	public void setLocalized(Boolean localized) {
		this.localized = localized;
	}

	public String getISO2() {
		return ISO2;
	}

	public void setISO2(String iSO2) {
		ISO2 = iSO2;
	}

	public String getISO3() {
		return ISO3;
	}

	public void setISO3(String iSO3) {
		ISO3 = iSO3;
	}

	private Long id;

	private Boolean localized;

	private String ISO2;

	private String ISO3;
}
