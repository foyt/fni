package fi.foyt.fni.chat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

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

  public UserChatCredentials createUserChatCredentials(User user, String userJid, String password) throws UnsupportedEncodingException, GeneralSecurityException {
    return userChatCredentialsDAO.create(user, userJid, EncryptionUtils.enryptDes(
        systemSettingsController.getSetting(SystemSettingKey.CHAT_CREDENTIAL_PASSPHRASE),
        systemSettingsController.getSetting(SystemSettingKey.CHAT_CREDENTIAL_SALT),
        systemSettingsController.getIntegerSetting(SystemSettingKey.CHAT_CREDENTIAL_ITERATIONS),
        password));
  }

  public UserChatCredentials findUserChatCredentialsByUser(User user) {
    return userChatCredentialsDAO.findByUser(user);
  }
  
  public UserChatCredentials findUserChatCredentialsByUserJid(String userJid) {
    return userChatCredentialsDAO.findByUserJid(userJid);
  }

  public UserChatCredentials updateUserChatCredentialsUserJid(UserChatCredentials userChatCredentials, String userJid) {
    return userChatCredentialsDAO.updateUserJid(userChatCredentials, userJid);
  }

  public UserChatCredentials updateUserChatCredentialsPassword(UserChatCredentials userChatCredentials, String password) throws UnsupportedEncodingException, GeneralSecurityException {
    return userChatCredentialsDAO.updatePassword(userChatCredentials, EncryptionUtils.enryptDes(
        systemSettingsController.getSetting(SystemSettingKey.CHAT_CREDENTIAL_PASSPHRASE),
        systemSettingsController.getSetting(SystemSettingKey.CHAT_CREDENTIAL_SALT),
        systemSettingsController.getIntegerSetting(SystemSettingKey.CHAT_CREDENTIAL_ITERATIONS),
        password));
  }
  
  public String getUserJidByUser(User user) {
    UserChatCredentials credentials = findUserChatCredentialsByUser(user);
    if (credentials != null) {
      return credentials.getUserJid();
    }
    
    return null;
  }

  public String getPasswordByUser(User user) throws GeneralSecurityException, IOException {
    UserChatCredentials credentials = findUserChatCredentialsByUser(user);
    if (credentials != null) {
      return EncryptionUtils.decryptDes(
          systemSettingsController.getSetting(SystemSettingKey.CHAT_CREDENTIAL_PASSPHRASE),
          systemSettingsController.getSetting(SystemSettingKey.CHAT_CREDENTIAL_SALT),
          systemSettingsController.getIntegerSetting(SystemSettingKey.CHAT_CREDENTIAL_ITERATIONS),
          credentials.getPassword());
    }
    
    return null;
  }

  public List<String> getAllUserJids() {
    List<String> result = new ArrayList<>();
    
    for (UserChatCredentials credentials : userChatCredentialsDAO.listAll()) {
      result.add(credentials.getUserJid());
    }
    
    return result;
  }

}
