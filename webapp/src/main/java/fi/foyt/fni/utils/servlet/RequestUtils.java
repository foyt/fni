package fi.foyt.fni.utils.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class RequestUtils {

  public static String requestParamsAsUrlString(HttpServletRequest httpServletRequest) throws UnsupportedEncodingException {
    StringBuilder result = new StringBuilder();
    
    Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
    
    while (parameterNames.hasMoreElements()) {
      String parameterName = parameterNames.nextElement();
      String[] parameterValues = httpServletRequest.getParameterValues(parameterName);
      for (int i = 0, l = parameterValues.length; i < l; i++) {
        result.append(URLEncoder.encode(parameterName, "UTF-8"));
        result.append('=');
        result.append(URLEncoder.encode(parameterValues[i], "UTF-8"));
        
        if (i < (parameterValues.length - 1)) {
          result.append('&');
        }
      }
      
      if (parameterNames.hasMoreElements())
        result.append('&');
    }
    
    return result.toString();
  }
  
  public static String stripCtxPath(String contextPath, String uri) {
    if (!StringUtils.isBlank(contextPath) && uri.startsWith(contextPath))
      return uri.substring(contextPath.length());
    return uri;
  }
  
  public static String stripPrecedingSlash(String url) {
  	if (url.startsWith("/")) {
      return url.substring(1);
    }
    
    return url;
  }
  
  public static String stripTrailingSlash(String url) {
    if (url.endsWith("/")) {
      return url.substring(0, url.length() - 1);
    }
    
    return url;
  }
  
  public static String extractToNextSlash(String uri) {
    int nextSlash = uri.indexOf('/');
    if (nextSlash > -1) {
      return uri.substring(0, nextSlash);
    }
    
    return uri;
  }

  public static String createUrlName(String text, int maxLength) {
  	String urlName = StringUtils.normalizeSpace(text);
    if (StringUtils.isBlank(urlName))
      return null;
    
    urlName = StringUtils.lowerCase(StringUtils.substring(StringUtils.stripAccents(urlName.replaceAll(" ", "_")).replaceAll("[^a-zA-Z0-9\\-\\.\\_]", ""), 0, maxLength));
    if (StringUtils.isBlank(urlName)) {
      urlName = UUID.randomUUID().toString();
    }
    
    return urlName;
  }
  
  public static String createUrlName(String text) {
    return createUrlName(text, 20);
  }

  public static String getRequestHostUrl(HttpServletRequest request) {
    String requestURL = request.getRequestURL().toString();
    String requestURI = request.getRequestURI();
    return requestURL.substring(0, requestURL.length() - requestURI.length());     
  }
  
  public static boolean isModifiedSince(HttpServletRequest request, Long lastModified, String eTag) throws IOException {
    // If 'If-None-Match' header contains * or matches the ETag send 304
    String ifNoneMatch = request.getHeader("If-None-Match");
    if ("*".equals(ifNoneMatch) || eTag.equals(ifNoneMatch)) {
      return false;
    }

    // If 'If-Modified-Since' header is greater than LastModified send 304.
    long ifModifiedSince = request.getDateHeader("If-Modified-Since");
    if ((ifNoneMatch == null) && (ifModifiedSince != -1) && ((ifModifiedSince + 1000) > lastModified)) {
      return false;
    }

    return true;
  }
}
