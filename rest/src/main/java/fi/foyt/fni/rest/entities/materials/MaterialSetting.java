package fi.foyt.fni.rest.entities.materials;

public class MaterialSetting {

  public Long getId() {
    return id;
  }

  public Material getMaterial() {
    return material;
  }
  
  public void setMaterial(Material material) {
    this.material = material;
  }
  
  public String getKey() {
		return key;
	}
  
  public void setKey(String key) {
		this.key = key;
	}
  
  public String getValue() {
	  return value;
  }
  
  public void setValue(String value) {
	  this.value = value;
  }
  
  private Long id;
  
  private Material material;
  
  private String key;

  private String value;
}