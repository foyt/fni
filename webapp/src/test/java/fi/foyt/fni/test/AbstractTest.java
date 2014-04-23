package fi.foyt.fni.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public abstract class AbstractTest {
  
  protected String getAppUrl() {
    return "http://" + getHost() + ':' + getPort() + '/' + getCtxPath();
  }

  protected String getHost() {
    return System.getProperty("it.host");
  }

  protected int getPort() {
    return Integer.parseInt(System.getProperty("it.port"));
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

}
