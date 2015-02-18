package fi.foyt.fni.jsf;

import fi.foyt.fni.security.LoggedIn;

public class NavigationController {

  public String accessDenied() {
    return "/error/access-denied.jsf";
  }
  
  public String notFound() {
    return "/error/not-found.jsf";
  }
  
  public String internalError() {
    return "/error/internal-error.jsf";
  }
  
  @LoggedIn
  public String requireLogin(String nextRule) {
    return nextRule;
  }
  
}
