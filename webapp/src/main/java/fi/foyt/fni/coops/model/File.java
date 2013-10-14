package fi.foyt.fni.coops.model;

import java.util.Date;

public class File {
	
	public File(String id, String name, Date modified, Long revisionNumber, String content, String contentType) {
		this.id = id;
		this.name = name;
		this.modified = modified;
		this.revisionNumber = revisionNumber;
		this.content = content;
		this.contentType = contentType;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getModified() {
		return modified;
	}

	public Long getRevisionNumber() {
		return revisionNumber;
	}

	public String getContent() {
		return content;
	}

	public String getContentType() {
		return contentType;
	}

	private String id;
	private String name;
	private Date modified;
	private Long revisionNumber;
	private String content;
	private String contentType;
}
