package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.List;

import fi.foyt.fni.persistence.model.forum.Forum;

public class CompactForumBean {

	public CompactForumBean(Long id, String urlName, String name, String description, Long categoryId) {
		this.id = id;
		this.urlName = urlName;
		this.name = name;
		this.description = description;
		this.categoryId = categoryId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrlName() {
		return urlName;
	}

	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public static CompactForumBean fromEntity(Forum entity) {
		if (entity == null)
			return null;
		
		return new CompactForumBean(entity.getId(), entity.getUrlName(), entity.getName(), entity.getDescription(), entity.getCategory().getId());
	}

	public static List<CompactForumBean> fromEntities(List<Forum> entities) {
		List<CompactForumBean> beans = new ArrayList<CompactForumBean>(entities.size());

		for (Forum entity : entities) {
			beans.add(fromEntity(entity));
		}

		return beans;
	}

	private Long id;

	private String urlName;

	private String name;

	private String description;

	private Long categoryId;
}
