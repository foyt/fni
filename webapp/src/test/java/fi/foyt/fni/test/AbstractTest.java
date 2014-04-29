package fi.foyt.fni.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;

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
    executeSql("delete from Address where user_id = ?", userId);
    executeSql("delete from UserToken where userIdentifier_id in (select id from UserIdentifier where user_id = ?)", userId);
    executeSql("delete from UserIdentifier where user_id = ?", userId);
    executeSql("delete from InternalAuth where user_id = ?", userId);
    executeSql("delete from UserEmail where user_id = ?", userId);
    executeSql("delete from User where id = ?", userId);
  }
  
}
