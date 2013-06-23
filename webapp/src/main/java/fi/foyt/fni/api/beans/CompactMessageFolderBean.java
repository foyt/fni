package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.List;

import fi.foyt.fni.persistence.model.messages.MessageFolder;

public class CompactMessageFolderBean {

	public CompactMessageFolderBean(Long id, String name, Long ownerId) {
	  this.id = id;
	  this.name = name;
	  this.ownerId = ownerId;
  }

	public Long getId() {
	  return id;
  }
	
	public void setId(Long id) {
	  this.id = id;
  }
	
	public String getName() {
	  return name;
  }
	
	public void setName(String name) {
	  this.name = name;
  }
	
	public Long getOwnerId() {
	  return ownerId;
  }
	
	public void setOwnerId(Long ownerId) {
	  this.ownerId = ownerId;
  }
	
	public static CompactMessageFolderBean fromEntity(MessageFolder entity) {
		if (entity == null)
			return null;
		
		return new CompactMessageFolderBean(entity.getId(), entity.getName(), entity.getOwner().getId());
	}

	public static List<CompactMessageFolderBean> fromEntities(List<MessageFolder> entities) {
		List<CompactMessageFolderBean> beans = new ArrayList<CompactMessageFolderBean>(entities.size());
		
		for (MessageFolder entity : entities) {
			beans.add(fromEntity(entity));
		}
		
		return beans;
	}
	
	private Long id;
  
  private String name;
    
  private Long ownerId;
}
