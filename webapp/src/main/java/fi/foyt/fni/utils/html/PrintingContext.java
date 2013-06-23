package fi.foyt.fni.utils.html;

import java.io.Writer;

public class PrintingContext {
  
  public PrintingContext(DocumentPrinter documentPrinter, Writer contentWriter) {
    this.contentWriter = contentWriter;
    this.documentPrinter = documentPrinter;
  }

  public Writer getContentWriter() {
    return contentWriter;
  }
  
  public DocumentPrinter getDocumentPrinter() {
    return documentPrinter;
  }
  
  private Writer contentWriter;
  private DocumentPrinter documentPrinter;
}
