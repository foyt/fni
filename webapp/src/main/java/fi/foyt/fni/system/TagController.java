package fi.foyt.fni.system;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<Tag> listAllTags() {
	  return tagDAO.listAll();
	}
	
	public List<String> getAllTags() {
	  List<Tag> tags = listAllTags();
	  
	  List<String> result = new ArrayList<>(tags.size());
	  for (Tag tag : tags) {
	    result.add(tag.getText());
	  }
	  
	  return result;
	}
	
}
