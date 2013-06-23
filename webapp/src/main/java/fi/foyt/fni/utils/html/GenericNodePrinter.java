package fi.foyt.fni.utils.html;
import java.io.IOException;
import java.io.Writer;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GenericNodePrinter implements NodePrinter {
  
  public void printNode(PrintingContext printingContext, Node node) throws IOException {
    Element element = (Element) node;
    String tagName = element.getLocalName().toLowerCase().replaceAll("[ \t\r]*", "");
    
    Writer writer = printingContext.getContentWriter();
    
    writer.append('<')
      .append(tagName);

    NamedNodeMap attrs = element.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      printAttribute(element, attrs.item(i), writer);
    }

    NodeList children = element.getChildNodes();
    if (children.getLength() == 0) {
      writer.append("/>");
    }
    else {
      writer.append('>');
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        NodePrinter nodePrinter = printingContext.getDocumentPrinter().getNodePrinter(child);
        if (nodePrinter != null) {
          nodePrinter.printNode(printingContext, child);
        }
      }
      writer.append("</");
      writer.append(tagName);
      writer.append('>');
    }
  }

  private void printAttribute(Element element, Node attribute, Writer writer) throws IOException {
    String attrName = attribute.getNodeName().toLowerCase();
    String attrValue = attribute.getNodeValue();
    if (attrValue != null) {
      writer.append(' ');
      writer.append(attrName);
      writer.append("=\"");
      writer.append(attrValue);
      writer.append("\"");
    }
  }
}
