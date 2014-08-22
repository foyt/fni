package fi.foyt.fni.view;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.paytrail.PaytrailService;

@WebServlet(urlPatterns = "/paytrail/*", name = "gamelibrary-paytrail")
@Transactional
public class PaytrailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private PaytrailService paytrailService;

	@Inject
	private OrderController orderController;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = getPathAction(request);
		switch (action) {
			case "success":
				handleSuccess(request, response);
			break;
			case "failure":
				handleFailure(request, response);
			break;
			case "notify":
				handleSuccess(request, response);
			break;
		}
	}

	private void handleFailure(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String orderNumber = request.getParameter("ORDER_NUMBER");
		Long orderId = NumberUtils.createLong(orderNumber);
		Order order = orderController.findOrderById(orderId);
		if (order != null) {
			orderController.updateOrderAsCanceled(order);
			response.sendRedirect(request.getContextPath() + getOrderRedirectUrl(order));
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
	}

	private void handleSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (validate(request)) {
			String orderNumber = request.getParameter("ORDER_NUMBER");
			Long orderId = NumberUtils.createLong(orderNumber);
			Order order = orderController.findOrderById(orderId);
			if (order != null) {
			  switch (order.getType()) {
			    case GAMELIBRARY_BOOK:
		        if (order.getDeliveryAddress() == null) {
		          orderController.updateOrderAsPaid(order);
		        } else {
		          orderController.updateOrderAsWaitingForDelivery(order);
		        }
		        
            response.sendRedirect(request.getContextPath() + getOrderRedirectUrl(order));
			    break;
			    case ILLUSION_GROUP:
            orderController.updateOrderAsPaid(order);
            response.sendRedirect(request.getContextPath() + getIllusionRedirectUrl(order));
			    break;
			  }

			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
	}

	private boolean validate(HttpServletRequest request) {
		String orderNumber = request.getParameter("ORDER_NUMBER");
		String timestamp = request.getParameter("TIMESTAMP");
		String paid = request.getParameter("PAID");
		String method = request.getParameter("METHOD");
		String authCode = request.getParameter("RETURN_AUTHCODE");

		return paytrailService.confirmPayment(orderNumber, timestamp, paid, method, authCode);
	}
	
  private String getOrderRedirectUrl(Order order) {
    StringBuilder resultBuilder = new StringBuilder()
      .append("/gamelibrary/orders/")
      .append(order.getId());
    
    if (StringUtils.isNotBlank(order.getAccessKey())) {
      resultBuilder.append("?key=").append(order.getAccessKey());
    }
    
    return resultBuilder.toString();
  }
	 
	private String getIllusionRedirectUrl(Order order) {
    IllusionEvent illusionEvent = orderController.findOrderIllusionEvent(order);

    return new StringBuilder()
      .append("/illusion/group/")
      .append(illusionEvent.getUrlName())
      .toString();
	}

	private String getPathAction(HttpServletRequest req) {
		String pathInfo = req.getPathInfo();
		int lastSlash = pathInfo.lastIndexOf('/');
		return pathInfo.substring(lastSlash + 1);
	}
}
