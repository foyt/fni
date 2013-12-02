package fi.foyt.fni;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebFilter(servletNames = { 
  "search",
  "materials",
  "users-profileimage", 
  "gamelibrary-publicationimage", 
  "gamelibrary-publicationfile",
  "gamelibrary-paytrail",
  "forge-folderbrowser",
  "forge-binary",
  "forge-googledrive",
  "forge-pdf",
  "forge-materialshare",
  "forge-ckbrowser",
  "forge-upload",
  "forge-coops"
} )
public class SessionMutexFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpSession session = null;
		if (request instanceof HttpServletRequest) {
			session = ((HttpServletRequest) request).getSession(false);
		}
		
		if (session == null) {
			chain.doFilter(request, response);
		} else {
  		String sessionId = session.getId();
  		Mutex mutex = getMutex(sessionId); 
  		try {
    		synchronized (mutex) {
    			chain.doFilter(request, response);
    		}
  		} finally {
    		releaseMutex(sessionId, mutex);
  		}
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
	
	protected synchronized Mutex getMutex(String sessionId) {
		Mutex mutex = mutexMap.get(sessionId);
		if (mutex == null) {
			mutex = new Mutex();
			mutexMap.put(sessionId, mutex);
		} 
		
		mutex.incRefCount();

		return mutex;
	}

	protected synchronized void releaseMutex(String sessionId, Mutex mutex) {
		mutex.decRefCount();

		if (mutex.getRefCount() <= 0) {
			mutexMap.remove(sessionId);
		}
	}

	private Map<String, Mutex> mutexMap = new HashMap<>();
	
	protected class Mutex {
		
		public void incRefCount() {
			refCount++;
		};
		
		public void decRefCount() {
			refCount--;
		}
		
		public int getRefCount() {
			return refCount;
		}
		
		private int refCount;
	}
}
