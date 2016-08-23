package fi.foyt.fni.view.index;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.blog.BlogController;
import fi.foyt.fni.persistence.model.blog.BlogEntry;
import fi.foyt.fni.persistence.model.blog.BlogTag;

@RequestScoped
@Named
@Stateful
@Join (path = "/news/archive/{year}/{month}", to = "/news-archive.jsf")
public class NewsViewBackingBean {
	
  @Parameter
  private Integer year;
  
  @Parameter
  private Integer month;
  
	@Inject
	private BlogController blogController;

	@RequestAction
	public void init() {
		blogEntries = blogController.listBlogEntriesByYearAndMonth(getYear(), getMonth() - 1);
		months = new ArrayList<>();
		
		LocalDate firstDate = blogController.getFirstBlogDate().toLocalDateTime().toLocalDate();
		LocalDate lastDate = blogController.getLastBlogDate().toLocalDateTime().toLocalDate();
		if (firstDate != null && lastDate != null) {
		  LocalDate currentMonth = LocalDate.of(lastDate.getYear(), lastDate.getMonthValue(), 1);
		  
  		while (currentMonth.isAfter(firstDate)) {
  		  int postCount = blogController.countBlogEntriesByCreatedBetween(toDate(currentMonth), toDate(currentMonth.plusMonths(1))).intValue();
  		  if (postCount > 0) {
  		    Date date = toDate(currentMonth);
  	      months.add(new Month(date, currentMonth.getYear(), currentMonth.getMonthValue(), postCount));
  		  }
  		  
  		  currentMonth = currentMonth.minusMonths(1);
  		}
		}
	}
	
	private Date toDate(LocalDate dateTime) {
	  if (dateTime == null) {
	    return null;
	  }
	  
	  return Date.from(dateTime.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  public Integer getYear() {
    return year;
  }
	
	public void setYear(Integer year) {
    this.year = year;
  }
	
	public Integer getMonth() {
    return month;
  }
	
	public void setMonth(Integer month) {
    this.month = month;
  }
	
	public List<Month> getMonths() {
    return months;
  }
	
	public List<BlogEntry> getBlogEntries() {
    return blogEntries;
  }
  
  public List<BlogTag> getBlogEntryTags(BlogEntry entry) {
    return blogController.listBlogEntryTags(entry);
  }
	
	private List<BlogEntry> blogEntries;
	private List<Month> months;
	
	public class Month {
	  
	  public Month(Date date, int year, int month, int postCount) {
      this.date = date;
      this.postCount = postCount;
      this.month = month;
      this.year = year;
    }
	  
	  public Date getDate() {
      return date;
    }
	  
    public int getPostCount() {
      return postCount;
    }
	  
    public int getMonth() {
      return month;
    }
    
    public int getYear() {
      return year;
    }
    
	  private int postCount;
	  private Date date;
	  private int month;
	  private int year;
	}
}
