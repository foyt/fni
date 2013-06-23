package fi.foyt.fni.api.events;

import javax.ws.rs.core.UriInfo;

import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;

public class MaterialSharedEvent extends ApiEvent {

	public MaterialSharedEvent(UriInfo uriInfo, User sharingUser, User targetUser, Material material) {
		super(uriInfo);
		this.sharingUser = sharingUser;
		this.targetUser = targetUser;
		this.material = material;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public User getSharingUser() {
		return sharingUser;
	}
	
	public User getTargetUser() {
		return targetUser;
	}
	
	private User sharingUser;
	private User targetUser;
	private Material material;

}
