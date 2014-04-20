package fi.foyt.fni.view.error;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

@Named
@Stateless
public class ErrorForbiddenBackingBean {

  public void preRenderListener() throws IOException {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ExternalContext externalContext = facesContext.getExternalContext();
    externalContext.setResponseStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
  
}
