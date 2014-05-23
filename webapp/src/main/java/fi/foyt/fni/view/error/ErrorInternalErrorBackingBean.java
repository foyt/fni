package fi.foyt.fni.view.error;

import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

@Named
@Stateless
public class ErrorInternalErrorBackingBean {
  
  public void preRenderListener() throws Throwable {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ExternalContext externalContext = facesContext.getExternalContext();
    externalContext.setResponseStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

}
