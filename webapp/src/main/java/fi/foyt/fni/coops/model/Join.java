package fi.foyt.fni.coops.model;

import java.util.Map;

public class Join {

	public Join(Map<String, Map<String, String>> extensions, Long revisionNumber, String content, String contentType, String sessionId, Map<String, String> properties) {
		this.extensions = extensions;
		this.revisionNumber = revisionNumber;
		this.content = content;
		this.contentType = contentType;
		this.sessionId = sessionId;
		this.properties = properties;
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
	
	public String getSessionId() {
    return sessionId;
  }
	
	public Map<String, String> getProperties() {
    return properties;
  }
	
	public Map<String, Map<String, String>> getExtensions() {
    return extensions;
  }
	
	private Long revisionNumber;
	private String content;
	private String contentType;
	private String sessionId;
	private Map<String, String> properties;
  private Map<String, Map<String, String>> extensions;
}
