package fi.foyt.fni.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;

@RequestScoped
@Named
@Stateful
@Join(path = "/about", to="/about.jsf")
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
      String href;
      String linkText;
      
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
