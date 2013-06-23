package fi.foyt.fni.utils.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtils {

  public static Cookie getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (name.equals(cookie.getName()))
          return cookie;
      }
    }
    
    return null;
  }
  
  public static String getCookieValue(HttpServletRequest request, String name) {
    Cookie cookie = getCookie(request,name);
    if (cookie != null)
      return cookie.getValue();
    return null;
  }
  
}
