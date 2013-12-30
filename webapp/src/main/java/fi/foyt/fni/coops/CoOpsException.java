package fi.foyt.fni.coops;

public class CoOpsException extends Exception {

  private static final long serialVersionUID = 1L;

  public CoOpsException() {
    super();
  }
  
  public CoOpsException(String message) {
    super(message);
  }

  public CoOpsException(Throwable cause) {
    super(cause);
  }
  
}
