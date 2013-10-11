package fi.foyt.fni.view.forge.old;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;

import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;

//@RequestScoped
//@Stateful
public class UploadMaterialsDialogViewController extends AbstractViewController {

  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return true;
  }

  @Override
  public void execute(ViewControllerContext context) {
    context.setIncludeJSP("/jsp/forge/uploadmaterialsdialog.jsp");
  }

}