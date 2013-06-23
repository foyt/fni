package fi.foyt.fni.utils.jsp;

public class UUID {

	public String getUniqueId() {
		return java.util.UUID.randomUUID().toString();
	}
	
}
