package fi.foyt.fni.api.events;

import javax.ws.rs.core.UriInfo;

public class ApiEvent {

	public ApiEvent(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}
	
	public UriInfo getUriInfo() {
		return uriInfo;
	}
	
	private UriInfo uriInfo;
}
