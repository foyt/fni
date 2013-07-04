package fi.foyt.fni.utils.html;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

public class HtmlUtils {

	public static String htmlToPlainText(String htmlText) {
    return StringEscapeUtils.unescapeHtml4(htmlText.replaceAll("\\<.*?>",""));
  }

  public static Document tidyToDOM(String htmlData) throws IOException {
    // As good as Tidy is, it seems to have occasional problems parsing script and style
    // tags into a DOM so let's just regexp them out of the way first

    htmlData = htmlData.replaceAll("(?is)<s(cript|tyle).*?>.*?</s(cript|tyle).*?>", "");

    // Let Tidy work its magic to get a decent DOM model of the data

    Tidy tidy = new Tidy();

    // tidy.setAltText(""); // default text for alt attribute.
    tidy.setAsciiChars(true); // convert quotes and dashes to nearest ASCII char.
    tidy.setBreakBeforeBR(false); // output newline before <br>.
    tidy.setBurstSlides(false); // split- create slides on each h2 element.
    // tidy.setDocType(""); // user specified doctype.
    tidy.setDropEmptyParas(false); // discard empty p elements.
    tidy.setDropFontTags(true); // discard presentation tags.
    tidy.setDropProprietaryAttributes(false); // discard proprietary attributes.
    tidy.setEmacs(false); // if true format error output for GNU Emacs.
    tidy.setEncloseBlockText(false); // if true text in blocks is wrapped in <p>'s.
    tidy.setEncloseText(false); // if true text at body is wrapped in <p>'s.
    tidy.setEscapeCdata(false); // replace CDATA sections with escaped text.
    tidy.setFixBackslash(true); // fix URLs by replacing \ with /.
    tidy.setFixComments(true); // fix-bad-comments- fix comments with adjacent hyphens.
    tidy.setFixUri(true); // fix uri references applying URI encoding if necessary.
    tidy.setForceOutput(true); // output document even if errors were found.
    tidy.setHideComments(true); // hides all (real) comments in output.
    tidy.setHideEndTags(false); // suppress optional end tags.
    tidy.setIndentAttributes(false); // newline+indent before each attribute.
    tidy.setIndentCdata(false); // indent CDATA sections.
    tidy.setIndentContent(false); // indent content of appropriate tags.
    tidy.setJoinClasses(true); // join multiple class attributes.
    tidy.setJoinStyles(true); // join multiple style attributes.
    // tidy.setKeepFileTimes() // if true last modified time is preserved.
    tidy.setLiteralAttribs(false); // if true attributes may use newlines.
    tidy.setLogicalEmphasis(false); // replace i by em and b by strong.
    tidy.setLowerLiterals(true);// folds known attribute values to lower case.
    tidy.setMakeBare(false); // remove Microsoft cruft.
    tidy.setMakeClean(false); // remove presentational clutter
    tidy.setNumEntities(true); // output entities other than the built-in HTML entities in the numeric rather than the named entity form.
    tidy.setQuoteAmpersand(false); // output naked ampersand as &.
    tidy.setQuoteMarks(true); // output " marks as &quot;.
    tidy.setQuoteNbsp(true); // output non-breaking space as entity.
    // tidy.setRepeatedAttributes(); // keep first or last duplicate attribute.
    tidy.setReplaceColor(false); // replace hex color attribute values with names.
    tidy.setTrimEmptyElements(false); // trim empty elements.
    tidy.setWord2000(true); // draconian cleaning for Word2000.
    // tidy.setWrapAsp() // wrap within ASP pseudo elements.
    // tidy.setWrapAttVals() // wrap within attribute values.
    // tidy.setWrapJste() // wrap within JSTE pseudo elements.
    // tidy.setWraplen() // default wrap margin.
    // tidy.setWrapPhp() // wrap within PHP pseudo elements.
    // tidy.setWrapScriptlets() // wrap within JavaScript string literals.
    // tidy.setWrapSection() // wrap within <!
    tidy.setXmlTags(false); // tidy should treat input as XML
    tidy.setXHTML(true); // output extensible HTML.
    tidy.setQuiet(true); // don't output summary, warnings or errors
    tidy.setShowWarnings(false); // show-warnings
    tidy.setPrintBodyOnly(true); // output BODY content only.
    tidy.setInputEncoding("UTF-8"); // input encoding
    tidy.setOutputEncoding("UTF-8"); // input encoding

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    tidy.parse(new ByteArrayInputStream(htmlData.getBytes("UTF-8")), baos);
    baos.flush();
    baos.close();

    return tidy.parseDOM(new ByteArrayInputStream(baos.toByteArray()), null);
  }

  public static String printDocument(Document document) throws IOException {
    return printDocument(document, new DocumentPrinter());
  }

  public static String printDocument(Document document, DocumentPrinter documentPrinter) throws IOException {
    StringWriter output = new StringWriter();

    PrintingContext printingContext = new PrintingContext(documentPrinter, output);

    DocumentType documentType = document.getDoctype();
    if (documentType != null) {
    	printDocumentType(printingContext, documentType);
    }

    NodePrinter nodePrinter = documentPrinter.getNodePrinter(document.getDocumentElement());
    if (nodePrinter != null) {
      nodePrinter.printNode(printingContext, document.getDocumentElement());
    }

    return output.toString();
  }

  public static String printDocumentContent(Document document) throws IOException, TransformerException {
    return printDocumentContent(document, new DocumentPrinter());
  }

  public static String printDocumentContent(Document document, DocumentPrinter documentPrinter) throws IOException, TransformerException {
    StringWriter output = new StringWriter();
    PrintingContext printingContext = new PrintingContext(documentPrinter, output);
   
    Node bodyElement = document.getDocumentElement().getElementsByTagName("body").item(0);
    NodeList children = bodyElement.getChildNodes();

    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);

      NodePrinter nodePrinter = documentPrinter.getNodePrinter(child);
      if (nodePrinter != null) {
        nodePrinter.printNode(printingContext, child);
      }
    }

    return output.toString();
  }
  
  public static boolean containsMicrosoftCruft(String data) {
    // TODO: Implement
    return false;
  }
  
  public static String getAsHtmlText(String title, String bodyContent) {
    StringBuilder htmlBuilder = new StringBuilder();
    htmlBuilder.append("<!DOCTYPE html>");
    htmlBuilder.append("<html>");
    htmlBuilder.append("<head>");
    htmlBuilder.append("<meta charset=\"UTF-8\">");
    
    if (StringUtils.isNotBlank(title)) {
      htmlBuilder.append("<title>");
      htmlBuilder.append(StringEscapeUtils.escapeHtml4(title));
      htmlBuilder.append("</title>");
    }
    
    htmlBuilder.append("</head>");
    htmlBuilder.append("<body>");
    htmlBuilder.append(bodyContent);
    htmlBuilder.append("</body>");
    htmlBuilder.append("</html>");
    
    return htmlBuilder.toString();
  }
  
  private static void printDocumentType(PrintingContext printingContext, DocumentType documentType) throws IOException {
  	Writer contentWriter = printingContext.getContentWriter();
  	contentWriter.append("<!DOCTYPE ");
  	contentWriter.append(documentType.getName());
  	
  	String publicId = documentType.getPublicId();
  	String systemId = documentType.getSystemId();
  	
  	if (StringUtils.isNotBlank(publicId) && StringUtils.isNotBlank(systemId)) {
  		contentWriter.append(" PUBLIC '");
  		contentWriter.append(publicId);
  		contentWriter.append("' '");
  		contentWriter.append(systemId);
  		contentWriter.append("'");
  	} else if (StringUtils.isNotBlank(systemId)) {
  		contentWriter.append(" SYSTEM '");
  		contentWriter.append(systemId);
  		contentWriter.append("'");
  	}
  	
    String internalSubset = documentType.getInternalSubset();
    if (internalSubset != null) {
    	contentWriter.append(" [");
    	contentWriter.append(internalSubset);
      contentWriter.append(']');
    }
    
    contentWriter.append('>');
  }
}
