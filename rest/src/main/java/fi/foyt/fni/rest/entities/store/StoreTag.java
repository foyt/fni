package fi.foyt.fni.rest.entities.store;

public class StoreTag {

	public StoreTag(Long id, String text) {
		super();
		this.id = id;
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
  private Long id;

	private String text;
}
