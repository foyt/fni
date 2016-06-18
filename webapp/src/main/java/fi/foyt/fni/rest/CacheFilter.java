package fi.foyt.fni.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class CacheFilter implements ContainerResponseFilter {

  @Context
  private HttpServletRequest request;
  
  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    if ("GET".equals(requestContext.getMethod()) && responseContext.getStatus() == 200) {
      MultivaluedMap<String,Object> headers = responseContext.getHeaders();
      if (!headers.containsKey("Cache-Control")) {
        headers.putSingle("Cache-Control", "no-cache, no-store, must-revalidate"); 
        headers.putSingle("Pragma", "no-cache");
        headers.putSingle("Expires", "0");
      }
    }
  }
  
}
