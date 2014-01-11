package fi.foyt.fni.coops.model;

import java.util.Map;

public class File {

  public File(Long revisionNumber, String name, String content, String contentType, Map<String, String> properties) {
    super();
    this.revisionNumber = revisionNumber;
    this.name = name;
    this.content = content;
    this.contentType = contentType;
    this.properties = properties;
  }

  public Long getRevisionNumber() {
    return revisionNumber;
  }

  public void setRevisionNumber(Long revisionNumber) {
    this.revisionNumber = revisionNumber;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  private Long revisionNumber;
  private String name;
  private String content;
  private String contentType;
  private Map<String, String> properties;
}
