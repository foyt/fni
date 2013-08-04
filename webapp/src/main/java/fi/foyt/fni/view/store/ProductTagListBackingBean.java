package fi.foyt.fni.view.store;

import java.io.FileNotFoundException;
import java.util.Arrays;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.persistence.model.store.StoreTag;

@SessionScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "store-product-tag-list", 
		pattern = "/gamelibrary/tags/#{productTagListBackingBean.tag}", 
		viewId = "/gamelibrary/producttaglist.jsf"
  )
})
public class ProductTagListBackingBean extends AbstractProductListBackingBean {
	
	@Inject
	private ProductController productController;
	
	@Inject
	private StoreTagController storeTagController;

	@URLAction
	public void init() throws FileNotFoundException {
		storeTag = storeTagController.findTagByText(tag);
		if (storeTag == null) {
			throw new FileNotFoundException();
		}
		
		setProducts(productController.listProductsByTags(Arrays.asList(storeTag)));
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public StoreTag getStoreTag() {
		return storeTag;
	}

	private String tag;
	private StoreTag storeTag;
}