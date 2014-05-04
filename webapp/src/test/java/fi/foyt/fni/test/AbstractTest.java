package fi.foyt.fni.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class AbstractTest {
  
  protected String getAppUrl() {
    return getAppUrl(false); 
  }
  
  protected String getAppUrl(boolean secure) {
    return (secure ? "https://" : "http://") + getHost() + ':' + (secure ? getPortHttps() : getPortHttp()) + '/' + getCtxPath();
  }

  protected String getHost() {
    return System.getProperty("it.host");
  }

  protected int getPortHttp() {
    return Integer.parseInt(System.getProperty("it.port.http"));
  }

  protected int getPortHttps() {
    return Integer.parseInt(System.getProperty("it.port.https"));
  }

  protected String getCtxPath() {
    return System.getProperty("it.ctx");
  }
  
  protected String getGoogleApiKey() {
    return System.getProperty("it.google.apiKey");
  }
  
  protected String getGoogleApiSecret() {
    return System.getProperty("it.google.apiSecret");
  }
  
  protected String getGoogleUsername() {
    return System.getProperty("it.google.username");
  }
  
  protected String getGooglePassword() {
    return System.getProperty("it.google.password");
  }
  
  protected String getFacebookApiKey() {
    return System.getProperty("it.facebook.apiKey");
  }
  
  protected String getFacebookApiSecret() {
    return System.getProperty("it.facebook.apiSecret");
  }
  
  protected String getFacebookUsername() {
    return System.getProperty("it.facebook.username");
  }
  
  protected String getFacebookPassword() {
    return System.getProperty("it.facebook.password");
  }
  
  protected String getDropboxApiKey() {
    return System.getProperty("it.dropbox.apiKey");
  }
  
  protected String getDropboxApiSecret() {
    return System.getProperty("it.dropbox.apiSecret");
  }
  
  protected String getDropboxUsername() {
    return System.getProperty("it.dropbox.username");
  }
  
  protected String getDropboxPassword() {
    return System.getProperty("it.dropbox.password");
  }
  
  protected Connection getConnection() throws Exception {
    String username = System.getProperty("it.jdbc.username");
    String password = System.getProperty("it.jdbc.password");
    String url = System.getProperty("it.jdbc.url");
    Class.forName(System.getProperty("it.jdbc.driver")).newInstance();
    
    return DriverManager.getConnection(url, username, password);
  }
  
  protected void executeSql(String sql, Object... params) throws Exception {
    Connection connection = getConnection();
    try {
      connection.setAutoCommit(true);
      PreparedStatement statement = connection.prepareStatement(sql);
      try {
        for (int i = 0, l = params.length; i < l; i++) {
          statement.setObject(i + 1, params[i]);
        }
        
        statement.execute();
      } finally {
        statement.close();
      }
    } finally {
      connection.close();
    }
  }
  
  protected void createUser(Long userId, String firstName, String lastName, String email, String password, String locale, String profileImageSource, String role) throws Exception {
    createUser(userId, firstName, lastName, email, password, locale, profileImageSource, role, true);
  }  
  
  protected void createUser(Long userId, String firstName, String lastName, String email, String password, String locale, String profileImageSource, String role, boolean verified) throws Exception {
    executeSql(
      "insert into " +
      "  User (id, archived, firstName, lastName, locale, profileImageSource, registrationDate, role) " +
      "values " +
      "  (?, ?, ?, ?, ?, ?, ?, ?)", userId, false, firstName, lastName, locale, profileImageSource, new Date(), role);
    
    executeSql(
      "insert into " +
      "  UserEmail (id, email, primaryEmail, user_id) " +
      "values " +
      "  (?, ?, ?, ?)", userId, email, true, userId);
    
    executeSql(
      "insert into " +
      "  InternalAuth (id, password, verified, user_id) " +
      "values " +
      "  (?, ?, ?, ?)", userId, DigestUtils.md5Hex(password), verified, userId);
  }
  
  protected void deleteUser(Long userId) throws Exception {
    executeSql("delete from DropboxFile where id in (select id from Material where creator_id = ?)", userId);
    executeSql("delete from Material where creator_id = ? and type = 'DROPBOX_FILE'", userId);
    executeSql("delete from GoogleDocument where id in (select id from Material where creator_id = ?)", userId);
    executeSql("delete from Material where creator_id = ? and type = 'GOOGLE_DOCUMENT'", userId);
    executeSql("delete from DropboxFolder where id in (select id from Material where creator_id = ?)", userId);
    executeSql("delete from DropboxRootFolder where id in (select id from Material where creator_id = ?)", userId);
    executeSql("delete from Folder where id in (select id from Material where creator_id = ?)", userId);
    executeSql("delete from Material where creator_id = ? and type = 'FOLDER'", userId);
    executeSql("delete from Material where creator_id = ?", userId);
    executeSql("delete from Address where user_id = ?", userId);
    executeSql("delete from UserToken where userIdentifier_id in (select id from UserIdentifier where user_id = ?)", userId);
    executeSql("delete from UserIdentifier where user_id = ?", userId);
    executeSql("delete from InternalAuth where user_id = ?", userId);
    executeSql("delete from UserEmail where user_id = ?", userId);
    executeSql("delete from User where id = ?", userId);
  }

  protected void createOAuthSettings() throws Exception {
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "FACEBOOK_APIKEY", getFacebookApiKey());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "FACEBOOK_APISECRET", getFacebookApiSecret());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "FACEBOOK_CALLBACKURL", getAppUrl() + "/login?return=1&loginMethod=FACEBOOK");
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "GOOGLE_APIKEY", getGoogleApiKey());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "GOOGLE_APISECRET", getGoogleApiSecret());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "GOOGLE_CALLBACKURL", getAppUrl() + "/login?return=1&loginMethod=GOOGLE");
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "DROPBOX_ROOT", "sandbox");
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "DROPBOX_APIKEY", getDropboxApiKey());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "DROPBOX_APISECRET", getDropboxApiSecret());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "DROPBOX_CALLBACKURL", getAppUrl() + "/login?return=1&loginMethod=DROPBOX");
  }
  
  protected void purgeOAuthSettings() throws Exception {
    executeSql("delete from SystemSetting where settingKey in ('FACEBOOK_APIKEY', 'FACEBOOK_APISECRET', 'FACEBOOK_CALLBACKURL', 'GOOGLE_APIKEY', 'GOOGLE_APISECRET', 'GOOGLE_CALLBACKURL', 'DROPBOX_ROOT', 'DROPBOX_APIKEY', 'DROPBOX_APISECRET', 'DROPBOX_CALLBACKURL')");
    deleteOAuthUsers();
  }
  
  protected void deleteOAuthUsers() throws Exception {
    List<Long> userIds = new ArrayList<>();
    
    Connection connection = getConnection();
    try {
      connection.setAutoCommit(true);
      PreparedStatement statement = connection.prepareStatement("select user_id from UserIdentifier where authSource in ('GOOGLE', 'FACEBOOK')");
      try {
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          Long userId = resultSet.getLong(1);
          userIds.add(userId);
        }
      } finally {
        statement.close();
      }
    } finally {
      connection.close();
    }
    
    for (Long userId : userIds) {
      deleteUser(userId);
    }
  }
  
}
