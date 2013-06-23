package fi.foyt.fni.ckcc;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import fi.foyt.ckc.CKCConnector;

public class CKCConnectorServlet extends fi.foyt.ckc.CKCConnectorServlet {
	
  private static final long serialVersionUID = 1L;
  
  @Inject
  private CKCConnector connector;
  
  @Override
  public void init(ServletConfig config) throws ServletException {
    setConnector(connector);
  }

}