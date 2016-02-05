package fi.foyt.fni.illusion;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.mail.MessagingException;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.exceptions.JadeException;
import fi.foyt.fni.jade.JadeController;
import fi.foyt.fni.mail.Mailer;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;

public class IllusionMailer {

  @Inject
  private Logger logger;

  @Inject
  private IllusionJadeTemplateLoader templateLoader;
  
  @Inject
  private JadeController jadeController;

  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private UserController userController;
  
  @Inject
  private Mailer mailer;
  
  public void sendMail(IllusionEventParticipant recipient, String subject, String template, Map<String, Object> templateModel) {
    User user = recipient.getUser();
    String toName = user.getFullName();
    String toMail = userController.getUserPrimaryEmail(user);
    
    sendMail(recipient.getEvent(), subject, toMail, toName, template, templateModel);
  }
  
  public void sendMail(IllusionEvent event, String subject, String toMail, String toName, String template, Map<String, Object> templateModel) {
    String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
    sendMail(event, subject, fromMail, fromName, toMail, toName, template, templateModel);
  }
  
  public void sendMail(IllusionEvent event, String subject, String fromMail, String fromName, String toMail, String toName, String template, Map<String, Object> templateModel) {
    JadeConfiguration jadeConfiguration = new JadeConfiguration();
    jadeConfiguration.setTemplateLoader(templateLoader);
    jadeConfiguration.setCaching(false);
    
    String content = null;
    try {
      content = jadeController.renderTemplate(jadeConfiguration, String.format("%s/%s", event.getUrlName(), template), templateModel);
    } catch (JadeException | IOException e) {
      logger.log(Level.SEVERE, "Failed to render Jade template", e);
      return;
    }
    
    try {
      mailer.sendMail(fromMail, fromName, toMail, toName, subject, content, "text/html");
    } catch (MessagingException e) {
      logger.log(Level.SEVERE, "Failed to send email", e);
      return;
    }
  }
  
}
