package fi.foyt.fni.utils.search;

import java.util.List;

public class SearchResult<T> {
	
	public SearchResult(T entity, String title, String link, String text) {
	  this(entity, title, link, text, null, null);
  }
	
	public SearchResult(T entity, String title, String link, String text, List<String> tags, Float score) {
	  this.entity = entity;
	  this.title = title;
	  this.link = link;
	  this.text = text;
	  this.tags = tags;
	  this.score = score;
  }

	public T getEntity() {
	  return entity;
  }
	
	public void setEntity(T entity) {
	  this.entity = entity;
  }
	
	public String getTitle() {
	  return title;
  }
	
	public void setTitle(String title) {
	  this.title = title;
  }
	
	public String getText() {
	  return text;
  }
	
	public void setText(String text) {
	  this.text = text;
  }
	
	public String getLink() {
	  return link;
  }
	
	public void setLink(String link) {
	  this.link = link;
  }
	
	public List<String> getTags() {
	  return tags;
  }
	
	public void setTags(List<String> tags) {
	  this.tags = tags;
  }
	
	public Float getScore() {
    return score;
  }
	
	private String title;
	private String text;
	private String link;
	private List<String> tags;
	private T entity;
	private Float score;
}
