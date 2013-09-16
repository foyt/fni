package fi.foyt.fni.gamelibrary;

import javax.ejb.Stateless;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.Role;
import fi.foyt.fni.security.PermissionCheck;
import fi.foyt.fni.security.PermissionCheckImplementation;
import fi.foyt.fni.security.SecurityException;
import fi.foyt.fni.session.SessionController;

@Stateless
@PermissionCheck (Permission.GAMELIBRARY_VIEW_ORDER)
public class GameLibraryViewOrderPermissionCheck implements PermissionCheckImplementation<Long> {
	
	@Inject
	private SessionController sessionController;
	
	@Inject
	private OrderController orderController;

	@Override
	public boolean checkPermission(Long orderId) {
		if (!sessionController.isLoggedIn()) {
			// Viewing orders always needs logged users. Orders done by not logged users
			// can be viewed only by Game Library Managers
			return false;
		}

		if (orderId == null) {
			throw new SecurityException("Could not resolve orderId while checking permission for GAMELIBRARY_VIEW_ORDER");
		}
		
		Order order = orderController.findOrderById(orderId);
		if (order == null) {
			throw new SecurityException("Could not resolve order while checking permission for GAMELIBRARY_VIEW_ORDER");
		}
		
		if (sessionController.hasLoggedUserRole(Role.GAME_LIBRARY_MANAGER)) {
			// Game Library Managers may view all orders
			return true;
		}
		
		if (order.getCustomer() == null) {
			// Orders done by not logged users can be viewed only by Game Library Managers
			return false;
		}
		
		if (order.getCustomer().getId().equals(sessionController.getLoggedUserId())) {
			// Users may view their own orders
			return true;
		}
		
		return false;
	}

}
