package fi.foyt.fni.view;


public abstract class AbstractViewController implements ViewController {

  public abstract boolean checkPermissions(ViewControllerContext context);

  public abstract void execute(ViewControllerContext context);
}