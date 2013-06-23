package fi.foyt.fni.chat;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.chat.UserChatCredentialsDAO;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.encryption.EncryptionUtils;

@RequestScoped
public class ChatController {
	
	@Inject
	private SystemSettingsController systemSettingsController;
	
	@Inject
	@DAO
	private UserChatCredentialsDAO userChatCredentialsDAO;

	public String getCredentialsUserJid(UserChatCredentials chatCredentials) throws GeneralSecurityException, IOException {
		String encryptSalt = systemSettingsController.getSetting("chat.security.encrypt.salt");
    String encryptPassPhrase = systemSettingsController.getSetting("chat.security.encrypt.passPhrase");
    int encryptIterations = systemSettingsController.getIntegerSetting("chat.security.encrypt.iterations");
		
    return EncryptionUtils.decryptDes(encryptPassPhrase, encryptSalt, encryptIterations, chatCredentials.getUserJid());
	}
	
	public String getCredentialsPassword(UserChatCredentials chatCredentials) throws GeneralSecurityException, IOException {
		String encryptSalt = systemSettingsController.getSetting("chat.security.encrypt.salt");
    String encryptPassPhrase = systemSettingsController.getSetting("chat.security.encrypt.passPhrase");
    int encryptIterations = systemSettingsController.getIntegerSetting("chat.security.encrypt.iterations");
		
    return EncryptionUtils.decryptDes(encryptPassPhrase, encryptSalt, encryptIterations, chatCredentials.getPassword());
	}

	public UserChatCredentials findUserChatCredentials(User user) {
		return userChatCredentialsDAO.findByUser(user);
	}
	
}
