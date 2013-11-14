package fi.foyt.fni.delivery;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;

@RequestScoped
@Stateful
public class DeliveryMehtodsController {

	@PostConstruct
	public void init() {
		deliveryMethods = new ArrayList<>();
		deliveryMethods.add(new PostiFreeDeliveryMethod());
		
//		deliveryMethods.add(new PostiDeliveryMethod());
//		deliveryMethods.add(new KopiopisteDeliveryMethod());
	}
	
	public List<DeliveryMethod> getDeliveryMethods() {
		return deliveryMethods;
	}

	public DeliveryMethod findDeliveryMethod(String deliveryMethodId) {
		for (DeliveryMethod deliveryMethod : deliveryMethods) {
			if (deliveryMethod.getId().equals(deliveryMethodId))
				return deliveryMethod;
		}
		
		return null;
	}
	
	private List<DeliveryMethod> deliveryMethods;

	public DeliveryMethod getDefaultDeliveryMethod() {
		return deliveryMethods.get(0);
	}
}
