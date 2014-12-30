package fi.foyt.fni.auth;

public class ExternalLoginFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ExternalLoginFailedException() {
	  super();
  }
	
  public ExternalLoginFailedException(Throwable cause) {
    super(cause);
  }

}
