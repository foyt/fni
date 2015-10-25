package fi.foyt.fni.system;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.common.TagDAO;
import fi.foyt.fni.persistence.model.common.Tag;

public class TagController {
	
	@Inject
	private TagDAO tagDAO;
	
	public Tag createTag(String text) {
		return tagDAO.create(text);
	}
	
	public Tag findTagByText(String text) {
		return tagDAO.findByText(text);
	}
	
}
