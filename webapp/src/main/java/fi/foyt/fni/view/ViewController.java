package fi.foyt.fni.view;

public interface ViewController {

	public abstract boolean checkPermissions(ViewControllerContext context);

  public abstract void execute(ViewControllerContext context);

}
