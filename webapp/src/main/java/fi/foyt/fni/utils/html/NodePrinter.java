package fi.foyt.fni.utils.html;

import java.io.IOException;

import org.w3c.dom.Node;

public interface NodePrinter {
  public void printNode(PrintingContext printingContext, Node node) throws IOException;
}
