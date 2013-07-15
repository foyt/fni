package fi.foyt.fni.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import fi.foyt.fni.persistence.model.store.FileProduct;
import fi.foyt.fni.persistence.model.store.FileProductFile;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.store.StoreDetail;
import fi.foyt.fni.persistence.model.store.StoreTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.rest.entities.store.BookProduct;
import fi.foyt.fni.rest.entities.store.PremiumAccountProduct;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.store.StoreController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.images.ImageUtils;
import fi.foyt.fni.utils.language.GuessedLanguage;
import fi.foyt.fni.utils.language.LanguageUtils;

@Path("/2/store")
@RequestScoped
@Stateful
@Produces("application/json")
@Consumes("application/json")
public class StoreService {
	
	@Inject
	private SystemSettingsController systemSettingsController;

	@Inject
	private SessionController sessionController;

	@Inject
	private StoreController storeController;

	@POST
	@Path("/products")
	public Response createProduct(fi.foyt.fni.rest.entities.store.Product product) {
		// TODO: Permissions
		User loggedUser = sessionController.getLoggedUser();

		if (product instanceof BookProduct) {
			BookProduct bookProduct = (BookProduct) product;
				// TODO: illegal id, created, creator, contentType, downloadUrl, modified, modifier

				List<StoreTag> tags = new ArrayList<>();
				for (String tag : bookProduct.getTags()) {
					StoreTag storeTag = storeController.findTagByText(tag);
					if (storeTag == null) {
						storeTag = storeController.createTag(tag);
					}

					tags.add(storeTag);
				}

				Long defaultImageId = bookProduct.getDefaultImage() != null ? bookProduct.getDefaultImage().getId() : null;
				ProductImage defaultImage = defaultImageId != null ? storeController.findProductImageById(defaultImageId) : null;

				return Response.ok().entity(toEntity(
					storeController.createBookProduct(loggedUser, bookProduct.getNames(), bookProduct.getDescriptions(), 
					  bookProduct.getRequiresDelivery(), bookProduct.getDownloadable(), 
					  bookProduct.getPrice(), defaultImage, tags, bookProduct.getDetails()
					)
			  )).build();
		} else {
			// TODO: Localize exception 
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown product type").build();
		}
	}

	@GET
	@Path("/products/{PRODUCTID}")
	public Response getProduct(@PathParam("PRODUCTID") Long productId, @Context HttpHeaders httpHeaders) {
		Product product = storeController.findProductById(productId);
		
		// TODO: Permissions
		
		if (product == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		return Response.ok(toEntity(product)).build();
	}

	@GET
	@Path("/products")
	public Response listProducts(@QueryParam("published") Boolean published, @QueryParam("tag") String[] tags, @Context HttpHeaders httpHeaders) {
		if (published != null) {
			// TODO: Own products only...
		}

		List<Product> products = null;
//		Locale locale = getRequestLocale(httpHeaders);

		if ((tags == null) || (tags.length == 0)) {
			products = storeController.listAllProducts();
		} else {
			products = storeController.listProductsByTags(tags);
		}

		if (published == null) {
			published = true;
		}

		List<fi.foyt.fni.rest.entities.store.Product> result = new ArrayList<>();

		for (Product product : products) {
			if (product.getPublished().equals(published)) {
				fi.foyt.fni.rest.entities.store.Product productEntity = null;

				if (product instanceof fi.foyt.fni.persistence.model.store.BookProduct) {
					productEntity = toEntity((fi.foyt.fni.persistence.model.store.BookProduct) product);
				} else if (product instanceof fi.foyt.fni.persistence.model.store.PremiumAccountProduct) {
					productEntity = toEntity((fi.foyt.fni.persistence.model.store.PremiumAccountProduct) product);
				} else {
					// TODO: Localize error message
					return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown product type").build();
				}

				result.add(productEntity);
			}
		}

		return Response.ok(result).build();
	}

	@Path("/products/{PRODUCTID}")
	@PUT
	public Response updateProduct(@PathParam("PRODUCTID") Long productId, fi.foyt.fni.rest.entities.store.Product product, @Context HttpHeaders httpHeaders) {
		// TODO: Permissions
		User loggedUser = sessionController.getLoggedUser();
		
		if (product.getId().equals(productId)) {
  		if (product instanceof BookProduct) {
  			BookProduct bookProductEntity = (BookProduct) product;
  			fi.foyt.fni.persistence.model.store.BookProduct bookProduct = storeController.findBookProductById(productId);

  			storeController.updateBookProduct(bookProduct,  
  	  			bookProductEntity.getPrice(),
  	  			bookProductEntity.getNames(),
  	  			bookProductEntity.getDescriptions(),
  	  			bookProductEntity.getDetails(),
  	  			bookProductEntity.getTags(),
  	  			bookProductEntity.getPublished(), 
  	  			bookProductEntity.getRequiresDelivery(),
  					bookProductEntity.getDownloadable(),
  					loggedUser
  			);
  			
  			
  		} else if (product instanceof PremiumAccountProduct) {
//  			PremiumAccountProduct premiumAccountProduct = (PremiumAccountProduct) product;
//  		} else {
  			// TODO: Localize exception
  			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Cannot update product, unknown type").build();
  		}
		} else {
			return Response.status(Status.BAD_REQUEST).entity("Entity id does not match path id").build();
		}
		
		return Response.status(Status.NO_CONTENT).build();
	}

	@Path("/products/{PRODUCTID}")
	@DELETE
	public Response deleteProduct(@PathParam("PRODUCTID") Long productId, @Context HttpHeaders httpHeaders) {
		// TODO: Permission check
		
		fi.foyt.fni.persistence.model.store.Product product = storeController.findProductById(productId);
		if (product == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		storeController.deleteProduct(product);
		
		return Response.status(Status.NO_CONTENT).build();
	}
	
	@Path("/products/{PRODUCTID}/files")
	@POST
	@Consumes("multipart/form-data")
	public Response uploadProductFile(@PathParam("PRODUCTID") Long productId, MultipartFormDataInput input, @Context HttpHeaders httpHeaders) {
		// TODO: Permission check

		fi.foyt.fni.persistence.model.store.FileProduct fileProduct = storeController.findFileProductById(productId);
		if (fileProduct == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

//		Locale locale = getRequestLocale(httpHeaders);

		if (fileProduct.getFile() != null) {
			// TODO: Localize error
			return Response.status(Status.BAD_REQUEST).entity("product already contains a file").build();
		}

		FileProductFile fileProductFile = null;
		Map<String, List<InputPart>> formDataMap = input.getFormDataMap();

		List<InputPart> fileParts = formDataMap.get("file");
		if (fileParts != null) {
			for (InputPart filePart : fileParts) {

				try {
					InputStream bodyStream = filePart.getBody(InputStream.class, null);
					try {
						byte[] data = IOUtils.toByteArray(bodyStream);
						String contentType = filePart.getMediaType().toString();
						fileProductFile = storeController.createFileProductFile(data, contentType);
						storeController.updateFileProductFile(fileProduct, fileProductFile);

						MediaType mediaType = MediaType.valueOf(contentType);
						if (!(("application".equals(mediaType.getType())) && ("pdf".equals(mediaType.getSubtype())))) {
							// TODO: Localize error
							return Response.status(Status.BAD_REQUEST).entity("file is not a pdf file").build();
						}
						
						ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(data);
						try {
							PDDocument pdfDocument = PDDocument.load(pdfInputStream);
						  try { 
  							PDDocumentInformation documentInformation = pdfDocument.getDocumentInformation();
  							PDDocumentCatalog documentCatalog = pdfDocument.getDocumentCatalog();
  
  							// Try to figure out the language by language specified in PDF
  							
  							String language = documentCatalog.getLanguage();
  							if (StringUtils.isNotBlank(language)) {
  							  int index = language.indexOf('-');
  							  if (index == 2) {
  							  	language = LocaleUtils.toLocale(language.substring(0, 2)).getLanguage();
  							  }
  							}
  							
  							if (StringUtils.isBlank(language)) {
  								PDFTextStripper pdfTextStripper = new PDFTextStripper();
  								String textContent = pdfTextStripper.getText(pdfDocument);
  								List<GuessedLanguage> guessedLanguages = LanguageUtils.getGuessedLanguages(textContent, 0.8);
  								if (guessedLanguages.size() > 0) {
  									language = guessedLanguages.get(0).getLanguageCode();
  								}
  							} 
  
  							if (StringUtils.isNotBlank(language)) {
  								language = LocaleUtils.toLocale(language).getLanguage();
  							}
  
  							int numberOfPages = pdfDocument.getNumberOfPages();
  							String author = documentInformation.getAuthor();
  							String subject = documentInformation.getSubject();
  //							String keywords = documentInformation.getKeywords();
  							
  							if (numberOfPages > 0) {
  								storeController.setProductDetail(fileProduct, "number-of-pages", String.valueOf(numberOfPages));
  							}
  							
  							if (StringUtils.isNotBlank(author)) {
  								storeController.setProductDetail(fileProduct, "author", author);
  							}
  							
  							if (StringUtils.isNotBlank(subject)) {
  								storeController.setProductDetail(fileProduct, "subject", subject);
  							}
							} finally {
								pdfDocument.close();
							}
						} finally {
							pdfInputStream.close();
						}

					} finally {
						bodyStream.close();
					}
				} catch (IOException e) {
					return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
				}
			}
		} else {
			// TODO: Localize error
			return Response.status(Status.BAD_REQUEST).entity("file not specified").build();
		}

		if (fileProduct instanceof fi.foyt.fni.persistence.model.store.BookProduct) {
			return Response.status(Status.OK).entity(toEntity((fi.foyt.fni.persistence.model.store.BookProduct) fileProduct)).build();
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not build result, unknown type").build();
		}
	}

	@Path("/products/{PRODUCTID}/files/{PRODUCTFILEID}")
	@GET
	public Response getProductFile(@PathParam("PRODUCTID") Long productId, @PathParam("PRODUCTFILEID") Long productFileId, @Context HttpHeaders httpHeaders) {
		FileProduct fileProduct = storeController.findFileProductById(productId);
		if ((fileProduct == null) || (fileProduct.getFile() == null) || (!fileProduct.getFile().getId().equals(productFileId))) {
			return Response.status(Status.NOT_FOUND).build();
		}

		Locale locale = getRequestLocale(httpHeaders);

		// TODO: Permissions

		FileProductFile fileProductFile = fileProduct.getFile();
		String productName = fileProduct.getName().getValue(locale);
		String urlName = StringUtils.lowerCase(StringUtils.stripAccents(StringUtils.substring(StringUtils.normalizeSpace(productName), 0, 20).replaceAll(" ", "_")));
		MediaType mediaType = MediaType.valueOf(fileProductFile.getContentType());

		if ("application".equals(mediaType.getType()) && "pdf".equals(mediaType.getSubtype())) {
			urlName += ".pdf"; 
		}

		return createBinaryResponse(fileProductFile.getContent(), fileProductFile.getContentType(), urlName);
	}

	@Path("/products/{PRODUCTID}/images")
	@POST
	@Consumes("multipart/form-data")
	public Response uploadProductImages(@PathParam("PRODUCTID") Long productId, MultipartFormDataInput input, @Context HttpHeaders httpHeaders) {
		// TODO: Permission check

		fi.foyt.fni.persistence.model.store.Product product = storeController.findProductById(productId);
		if (product == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

//		Locale locale = getRequestLocale(httpHeaders);
		User loggedUser = sessionController.getLoggedUser();
		List<fi.foyt.fni.rest.entities.store.ProductImage> result = new ArrayList<>();

		Map<String, List<InputPart>> formDataMap = input.getFormDataMap();

		List<InputPart> fileParts = formDataMap.get("files");
		if (fileParts != null) {
			for (InputPart filePart : fileParts) {
				try {
					InputStream bodyStream = filePart.getBody(InputStream.class, null);
					try {
						byte[] data = IOUtils.toByteArray(bodyStream);
						String contentType = filePart.getMediaType().toString();
						ProductImage productImage = storeController.createProductImage(product, data, contentType, loggedUser);
						if (product.getDefaultImage() == null) {
							storeController.updateProductDefaultImage(product, productImage);
						}
						result.add(toEntity(productImage));
					} finally {
						bodyStream.close();
					}
				} catch (IOException e) {
					return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
				}
			}
		} else {
			// TODO: Localize error
			return Response.status(Status.BAD_REQUEST).entity("file not specified").build();
		}

		return Response.status(Status.OK).entity(result).build();
	}

	@Path("/products/{PRODUCTID}/images")
	@GET
	public Response getProductImage(@PathParam("PRODUCTID") Long productId, @Context HttpHeaders httpHeaders) {
		// TODO: Permissions

		Product product = storeController.findProductById(productId);
		if (product == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		List<fi.foyt.fni.rest.entities.store.ProductImage> result = new ArrayList<>();
		List<ProductImage> productImages = storeController.listProductImageByProduct(product);
		for (ProductImage productImage : productImages) {
			result.add(toEntity(productImage));
		}

		return Response.ok().entity(result).build();
	}

	@Path("/products/{PRODUCTID}/images/{PRODUCTIMAGEID}")
	@GET
	public Response getProductImage(@PathParam("PRODUCTID") Long productId, @PathParam("PRODUCTIMAGEID") Long productImageId, @Context HttpHeaders httpHeaders) {
		// TODO: Permissions

		Product product = storeController.findProductById(productId);
		if (product == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		ProductImage productImage = storeController.findProductImageById(productImageId);
		if (productImage == null || (!productImage.getProduct().getId().equals(product.getId()))) {
			return Response.status(Status.NOT_FOUND).build();
		}

		return Response.ok().entity(toEntity(productImage)).build();
	}

	@Path("/products/{PRODUCTID}/images/{PRODUCTIMAGEID}/content")
	@GET
	public Response getProductImageContent(@PathParam("PRODUCTID") Long productId, @PathParam("PRODUCTIMAGEID") Long productImageId,
			@QueryParam("width") Integer width, @QueryParam("height") Integer height, @Context HttpHeaders httpHeaders) {
		// TODO: Permissions
		// TODO: Client side cache support

		Product product = storeController.findProductById(productId);
		if (product == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		ProductImage productImage = storeController.findProductImageById(productImageId);
		if (productImage == null || (!productImage.getProduct().getId().equals(product.getId()))) {
			return Response.status(Status.NOT_FOUND).build();
		}

//		Locale locale = getRequestLocale(httpHeaders);

		TypedData data = new TypedData(productImage.getContent(), productImage.getContentType());
		if ((width != null) && (height != null)) {
			try {
				data = ImageUtils.resizeImage(data, width, height, null);
			} catch (IOException e) {
				// TODO: Localize exception
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to resize image").build();
			}
		}

		return createBinaryResponse(data.getData(), data.getContentType(), null);
	}
	
	@Path("/products/{PRODUCTID}/details")
	@GET
	public Response getProductDetails(@PathParam("PRODUCTID") Long productId, @Context HttpHeaders httpHeaders) {
		// TODO: Permissions

		Product product = storeController.findProductById(productId);
		if (product == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		Map<String, String> details = storeController.getProductDetailMap(product);

		return Response.ok().entity(details).build();
	}
	
	@Path("/products/{PRODUCTID}/details")
	@PUT
	public Response updateProductDetails(@PathParam("PRODUCTID") Long productId, Map<String, String> details, @Context HttpHeaders httpHeaders) {
		// TODO: Permissions

		Product product = storeController.findProductById(productId);
		if (product == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		for (String name : details.keySet()) {
			String value = details.get(name);
			storeController.setProductDetail(product, name, value);
		}
		
		return Response.ok().entity(details).build();
	}

	@GET
	@Path("/tags")
	public Response listTags() {
		List<fi.foyt.fni.rest.entities.store.StoreTag> result = new ArrayList<>();

		List<StoreTag> storeTags = storeController.listTags();
		for (StoreTag storeTag : storeTags) {
			result.add(toEntity(storeTag));
		}

		return Response.ok(result).build();
	}

	@POST
	@Path("/details")
	public Response createDetail(StoreDetail detail) {
		// TODO: Validation checks
		// TODO: Permissions

		StoreDetail storeDetail = storeController.createStoreDetail(detail.getName());

		return Response.ok(toEntity(storeDetail)).build();
	}

	@GET
	@Path("/details")
	public Response listDetails() {
		List<fi.foyt.fni.rest.entities.store.StoreDetail> result = new ArrayList<>();

		List<StoreDetail> storeDetails = storeController.listStoreDetails();
		for (StoreDetail storeDetail : storeDetails) {
			result.add(toEntity(storeDetail));
		}

		return Response.ok(result).build();
	}

	private Response createBinaryResponse(final byte[] data, String contentType, String urlName) {
		ResponseBuilder responseBuilder = Response.ok(new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				if (data != null)
					output.write(data);
			}
		}, MediaType.valueOf(contentType));

		if (StringUtils.isNotBlank(urlName)) {
			responseBuilder.header("content-disposition", "attachment; filename=" + urlName);
		}

		return responseBuilder.build();
	}

	private String getBookFileDownloadUrl(FileProduct product) {
		return product.getFile() == null ? null : new StringBuilder().append("/store/products/").append(product.getId()).append("/files/")
				.append(product.getFile().getId()).toString();
	}

	private Locale getRequestLocale(HttpHeaders httpHeaders) {
		Locale locale = httpHeaders.getLanguage();
		if (locale == null) {
			locale = systemSettingsController.getDefaultLocale();
		}
		return locale;
	}
	
	private fi.foyt.fni.rest.entities.store.Product toEntity(fi.foyt.fni.persistence.model.store.Product product) {
		if (product instanceof fi.foyt.fni.persistence.model.store.BookProduct) {
			return toEntity((fi.foyt.fni.persistence.model.store.BookProduct) product);
		} else if (product instanceof fi.foyt.fni.persistence.model.store.PremiumAccountProduct) {
			return toEntity((fi.foyt.fni.persistence.model.store.PremiumAccountProduct) product);
		}
		
		return null;
	}
	
	private PremiumAccountProduct toEntity(fi.foyt.fni.persistence.model.store.PremiumAccountProduct product) {
		if (product == null) {
			return null;
		}
		
		List<String> tags = getProductTags(product);
		Map<String, String> details = storeController.getProductDetailMap(product);

		return new PremiumAccountProduct(product.getId(), product.getPublished(), "PREMIUM_ACCOUNT", product.getName().toMap(), product.getDescription().toMap(),
				product.getPrice(), toEntity(product.getDefaultImage()), product.getModified(), product.getCreated(), toEntity(product.getCreator()),
				toEntity(product.getModifier()), product.getRequiresDelivery(), tags, details, product.getMonths());
	}

	private BookProduct toEntity(fi.foyt.fni.persistence.model.store.BookProduct product) {
		if (product == null) {
			return null;
		}
		
		List<String> tags = getProductTags(product);
		Map<String, String> details = storeController.getProductDetailMap(product);

		return new BookProduct(product.getId(), product.getPublished(), "BOOK", product.getName().toMap(), product.getDescription().toMap(), product.getPrice(),
				toEntity(product.getDefaultImage()), product.getModified(), product.getCreated(), toEntity(product.getCreator()), toEntity(product.getModifier()),
				product.getRequiresDelivery(), tags, details, getBookFileDownloadUrl(product), product.getDownloadable());
	}

	private List<String> getProductTags(fi.foyt.fni.persistence.model.store.Product product) {
		List<String> tags = new ArrayList<>();

		for (StoreTag storeTag : storeController.listStoreTagsByProduct(product)) {
			tags.add(storeTag.getText());
		}
		return tags;
	}

	private fi.foyt.fni.rest.entities.store.ProductImage toEntity(ProductImage productImage) {
		if (productImage == null) {
			return null;
		}

		String downloadUrl = new StringBuilder().append("/store/products/").append(productImage.getProduct().getId()).append("/images/")
				.append(productImage.getId()).append("/content").toString();

		return new fi.foyt.fni.rest.entities.store.ProductImage(productImage.getId(), productImage.getModified(), productImage.getCreated(),
				toEntity(productImage.getCreator()), toEntity(productImage.getModifier()), downloadUrl);
	}

	private fi.foyt.fni.rest.entities.users.User toEntity(User user) {
		if (user == null) {
			return null;
		}

		return new fi.foyt.fni.rest.entities.users.User(user.getId(), user.getFirstName(), user.getLastName(), user.getNickname(), user.getRole().toString(),
				user.getRegistrationDate(), user.getLocale());
	}

	private fi.foyt.fni.rest.entities.store.StoreTag toEntity(StoreTag storeTag) {
		return new fi.foyt.fni.rest.entities.store.StoreTag(storeTag.getId(), storeTag.getText());
	}

	private fi.foyt.fni.rest.entities.store.StoreDetail toEntity(StoreDetail storeDetail) {
		return new fi.foyt.fni.rest.entities.store.StoreDetail(storeDetail.getId(), storeDetail.getName());
	}

}
