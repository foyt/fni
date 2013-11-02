package fi.foyt.fni.coops.model;

public class Join {

	public Join(String[] extensions, Long revisionNumber, String content, String contentType, String clientId) {
		this.extensions = extensions;
		this.revisionNumber = revisionNumber;
		this.content = content;
		this.contentType = contentType;
		this.clientId = clientId;
	}

	public String[] getExtensions() {
		return extensions;
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
	
	public String getClientId() {
    return clientId;
  }

	private String[] extensions;
	private Long revisionNumber;
	private String content;
	private String contentType;
	private String clientId;
}
