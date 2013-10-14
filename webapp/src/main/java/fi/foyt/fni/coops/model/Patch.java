package fi.foyt.fni.coops.model;

public class Patch {
	
	public Patch() {
	}
	
	public Patch( Long revisionNumber, String patch, String checksum) {
		this.patch = patch;
		this.revisionNumber = revisionNumber;
		this.checksum = checksum;
	}
	
	public String getPatch() {
		return patch;
	}
	
	public void setPatch(String patch) {
		this.patch = patch;
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
	
	private String patch;
	private Long revisionNumber;
	private String checksum;
}