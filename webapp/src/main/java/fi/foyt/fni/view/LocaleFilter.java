package fi.foyt.fni.view;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.jsp.jstl.core.Config;

import fi.foyt.fni.session.SessionController;

@WebFilter (servletNames = "ViewDispatcher", urlPatterns = { "/forge/*", "/forum/*" })
public class LocaleFilter implements Filter {
	
	@Inject
	private SessionController sessionController;

  public void init(FilterConfig arg0) throws ServletException {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    ServletRequest servletRequest = request;
    try {
      if (request instanceof HttpServletRequest) {
      	Locale locale = sessionController.getLocale();
        Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(locale));
        servletRequest = new LocaleRequestWrapper((HttpServletRequest) request, locale);
      }
    } finally {
      filterChain.doFilter(servletRequest, response);
    }
  }

  public void destroy() {
  }

  private class LocaleRequestWrapper extends HttpServletRequestWrapper {
    
    public LocaleRequestWrapper(HttpServletRequest req, Locale locale) {
      super(req);
      this.locale = locale;
    }

    public Enumeration<Locale> getLocales() {
      Vector<Locale> v = new Vector<Locale>(1);
      v.add(getLocale());
      return v.elements();
    }

    public Locale getLocale() {
      return locale;
    }

    private Locale locale;
  }

  private class LocalizationContext extends javax.servlet.jsp.jstl.fmt.LocalizationContext {
    public LocalizationContext(Locale locale) {
      super(Locales.getResourceBundle(locale), locale);
    }
  }

}
