package fi.foyt.fni.coops.model;

import java.util.Map;

public class Patch {
	
	public Patch() {
	}
	
	public Patch( Long revisionNumber, String patch, Map<String, String> properties, String checksum, String clientId) {
		this.patch = patch;
		this.properties = properties;
		this.revisionNumber = revisionNumber;
		this.checksum = checksum;
		this.clientId = clientId;
	}
	
	public String getPatch() {
		return patch;
	}
	
	public void setPatch(String patch) {
		this.patch = patch;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public Long getRevisionNumber() {
		return revisionNumber;
	}
	
	public void setRevisionNumber(Long revisionNumber) {
		this.revisionNumber = revisionNumber;
	}
	
	public String getChecksum() {
		return checksum;
	}
	
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	public String getClientId() {
    return clientId;
  }
	
	public void setClientId(String clientId) {
    this.clientId = clientId;
  }
	
	private String patch;
	private Map<String, String> properties;
	private Long revisionNumber;
	private String checksum;
	private String clientId;
}