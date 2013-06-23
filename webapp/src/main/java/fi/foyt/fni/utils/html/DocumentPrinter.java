package fi.foyt.fni.utils.html;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocumentPrinter {

  public DocumentPrinter() {
    this(defaultMap);
  }

  public DocumentPrinter(Map<String, NodePrinter> printerMap) {
    this.printerMap = printerMap;
  }

  public NodePrinter getNodePrinter(Node node) {
    if (node == null)
      return getDefaultNodePrinter();

    String tagName = node.getLocalName().toLowerCase();
    List<String> classNames = null;

    if (node instanceof Element) {
      Element element = (Element) node;
      String classes = element.getAttribute("class");
      if (!StringUtils.isBlank(classes)) {
        classNames = Arrays.asList(StringUtils.split(classes, ' '));
      }
    }

    for (String rule : printerMap.keySet()) {
      if ((rule.charAt(0) == '.') && (classNames != null)) {
        if (classNames.contains(rule.substring(1)))
          return printerMap.get(rule);
      }
    }

    NodePrinter printer = printerMap.get(tagName);
    if (printer != null)
      return printer;

    return getDefaultNodePrinter();
  }

  public NodePrinter getDefaultNodePrinter() {
    return printerMap.get("*");
  }

  private Map<String, NodePrinter> printerMap;
  private static Map<String, NodePrinter> defaultMap = new HashMap<String, NodePrinter>();

  static {
    defaultMap.put("*", new GenericNodePrinter());
    defaultMap.put("#text", new TextNodePrinter());
  }
}
