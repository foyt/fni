package fi.foyt.fni.illusion;

import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.gamelibrary.OrderEvent;
import fi.foyt.fni.gamelibrary.OrderPaid;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;

public class OrderStatusChangeListener {
	
	@Inject
	private Logger logger;
	
	@Inject
	private OrderController orderController;

	@Inject
	private IllusionGroupController illusionGroupController;
	
	public void onOrderPaid(@Observes @OrderPaid OrderEvent event) {
		if (event.getOrderId() != null) {
  		Order order = orderController.findOrderById(event.getOrderId()); 
  		if (order != null) {
  		  switch (order.getType()) {
  		    case ILLUSION_GROUP:
  		      IllusionGroup group = orderController.findOrderIllusionGroup(order);
  		      if (group != null) {
  		        IllusionGroupMember member = illusionGroupController.findIllusionGroupMemberByUserAndGroup(group, order.getCustomer());
    		      illusionGroupController.updateIllusionGroupMemberRole(member, IllusionGroupMemberRole.PLAYER);
  		      } else {
  		        logger.severe("Tried to lift illusion group member role to player for non-existing group");
  		      }
  		    break;
  		    case GAMELIBRARY_BOOK:
  		    break;
  		  }
  		} else {
  			logger.severe("Tried to lift illusion group member role to player for non-existing order");
  		}
		} else {
      logger.severe("Tried to lift illusion group member role to player for non-existing order");
		}
	}

}
