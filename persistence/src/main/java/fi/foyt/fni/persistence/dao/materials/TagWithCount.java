package fi.foyt.fni.persistence.dao.materials;

import fi.foyt.fni.persistence.model.common.Tag;

public class TagWithCount {
  
  public TagWithCount(Tag tag, long count) {
    this.tag = tag;
    this.count = count;
  }
  
  public long getCount() {
    return count;
  }
  
  public void setCount(long count) {
    this.count = count;
  }
  
  public Tag getTag() {
    return tag;
  }
  
  public void setTag(Tag tag) {
    this.tag = tag;
  }
  
  private Tag tag;
  private long count;
}