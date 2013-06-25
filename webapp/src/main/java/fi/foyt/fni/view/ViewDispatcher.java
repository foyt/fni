package fi.foyt.fni.view;

import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.servlet.RequestUtils;

@WebServlet (urlPatterns = {"*.page", "*.json"}, name = "ViewDispatcher")
public class ViewDispatcher extends AbstractViewServlet {
	
	@Inject 
	private Logger logger;
  
  @Inject
  private SessionController sessionController;

  @Inject
	private ViewControllerMapper viewControllerMapper;

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
		logger.info(request.getMethod() + " - request into " + request.getRequestURI());

		String controllerName = RequestUtils.stripPrecedingSlash(RequestUtils.stripCtxPath(request.getContextPath(), request.getRequestURI()));

		ViewController viewController = viewControllerMapper.getViewController(controllerName);
		if (viewController != null) {
			ViewControllerContext viewControllerContext = new ViewControllerContext(new DefaultParameterHandler(request), request, response, getServletContext());

			try {
				if (viewController.checkPermissions(viewControllerContext)) {
					viewController.execute(viewControllerContext);

					if (!StringUtils.isBlank(viewControllerContext.getRedirectURL())) {
						handleRedirect(response, viewControllerContext.getRedirectURL(), viewControllerContext.getRedirectPermanent());
					} else if (!StringUtils.isBlank(viewControllerContext.getIncludeJSP())) {
						request.setAttribute("jsVariables", viewControllerContext.getJsVariables());
						handleIncludeJsp(request, response, viewControllerContext.getIncludeJSP());
					} else if (viewControllerContext.getData() != null) {
						handleData(request, response, viewControllerContext.getData());
					}
				} else {
					handleForbidden(request, response, sessionController.isLoggedIn());
				}
			} catch (NotFoundException e) {
				handleNotFound(request, response);
			} catch (EJBException e) {
				if (NotFoundException.class.isInstance(e.getCause())) {
					handleNotFound(request, response);
				} else {
  				handleInternalError(request, response, e);
				}
			} catch (Exception e) {
				handleInternalError(request, response, e);
			}
		} else {
			handleNotFound(request, response);
		}
	}
	
	
}
