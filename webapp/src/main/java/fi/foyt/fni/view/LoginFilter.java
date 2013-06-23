package fi.foyt.fni.view;

import java.io.IOException;
import java.net.URLEncoder;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter (
  urlPatterns = { 
  		"/illusion/session.jsf",
  		"/editprofile.jsf"
  }
)

// TODO: Replace this when annotation based permission checks work (https://code.google.com/p/fni/issues/detail?id=144)
public class LoginFilter implements Filter {
  
  @Inject
  private fi.foyt.fni.session.SessionController sessionController;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (sessionController.isLoggedIn()) {
      chain.doFilter(request, response);
    } else {
      HttpServletRequest servletRequest = (HttpServletRequest) request;
      HttpServletResponse servletResponse = (HttpServletResponse) response;
      String contextPath = servletRequest.getContextPath();
      String redirectUrl = servletRequest.getRequestURI();

      servletResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
      servletResponse.setHeader("Location", contextPath + "/login?redirectUrl=" + URLEncoder.encode(redirectUrl, "UTF-8"));
    }
  }

  @Override
  public void destroy() {
  }

}
