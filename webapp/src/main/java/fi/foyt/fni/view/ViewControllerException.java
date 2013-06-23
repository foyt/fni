package fi.foyt.fni.view;

public class ViewControllerException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ViewControllerException(String message) {
    super(message);
  }
  
  public ViewControllerException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
