package fi.foyt.fni.view.gamelibrary;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.gamelibrary.ProductController;

@Stateful
@RequestScoped
@Named
public class PublicationAdminBackingBean {
	
	@Inject
	private ProductController productController;

	public void publish() {
		productController.publishProduct(productController.findProductById(productId));
	}

	public void unpublish() {
		productController.unpublishProduct(productController.findProductById(productId));
	}

	public void delete() {
		productController.deleteProduct(productController.findProductById(productId));
	}
	
	public Long getProductId() {
		return productId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	private Long productId;
}
