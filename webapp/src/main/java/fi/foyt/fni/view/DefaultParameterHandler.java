package fi.foyt.fni.view;

import javax.servlet.http.HttpServletRequest;

public class DefaultParameterHandler implements ParameterHandler {

  public DefaultParameterHandler(HttpServletRequest httpServletRequest) {
    this.httpServletRequest = httpServletRequest;
  }
  
  public String[] getParameterValues(String name) {
    return httpServletRequest.getParameterValues(name);
  };
 
  private HttpServletRequest httpServletRequest;
}
