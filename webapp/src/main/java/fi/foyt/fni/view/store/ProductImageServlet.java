package fi.foyt.fni.view.store;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.store.StoreController;

@WebServlet (urlPatterns = "/store/productImage")
public class ProductImageServlet extends HttpServlet {

	private static final long serialVersionUID = 8109481247044843102L;

	@Inject
	private StoreController storeController;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Long productImageId = NumberUtils.createLong(req.getParameter("productImageId"));
		ProductImage productImage = storeController.findProductImageById(productImageId);

		resp.setContentType(productImage.getContentType());
		
		ServletOutputStream outputStream = resp.getOutputStream();
		try {
			outputStream.write(productImage.getContent());
		} finally {
			outputStream.flush();
			outputStream.close();
		}
	}
	
}
