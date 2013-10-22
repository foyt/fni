package fi.foyt.fni.materials;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

import fi.foyt.fni.persistence.dao.materials.DocumentDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentRevisionDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.DocumentRevision;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.html.HtmlUtils;
import fi.foyt.fni.utils.itext.B64ImgReplacedElementFactory;
import fi.foyt.fni.utils.servlet.RequestUtils;

@Dependent
@Stateful
public class DocumentController {
	
	@Inject
	private Logger logger;

	@Inject
	private MaterialController materialController;

	@Inject
	private MaterialPermissionController materialPermissionController;
	
  @Inject
  private DocumentDAO documentDAO;

  @Inject
  private DocumentRevisionDAO documentRevisionDAO;
  
  /* Document */

	public Document createDocument(Folder parentFolder, String urlName, String title, User creator) {
		return createDocument(parentFolder, urlName, title, null, null, creator);
	}

	public Document createDocument(Folder parentFolder, String urlName, String title, String data, Language language, User creator) {
		return documentDAO.create(creator, language, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
	}
	
	public Document findDocumentById(Long documentId) {
		return documentDAO.findById(documentId);
	}

	public Document updateDocumentData(Document document, String data, User user) {
		return documentDAO.updateData(document, user, data);
	}
	
	/* Revisions */
	
	public DocumentRevision createDocumentRevision(Document document, Long revisionNumber, Date created, boolean compressed, boolean completeVersion, byte[] revisionBytes, String checksum) {
	  return documentRevisionDAO.create(document, revisionNumber, created, compressed, completeVersion, revisionBytes, checksum, null, null);
	}

	public List<DocumentRevision> listDocumentRevisionsAfter(Document document, Long revisionNumber) {
		List<DocumentRevision> documentRevisions = documentRevisionDAO.listByDocumentAndRevisionGreaterThan(document, revisionNumber);
    Collections.sort(documentRevisions, new Comparator<DocumentRevision>() {
      @Override
      public int compare(DocumentRevision documentRevision1, DocumentRevision documentRevision2) {
        return documentRevision1.getRevision().compareTo(documentRevision2.getRevision());
      }
    });
    
    return documentRevisions;
	}

	public Long getDocumentRevision(Document document) {
		Long result = documentRevisionDAO.maxRevisionByDocument(document);
		if (result == null) {
			result = 0l;
		}
		
		return result;
	}
	
	/* PDF */

  public TypedData printDocumentAsPdf(String contextPath, String baseUrl, User user, Document document) throws DocumentException, IOException, ParserConfigurationException, SAXException {
	  ITextRenderer renderer = new ITextRenderer();
	  ReplacedElementFactory replacedElementFactory = new B64ImgReplacedElementFactory();
	  renderer.getSharedContext().setReplacedElementFactory(replacedElementFactory);
	  
	  String documentContent = new String(document.getData(), "UTF-8");
	  org.w3c.dom.Document domDocument = tidyForPdf(document.getTitle(), documentContent);
	  
	  try {
  	  NodeList imageList = XPathAPI.selectNodeList(domDocument, "//img");
  	  for (int i = 0, l = imageList.getLength(); i < l; i++) {
  	  	Element imageElement = (Element) imageList.item(i);
  	  	String src = imageElement.getAttribute("src");
  	  	
  	  	try {
  	  		boolean internal = false;
  	  		
  	  		if (src.startsWith("http://") || src.startsWith("https://")) {
  	  			if (src.startsWith(baseUrl)) {
  	  			  src = RequestUtils.stripCtxPath(contextPath, src.substring(baseUrl.length()));	
  	  			  internal = true;
  	  			} else {
  	  				internal = false;
  	  			}
  	  		} else {
  	  			src = RequestUtils.stripCtxPath(contextPath, src);
  	  			internal = true;
  	  		}
  	  		
  	  		if (internal) {
  	  			Material material = materialController.findMaterialByCompletePath(src);
  	  			if (materialPermissionController.hasAccessPermission(user, material)) {
    	  			if (material.getType() == MaterialType.IMAGE) {
    	  				Image image = (Image) material;

    	  				StringBuilder srcBuilder = new StringBuilder()
        	  			.append("data:")
        	  			.append(image.getContentType())
        	  		  .append(";base64,")
        	  		  .append(new String(Base64.encodeBase64(image.getData())));
    	  				
    	  				imageElement.setAttribute("src", srcBuilder.toString());
    	  			}
  	  			}
  	  		}
    	  	
  	  	} catch (Exception e) {
  	  		// If anything goes wrong we just leave this img "as is".
  	  	}
  	  }
  	} catch (Exception e) {
  		// If anything goes wrong we just leave the document "as is".
  	}
	  
	  ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
	  renderer.setDocument(domDocument, baseUrl);
	  renderer.layout();
	  renderer.createPDF(pdfStream);
	  pdfStream.flush();
	  pdfStream.close();
	  
	  return new TypedData(pdfStream.toByteArray(), "application/pdf");
  }
	
  private org.w3c.dom.Document tidyForPdf(String title, String bodyContent) throws ParserConfigurationException, IOException, SAXException {
  	String documentHtml = HtmlUtils.getAsHtmlText(title, bodyContent);
  	String cleanedHtml = null;
  	
  	ByteArrayOutputStream tidyStream = new ByteArrayOutputStream();
  	try {
    	Tidy tidy = new Tidy();
      tidy.setInputEncoding("UTF-8");
      tidy.setOutputEncoding("UTF-8");
      tidy.setShowWarnings(true);
      tidy.setNumEntities(false);
      tidy.setXmlOut(true);
      tidy.setXHTML(true);

      cleanedHtml = HtmlUtils.printDocument(tidy.parseDOM(new StringReader(documentHtml), null));
  	} catch (Exception e) {
  		throw e;
  	} finally {
      tidyStream.flush();
      tidyStream.close();
  	}
  	
    InputStream documentStream = new ByteArrayInputStream(cleanedHtml.getBytes("UTF-8"));
  	try {

      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      builderFactory.setNamespaceAware(false);
      builderFactory.setValidating(false);
      builderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
      builderFactory.setFeature("http://xml.org/sax/features/validation", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      DocumentBuilder builder = builderFactory.newDocumentBuilder();
  
      return builder.parse(documentStream);

  	} finally {
  		documentStream.close();
  	}
	}

}
