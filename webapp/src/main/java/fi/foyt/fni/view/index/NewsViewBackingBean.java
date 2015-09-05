package fi.foyt.fni.view.index;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;
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
		
		DateTime firstDate = blogController.getFirstBlogDate();
		DateTime lastDate = blogController.getLastBlogDate();
		if (firstDate != null && lastDate != null) {
		  DateTime currentMonth = new DateTime(lastDate.getYear(), lastDate.getMonthOfYear(), 1, 0, 0, 0, 0);
  		
  		while (currentMonth.isAfter(firstDate)) {
  		  int postCount = blogController.countBlogEntriesByCreatedBetween(currentMonth.toDate(), currentMonth.plusMonths(1).toDate()).intValue();
  		  if (postCount > 0) {
  	      months.add(new Month(currentMonth, postCount));
  		  }
  		  
  		  currentMonth = currentMonth.minusMonths(1);
  		}
		}
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
	  
	  public Month(DateTime dateTime, int postCount) {
      this.date = dateTime.toDate();
      this.postCount = postCount;
      this.month = dateTime.getMonthOfYear();
      this.year = dateTime.getYear();
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
