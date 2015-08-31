package fi.foyt.fni.materials;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.foyt.fni.persistence.model.materials.BookDesign;

public class BookDesignRenderer {

  private static final String HTML_TEMPLATE = "<!DOCTYPE HTML><html><head><meta charset=\"UTF-8\"><title>{0}</title><style type=\"text/css\">{1}</style></head><body><article class=\"forge-book-designer-pages\">{2}</article></body></html>";
  private static final String PAGE_STYLES = "body { margin:0px } .forge-book-designer-page { box-sizing: border-box; height: 297mm; width: 210mm; page-break-after: always; }";
  
  public BookDesignRenderer(BookDesign bookDesign) {
    this.bookDesign = bookDesign;
  }

  public String toHtml() throws JsonParseException, JsonMappingException, IOException {
    String bodyContent = bookDesign.getData();
    String title = bookDesign.getTitle();
    String styles = getBookDesignStyles();
    return MessageFormat.format(HTML_TEMPLATE, title, styles, bodyContent);
  }
  
  private void printStyleRules(StringBuilder result, Map<String, String> rules) {
    for (String rule : rules.keySet()) {
      result.append(rule);
      result.append(':');
      result.append(rules.get(rule));
      result.append(';');
    }
  }
  
  public String getFontsCss() throws JsonParseException, JsonMappingException, IOException {
    StringBuilder result = new StringBuilder();

    ObjectMapper objectMapper = new ObjectMapper();

    List<BookDesignFont> fonts = null;
    
    if (StringUtils.isNotBlank(bookDesign.getFonts())) {
      fonts = objectMapper.readValue(bookDesign.getFonts(), new TypeReference<List<BookDesignFont>>() { });
    }

    
    if (fonts != null) {
      for (BookDesignFont font : fonts) {
        result.append(String.format("@import url(%s);", font.getUrl()));
      }
    }
    
    return result.toString();
  }
  
  public String getStylesCss() throws JsonParseException, JsonMappingException, IOException {
    StringBuilder result = new StringBuilder();

    ObjectMapper objectMapper = new ObjectMapper();

    List<BookDesignStyle> styles = null;
    List<BookDesignPageType> pageTypes = null;
    
    if (StringUtils.isNotBlank(bookDesign.getStyles())) {
      styles = objectMapper.readValue(bookDesign.getStyles(), new TypeReference<List<BookDesignStyle>>() { });
    }
    
    if (StringUtils.isNotBlank(bookDesign.getPageTypes())) {
      pageTypes = objectMapper.readValue(bookDesign.getPageTypes(), new TypeReference<List<BookDesignPageType>>() { });
    }
    
    if (pageTypes != null) {
      for (BookDesignPageType pageType : pageTypes) {
        Map<String, String> pageRules = pageType.getPageRules();
        Map<String, String> headerRules = pageType.getHeader().getRules();
        Map<String, String> footerRules = pageType.getFooter().getRules();
        
        result.append(String.format(".forge-book-designer-page[data-type-id=\"%s\"] {", pageType.getId()));
        printStyleRules(result, pageRules);
        result.append("}");
  
        result.append(String.format(".forge-book-designer-page[data-type-id=\"%s\"] header {", pageType.getId()));
        printStyleRules(result, headerRules);
        result.append("}");
  
        result.append(String.format(".forge-book-designer-page[data-type-id=\"%s\"] footer {", pageType.getId()));
        printStyleRules(result, footerRules);
        result.append("}");
      }
    }
    
    if (styles != null) {
      for (BookDesignStyle style : styles) {
        result.append(String.format(".forge-book-designer-pages %s {", style.getSelector()));
        printStyleRules(result, style.getRules());
        result.append("}");
      }
    }
    
    return result.toString();
  }

  private String getBookDesignStyles() throws JsonParseException, JsonMappingException, IOException {
    StringBuilder result = new StringBuilder();

    result.append(getFontsCss());
    result.append(PAGE_STYLES);
    result.append(getStylesCss());
    
    return result.toString();
  }

  private BookDesign bookDesign;

  @SuppressWarnings("unused")
  private static class BookDesignPageType {

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
    
    public Boolean getNumberedPage() {
      return numberedPage;
    }
    
    public void setNumberedPage(Boolean numberedPage) {
      this.numberedPage = numberedPage;
    }

    public Map<String, String> getPageRules() {
      return pageRules;
    }

    public void setPageRules(Map<String, String> pageRules) {
      this.pageRules = pageRules;
    }

    public BookDesignTypeHeaderOrFooter getHeader() {
      return header;
    }

    public void setHeader(BookDesignTypeHeaderOrFooter header) {
      this.header = header;
    }

    public BookDesignTypeHeaderOrFooter getFooter() {
      return footer;
    }

    public void setFooter(BookDesignTypeHeaderOrFooter footer) {
      this.footer = footer;
    }

    private String id;
    private String name;
    private Boolean numberedPage;
    private Map<String, String> pageRules;
    private BookDesignTypeHeaderOrFooter header;
    private BookDesignTypeHeaderOrFooter footer;
  }

  @SuppressWarnings("unused")
  private static class BookDesignTypeHeaderOrFooter {
    
    public String getText() {
      return text;
    }
    
    public void setText(String text) {
      this.text = text;
    }

    public Map<String, String> getRules() {
      return rules;
    }
    
    public void setRules(Map<String, String> rules) {
      this.rules = rules;
    }
    
    private String text;
    private Map<String, String> rules;
  }

  @SuppressWarnings("unused")
  private static class BookDesignFont {

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    private String name;
    private String url;
  }

  @SuppressWarnings("unused")
  private static class BookDesignStyle {

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getSelector() {
      return selector;
    }

    public void setSelector(String selector) {
      this.selector = selector;
    }

    public Map<String, String> getRules() {
      return rules;
    }

    public void setRules(Map<String, String> rules) {
      this.rules = rules;
    }

    private String name;
    private String selector;
    private Map<String, String> rules;
  }
}
