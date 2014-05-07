package fi.foyt.fni.debug;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter(urlPatterns = "*")
public class DebugTimerFilter implements Filter {
  
  @Inject
  private DebugTimerResults debugTimerResults;
  
  @Inject
  private DebugTimerCollector debugTimerCollector;

  public void destroy() {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    long startTime = System.currentTimeMillis();
    
    chain.doFilter(request, response);
    
    if (request instanceof HttpServletRequest) {
      String view = ((HttpServletRequest) request).getRequestURI().toString();
      Map<Method, List<Long>> calls = debugTimerCollector.getCalls();
      long endTime = System.currentTimeMillis();
      debugTimerResults.addRequestStats(view, calls, endTime - startTime);
    }
  }

  public void init(FilterConfig fConfig) throws ServletException {
  }

}
