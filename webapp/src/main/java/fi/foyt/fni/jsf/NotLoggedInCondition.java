package fi.foyt.fni.jsf;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ocpsoft.rewrite.config.DefaultConditionBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

import fi.foyt.fni.session.SessionController;

@ApplicationScoped
public class NotLoggedInCondition extends DefaultConditionBuilder {

  @Inject
  private SessionController sessionController;

  @Override
  public boolean evaluate(Rewrite event, EvaluationContext context) {
    return !sessionController.isLoggedIn();
  }
}