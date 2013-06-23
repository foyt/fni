package fi.foyt.fni.view;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.http.CookieUtils;

public class ViewControllerContext {

	public ViewControllerContext(ParameterHandler parameterHandler, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
    this.parameterHandler = parameterHandler;
    this.request = request;
    this.response = response;
    this.servletContext = servletContext;

    try {
      this.request.setCharacterEncoding("UTF-8");
      this.response.setCharacterEncoding("UTF-8");
    } catch (UnsupportedEncodingException e) {
    }
  }

  public String getStringParameter(String name) {
    String[] parameterValues = parameterHandler.getParameterValues(name);
    String parameterValue = parameterValues != null && parameterValues.length > 0 ? parameterValues[0] : null;
    if (!StringUtils.isBlank(parameterValue)) {
      return parameterValue;
    } else {
      return null;
    }
  }
  
  public Set<String> getStringParameters(String name) {
    Set<String> result = new HashSet<String>();
  
    String[] values = parameterHandler.getParameterValues(name);
    if (values != null) {
      for (String value : values) {
        result.add(value);
      }
    }
    
    return result;
  }
  
  public Long getLongParameter(String name) {
    return NumberUtils.createLong(getStringParameter(name));
  }
  
  public Integer getIntegerParameter(String name) {
    return NumberUtils.createInteger(getStringParameter(name));
  }
  
  public Double getDoubleParameter(String name) {
    return NumberUtils.createDouble(getStringParameter(name));
  }
  
  public Float getFloatParameter(String name) {
    return NumberUtils.createFloat(getStringParameter(name));
  }
  
  public Set<Long> getLongParameters(String name) {
    Set<String> stringValues = getStringParameters(name);
    Set<Long> result = new HashSet<Long>(stringValues.size());
    
    for (String stringValue : stringValues) {
      Long longValue = NumberUtils.createLong(stringValue);
      if (longValue != null)
        result.add(longValue);
    }
    
    return result;
  }
  
  public Cookie getCookie(String name) {
  	return CookieUtils.getCookie(request, name);
  }
  
  public String getCookieValue(String name) {
  	return CookieUtils.getCookieValue(request, name);
  }
  
  public Cookie addCookie(String name, String value, Date expires, String path) {
    Cookie cookie = new Cookie(name, value);
    
    if (path != null)
      cookie.setPath(path);
    
    if (expires != null)
      cookie.setMaxAge((int) (expires.getTime() - System.currentTimeMillis()) / 1000);
    
    getResponse().addCookie(cookie);
    
    return cookie;
  }

  public HttpServletRequest getRequest() {
    return request;
  }

  public HttpServletResponse getResponse() {
    return response;
  }

  public ServletContext getServletContext() {
    return servletContext;
  }

  public Long getUserId() {
    HttpSession session = request.getSession(false);
    if ((session == null) || session.isNew())
      return null;

    return (Long) session.getAttribute("loggedUserId");
  }
  
  public boolean isLoggedIn() {
    HttpSession session = getRequest().getSession(false);
    if (session != null && session.getAttribute("loggedUserId") != null)
      return true;
    return false;
  }
  
  public String getBasePath() {
    String requestURL = getRequest().getRequestURL().toString();
    String requestURI = getRequest().getRequestURI();
    String contextPath = getRequest().getContextPath();
    String result = requestURL.substring(0, requestURL.length() - requestURI.length()) + contextPath; 
    return result;
  }
  
  public String getFullRequestURL() {
    StringBuilder fullURLBuilder = new StringBuilder();
    
    fullURLBuilder.append(request.getRequestURL().toString());
    java.util.Enumeration<String> parameterNames = request.getParameterNames();
    
    if (parameterNames.hasMoreElements()) {
      fullURLBuilder.append('?');
      while (parameterNames.hasMoreElements()) {
        String parameterName = parameterNames.nextElement();
        String parameterValue = request.getParameter(parameterName);
        fullURLBuilder.append(parameterName).append('=').append(parameterValue.length() > 30 ? "[DATA]" : parameterValue);
        if (parameterNames.hasMoreElements()) {      
          fullURLBuilder.append('&');
        }
      }
    }
    
    return fullURLBuilder.toString();
  }
  
  public String getReferer() {
    return request.getHeader("Referer");
  }
  
  public void setIncludeJSP(String includeJSP) {
    this.includeJSP = includeJSP;
  }
  
  public String getIncludeJSP() {
    return includeJSP;
  }
  
  public TypedData getData() {
  	return data;
  }
  
  public void setData(TypedData data) {
  	this.data = data;
  }
  
  public void setRedirect(String url, boolean permanent) {
    this.redirectURL = url;
    this.redirectPermanent = permanent;
  }
  
  public Boolean getRedirectPermanent() {
    return redirectPermanent;
  }
  
  public String getRedirectURL() {
    return redirectURL;
  }
  
  public ParameterHandler getParameterHandler() {
    return parameterHandler;
  }

	public void addMessage(MessageSeverity severity, String text) {
		Long messageCount = getLongJsVariable("messages.count");
		if (messageCount == null) {
			messageCount = 0l;
		}
		
		setJsVariable("message." + messageCount + ".severity", severity.toString());
		setJsVariable("message." + messageCount + ".text", text);

		messageCount++;

		setJsVariable("messages.count", messageCount.toString());
  }
	
	public Map<String, String> getJsVariables() {
	  return jsVariables;
  }
	
	public void setJsVariable(String key, String value) {
		jsVariables.put(key, value);
	}
	
	private Long getLongJsVariable(String key) {
		return NumberUtils.createLong(getJsVariable(key));
	}
	
	private String getJsVariable(String key) {
		return getJsVariables().get(key);
	}
	
  private HttpServletRequest request;
  private HttpServletResponse response;
  private ServletContext servletContext;
  private String includeJSP;
  private String redirectURL;
  private TypedData data;
  private Boolean redirectPermanent;
  private ParameterHandler parameterHandler;
  private Map<String, String> jsVariables = new HashMap<String, String>();

	public enum MessageSeverity {
	  INFO,
	  WARNING,
	  SERIOUS,
	  CRITICAL
	};
	
}
