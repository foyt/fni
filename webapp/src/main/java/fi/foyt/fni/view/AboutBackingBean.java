package fi.foyt.fni.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

@RequestScoped
@Named
@Stateful
@URLMappings (
  mappings = @URLMapping (
    id = "about",
    pattern = "/about",
    viewId = "/about.jsf"
  )    
)
public class AboutBackingBean {
  
  @PostConstruct
  public void init() {
    this.pattern = Pattern.compile("\\[(.*?)\\]");
  }
	
  public String getParsedText(String text) {
    String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    
    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      String[] parts = matcher.group(1).split("\\|");
      String href = null;
      String linkText = null;
      
      if (parts.length == 1) {
        href = linkText = parts[0];
      } else {
        linkText = parts[0];
        href = parts[1];
      }
      
      if (!(StringUtils.startsWith(href, "http://") || StringUtils.startsWith(href, "https://"))) {
        href = contextPath + href;
      }
      
      String link = String.format("<a href=\"%s\">%s</a>", href, linkText);
      text = matcher.replaceFirst(link);
      matcher = pattern.matcher(text);
    }
    
    return text;
  }

  private Pattern pattern;
}
