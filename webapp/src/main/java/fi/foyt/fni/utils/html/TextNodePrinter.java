package fi.foyt.fni.utils.html;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TextNodePrinter implements NodePrinter {

  @Override
  public void printNode(PrintingContext printingContext, Node node) throws IOException {
    Text textNode = (Text) node;
    String text = textNode.getNodeValue();
    if (text != null && text.length() > 0) {
      printingContext.getContentWriter().append(StringEscapeUtils.escapeXml(text));
    }
  }
}
