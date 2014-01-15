package fi.foyt.fni.chat;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.chat.UserChatCredentialsDAO;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.encryption.EncryptionUtils;

@Dependent
@Stateless
public class ChatCredentialsController {

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private UserChatCredentialsDAO userChatCredentialsDAO;
  
  public String getUserJidByUser(User user) throws GeneralSecurityException, IOException {
    UserChatCredentials credentials = userChatCredentialsDAO.findByUser(user);
    if (credentials != null) {
      return EncryptionUtils.decryptDes(
          systemSettingsController.getSetting(SystemSettingKey.CHAT_CREDENTIAL_PASSPHRASE),
          systemSettingsController.getSetting(SystemSettingKey.CHAT_CREDENTIAL_SALT),
          systemSettingsController.getIntegerSetting(SystemSettingKey.CHAT_CREDENTIAL_ITERATIONS),
          credentials.getUserJid());
    }
    
    return null;
  }

  public String getPasswordByUser(User user) throws GeneralSecurityException, IOException {
    UserChatCredentials credentials = userChatCredentialsDAO.findByUser(user);
    if (credentials != null) {
      return EncryptionUtils.decryptDes(
          systemSettingsController.getSetting(SystemSettingKey.CHAT_CREDENTIAL_PASSPHRASE),
          systemSettingsController.getSetting(SystemSettingKey.CHAT_CREDENTIAL_SALT),
          systemSettingsController.getIntegerSetting(SystemSettingKey.CHAT_CREDENTIAL_ITERATIONS),
          credentials.getPassword());
    }
    
    return null;
  }
  

}
