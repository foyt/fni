package fi.foyt.fni.test;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public abstract class AbstractTest {

  private static final long TEST_START_TIME = System.currentTimeMillis();

  @Rule
  public TestName testName = new TestName();
  
  @Before
  public void printName(){
    System.out.println(String.format("> %s", testName.getMethodName()));
  }
  
  @After
  public void flushCache() throws ClientProtocolException, IOException {
    given()
      .baseUri(getAppUrl() + "/rest")
      .header("Authorization", "Bearer systemtoken")
      .get("/system/jpa/cache/flush")
      .then()
      .statusCode(200);
  }

  
  protected void reindexHibernateSearch() {
    given()
      .baseUri(getAppUrl() + "/rest")
      .header("Authorization", "Bearer systemtoken")
      .get("/system/search/reindex")
      .then()
      .statusCode(200);
  }

  @Before
  public void sqlSetup() throws Exception {
    executeSqlStatements(getTestMethod(), true);
  }

  @After
  public void sqlTearDown() throws Exception {
    executeSqlStatements(getTestMethod(), false);
  }
  
  private void executeSqlStatements(Method method, boolean before) throws Exception {
    SqlSets sqlSets = method.getAnnotation(SqlSets.class);
    if (sqlSets == null) {
      return;
    }
    
    List<TestSql> testSqls = new ArrayList<>();
    Class<?> testClass = method.getDeclaringClass();
    
    if (sqlSets.value() != null) {
      String[] sqlSetIds = sqlSets.value();
      
      if (!before) {
        ArrayUtils.reverse(sqlSetIds);
      }
      
      for (String sqlSetId : sqlSetIds) {
        DefinedSqlSet definedSqlSet = getDefinedSqlSet(method.getDeclaringClass(), sqlSetId);
        if (definedSqlSet == null) {
          throw new RuntimeException("Could not find sqlset " + sqlSetId);
        }
        
        Map<String, String> paramMap = definedSqlSet.getParams();
        if (before) {
          if (definedSqlSet.getBefore() != null) {
            for (String sqlFile : definedSqlSet.getBefore()) {
              testSqls.add(new TestSql(sqlFile, paramMap));
            }
          }
        } else {
          if (definedSqlSet.getAfter() != null) {
            for (String sqlFile : definedSqlSet.getAfter()) {
              testSqls.add(new TestSql(sqlFile, paramMap));
            }
          }
        }
      }
    }
    
    fi.foyt.fni.test.SqlSet[] methodSets = sqlSets.sets();
    if (methodSets != null) {
      
      if (!before) {
        ArrayUtils.reverse(methodSets);
      }
      
      for (fi.foyt.fni.test.SqlSet methodSet : methodSets) {
        SqlParam[] methodSetParams = methodSet.params();
        String sqlSetId = methodSet.id();
        DefinedSqlSet definedSqlSet = getDefinedSqlSet(testClass, sqlSetId);
        String[] sqlFiles = null;
        Map<String, String> paramMap = definedSqlSet.getParams();
        if (methodSetParams != null) {
          for (SqlParam param : methodSetParams) {
            paramMap.put(param.name(), param.value());
          }
        }
        
        if (before) {
          sqlFiles = definedSqlSet.getBefore();
        } else {
          sqlFiles = definedSqlSet.getAfter();  
        }
        
        for (String sqlFile : sqlFiles) {
          testSqls.add(new TestSql(sqlFile, paramMap));
        }
      }
    } 
    
    runTestSqls(testSqls);
  }
  
  private void runTestSqls(List<TestSql> testSqls) throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    Pattern paramPattern = Pattern.compile("\\{([^\\|\\}]*)(\\|[^\\}]*)?}");
        
    for (TestSql testSql : testSqls) {
      InputStream sqlStream = classLoader.getResourceAsStream(testSql.getFile());
      try {
        String statements = IOUtils.toString(sqlStream);
        StringBuffer statementBuffer = new StringBuffer();
        
        Matcher matcher = paramPattern.matcher(statements);
        while (matcher.find()) {
          String group = matcher.group(1);
          String defaultValue = matcher.group(2);
          if (StringUtils.isNotBlank(defaultValue)) {
            defaultValue = defaultValue.substring(1);
          }
          
          String value = testSql.getParams().containsKey(group) ? testSql.getParams().get(group) : defaultValue;
          assertNotNull(String.format("%s: parameter %s in file %s does not have a value.", testName.getMethodName(), matcher.group(0), testSql.getFile()), value);
          matcher.appendReplacement(statementBuffer, value);
        }
        matcher.appendTail(statementBuffer);

        statements = statementBuffer.toString();
        
        if (StringUtils.isNotBlank(statements)) {
          // Tokenization regex from https://github.com/otavanopisto/muikku/blob/master/muikku-core-plugins/src/test/java/fi/muikku/plugins/workspace/test/ui/SeleniumTestBase.java
          for (String statement : statements.split(";(?=([^\']*\'[^\']*\')*[^\']*$)")) {
            String sql = StringUtils.trim(statement);
            if (StringUtils.isNotBlank(sql)) {
              executeSql(sql);
            }
          }
        }
      } finally {
        sqlStream.close();
      }
    }
  }
  
  private Method getTestMethod() throws NoSuchMethodException {
    String methodName = testName.getMethodName();
    int parameterizedIndex = methodName.indexOf("[");
    if (parameterizedIndex != -1) {
      methodName = methodName.substring(0, parameterizedIndex);
    }
    
    return getClass().getMethod(methodName, new Class<?>[] {});
  }

  @After
  public void dataClean() throws Exception {
    if ("true".equals(System.getProperty("it.verifyclean"))) {
      assertEquals(String.format("ForumTopicWatchers not properly cleaned on test %s", testName.getMethodName()), Integer.valueOf(0), countForumTopicWatchers());
      assertEquals(String.format("ForumMessages not properly cleaned on test %s", testName.getMethodName()), Integer.valueOf(0), countForumMessages());
    }
  }
  
  private DefinedSqlSet getDefinedSqlSet(Class<?> testClass, String id) {
    DefineSqlSets defineSqlSets = testClass.getAnnotation(DefineSqlSets.class);
    if (defineSqlSets == null) {
      defineSqlSets = testClass.getSuperclass().getAnnotation(DefineSqlSets.class);
    }
    
    if (defineSqlSets != null) {
      for (DefineSqlSet defineSqlSet : defineSqlSets.value()) {
        if (defineSqlSet.id().equals(id)) {
          Map<String, String> params = new HashMap<>();
          for (SqlParam sqlParam : defineSqlSet.params()) {
            params.put(sqlParam.name(), sqlParam.value());
          }
          
          return new DefinedSqlSet(defineSqlSet.before(), defineSqlSet.after(), params);
        }
      }
    }
    
    return null;
  }

  protected String getAppUrl() {
    return getAppUrl(false);
  }

  protected String getAppUrl(boolean secure) {
    String ctxPath = getCtxPath();
    return String.format("%s%s:%d%s", secure ? "https://" : "http://", getHost(), secure ? getPortHttps() : getPortHttp(), ctxPath != null ? "/" + ctxPath : "");
  }

  protected String getSeleniumVersion() {
    return System.getProperty("it.selenium.version");
  }
  
  protected String getPlatform() {
    return System.getProperty("it.platform");
  }

  protected String getBrowserVersion() {
    return System.getProperty("it.browser.version");
  }

  protected String getBrowser() {
    return System.getProperty("it.browser");
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

  protected int getSmtpPort() {
    return Integer.parseInt(System.getProperty("it.smtp.port"));
  }

  public String getProjectVersion() {
    return System.getProperty("it.project.version");
  }

  public static long getTestStartTime() {
    return TEST_START_TIME;
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

  protected String getSauceUsername() {
    return System.getProperty("it.sauce.username");
  }

  protected String getSauceAccessKey() {
    return System.getProperty("it.sauce.accessKey");
  }

  protected String getSauceTunnelId() {
    return System.getProperty("it.sauce.tunnelId");
  }
  
  protected File getTestPdf() {
    return new File(System.getProperty("it.files.pdf"));
  }

  protected File getTestPng() {
    return new File(System.getProperty("it.files.png"));
  }

  protected Connection getConnection() throws Exception {
    String username = System.getProperty("it.jdbc.username");
    String password = System.getProperty("it.jdbc.password");
    String url = System.getProperty("it.jdbc.url");
    Class.forName(System.getProperty("it.jdbc.driver")).newInstance();

    return DriverManager.getConnection(url, username, password);
  }
  
  protected void executeSqlFile(String file) throws IOException, Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream sqlStream = classLoader.getResourceAsStream(file);
    try {
      String statements = IOUtils.toString(sqlStream);
      if (StringUtils.isNotBlank(statements)) {
        // Tokenization regex from https://github.com/otavanopisto/muikku/blob/master/muikku-core-plugins/src/test/java/fi/muikku/plugins/workspace/test/ui/SeleniumTestBase.java
        for (String statement : statements.split(";(?=([^\']*\'[^\']*\')*[^\']*$)")) {
          String sql = StringUtils.trim(statement);
          if (StringUtils.isNotBlank(sql)) {
            executeSql(sql);
          }
        }
      }
    } finally {
      sqlStream.close();
    }
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
  
  protected void createUser(Long userId, String firstName, String lastName, String email, String password, String locale, String profileImageSource, String role)
      throws Exception {
    createUser(userId, firstName, lastName, email, password, locale, profileImageSource, role, true);
  }

  protected void createUser(Long userId, String firstName, String lastName, String email, String password, String role) throws Exception {
    createUser(userId, firstName, lastName, email, password, "en", "GRAVATAR", role, true);
  }

  protected void createUser(Long userId, String firstName, String lastName, String email, String password, String locale, String profileImageSource,
      String role, boolean verified) throws Exception {
    executeSql("insert into " + "  User (id, archived, firstName, lastName, locale, profileImageSource, registrationDate, role) " + "values "
        + "  (?, ?, ?, ?, ?, ?, ?, ?)", userId, false, firstName, lastName, locale, profileImageSource, new Date(), role);

    executeSql("insert into " + "  UserEmail (id, email, primaryEmail, user_id) " + "values " + "  (?, ?, ?, ?)", userId, email, true, userId);

    executeSql("insert into " + "  InternalAuth (id, password, verified, user_id) " + "values " + "  (?, ?, ?, ?)", userId, DigestUtils.md5Hex(password),
        verified, userId);
  }
  
  protected void deleteUser(String email) throws Exception {
    Long userId = findUserIdByEmail(email);
    assertNotNull(userId);
    deleteUser(userId);
  }

  protected void assertInternalAuthExists(String email, String password) throws Exception {
    Long userId = findUserIdByEmail(email);
    
    Connection connection = getConnection();
    try {
      connection.setAutoCommit(true);
      PreparedStatement statement = connection.prepareStatement("select id from InternalAuth where user_id = ? and password = ?");
      try {
        statement.setObject(1, userId);
        statement.setObject(2, DigestUtils.md5(password));        
        statement.execute();
        ResultSet resultSet = statement.getResultSet();
        assertNotNull(resultSet);
        assertTrue(resultSet.next());
        assertNotNull(resultSet.getLong(1));
      } finally {
        statement.close();
      }
    } finally {
      connection.close();
    }
  }
  
  private Long findUserIdByEmail(String email) throws Exception, SQLException {
    Connection connection = getConnection();
    try {
      connection.setAutoCommit(true);
      PreparedStatement statement = connection.prepareStatement("select user_id from UserEmail where email = ?");
      try {
        statement.setObject(1, email);
        statement.execute();
        ResultSet resultSet = statement.getResultSet();
        if (resultSet.next()) {
          return resultSet.getLong(1);
        }
      } finally {
        statement.close();
      }
    } finally {
      connection.close();
    }
    
    return null;
  }
  
  protected Integer countForums() throws Exception, SQLException {
    Connection connection = getConnection();
    try {
      connection.setAutoCommit(true);
      PreparedStatement statement = connection.prepareStatement("select count(id) from Forum");
      try {
        statement.execute();
        ResultSet resultSet = statement.getResultSet();
        if (resultSet.next()) {
          return resultSet.getInt(1);
        }
      } finally {
        statement.close();
      }
    } finally {
      connection.close();
    }
    
    return null;
  }
  
  protected Integer countForumTopicWatchers() throws Exception, SQLException {
    Connection connection = getConnection();
    try {
      connection.setAutoCommit(true);
      PreparedStatement statement = connection.prepareStatement("select count(id) from ForumTopicWatcher");
      try {
        statement.execute();
        ResultSet resultSet = statement.getResultSet();
        if (resultSet.next()) {
          return resultSet.getInt(1);
        }
      } finally {
        statement.close();
      }
    } finally {
      connection.close();
    }
    
    return null;
  }
  
  protected Integer countForumMessages() throws Exception, SQLException {
    Connection connection = getConnection();
    try {
      connection.setAutoCommit(true);
      PreparedStatement statement = connection.prepareStatement("select count(id) from ForumMessage");
      try {
        statement.execute();
        ResultSet resultSet = statement.getResultSet();
        if (resultSet.next()) {
          return resultSet.getInt(1);
        }
      } finally {
        statement.close();
      }
    } finally {
      connection.close();
    }
    
    return null;
  }

  protected void deleteUser(Long userId) throws Exception {
    executeSql("delete from DropboxFile where id in (select id from Material where creator_id = ?)", userId);
    executeSql("delete from Material where creator_id = ? and type = 'DROPBOX_FILE'", userId);
    executeSql("delete from GoogleDocument where id in (select id from Material where creator_id = ?)", userId);
    executeSql("delete from Material where creator_id = ? and type = 'GOOGLE_DOCUMENT'", userId);
    executeSql("delete from DropboxFolder where id in (select id from Material where creator_id = ?)", userId);
    executeSql("delete from DropboxRootFolder where id in (select id from Material where creator_id = ?)", userId);
    executeSql("delete from Folder where id in (select id from Material where creator_id = ?)", userId);
    executeSql("delete from MaterialView where material_id in (select id from Material where creator_id = ?)", userId);
    executeSql("delete from Material where creator_id = ? and type = 'FOLDER'", userId);
    executeSql("delete from Material where creator_id = ?", userId);
    executeSql("delete from Address where user_id = ?", userId);
    executeSql("delete from UserToken where userIdentifier_id in (select id from UserIdentifier where user_id = ?)", userId);
    executeSql("delete from UserIdentifier where user_id = ?", userId);
    executeSql("delete from InternalAuth where user_id = ?", userId);
    executeSql("delete from UserEmail where user_id = ?", userId);
    executeSql("delete from User where id = ?", userId);
  }

  protected void deleteIllusionEventByUrl(String urlName) throws Exception {
    executeSql("delete from ForumTopicWatcher where topic_id = (select forumTopic_id from IllusionEvent where urlName = ?)", urlName);
    executeSql("update ForumTopic set urlName = 'DELETE' where id = (select forumTopic_id from IllusionEvent where urlName = ?)", urlName);
    executeSql("delete from IllusionEventGenre where event_id = (select id from IllusionEvent where urlName = ?)", urlName);
    executeSql("delete from IllusionEventParticipant where event_id = (select id from IllusionEvent where urlName = ?)", urlName);
    executeSql("delete from IllusionEventSetting where event_id = (select id from IllusionEvent where urlName = ?)", urlName);
    executeSql("update Material set type = 'DELETE' where id in (select folder_id from IllusionEvent where urlName = ?) or parentFolder_id in (select folder_id from IllusionEvent where urlName = ?)", urlName, urlName);
    executeSql("delete from IllusionEvent where urlName = ?", urlName);
    executeSql("delete from ForumPost where topic_id in (select id from ForumTopic where urlName = 'DELETE')");
    executeSql("delete from ForumTopic where urlName = 'DELETE'");
    executeSql("delete from ForumMessage where id not in (select id from ForumPost union select id from ForumTopic)");
    executeSql("update Material set parentFolder_id = null where id in (select id from Material where type = 'DELETE')");
    executeSql("delete from IllusionEventDocument where id in (select id from Material where type = 'DELETE')");
    executeSql("delete from IllusionEventFolder where id in (select id from Material where type = 'DELETE')");
    executeSql("delete from Document where id in (select id from Material where type = 'DELETE')");
    executeSql("delete from Folder where id in (select id from Material where type = 'DELETE')");
    executeSql("delete from Material where type = 'DELETE'");
  }

  protected void deleteIllusionFolderByUser(String email) throws Exception {
    executeSql("delete from IllusionFolder where id = (select id from Material where type = 'ILLUSION_FOLDER' and creator_id = (select user_id from UserEmail where email = ?))", email);
    executeSql("delete from Folder where id = (select id from Material where type = 'ILLUSION_FOLDER' and creator_id = (select user_id from UserEmail where email = ?))", email);
    executeSql("delete from Material where type = 'ILLUSION_FOLDER' and creator_id = (select user_id from UserEmail where email = ?)", email);
  }
  
  protected void createOAuthSettings() throws Exception {
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "FACEBOOK_APIKEY",
        getFacebookApiKey());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "FACEBOOK_APISECRET",
        getFacebookApiSecret());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "FACEBOOK_CALLBACKURL",
        getAppUrl(true) + "/login/?return=1&loginMethod=FACEBOOK");
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "GOOGLE_APIKEY", getGoogleApiKey());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "GOOGLE_APISECRET",
        getGoogleApiSecret());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "GOOGLE_CALLBACKURL",
        getAppUrl(true) + "/login/?return=1&loginMethod=GOOGLE");
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "DROPBOX_ROOT", "sandbox");
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "DROPBOX_APIKEY", getDropboxApiKey());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "DROPBOX_APISECRET",
        getDropboxApiSecret());
    executeSql("insert into SystemSetting (id, settingKey, value) values ((select max(id) + 1 from SystemSetting), ?, ?)", "DROPBOX_CALLBACKURL",
        getAppUrl(true) + "/login/?return=1&loginMethod=DROPBOX");
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

  protected GreenMail startSmtpServer() {
    GreenMail greenMail = new GreenMail(new ServerSetup(getSmtpPort(), "localhost", ServerSetup.PROTOCOL_SMTP));
    greenMail.start();
    return greenMail;
  }
  
  protected ZonedDateTime getDate(int year, int monthOfYear, int dayOfMonth, ZoneId zone) {
    return ZonedDateTime.of(monthOfYear, monthOfYear, dayOfMonth, 0, 0, 0, 0, zone);
  }
  
  protected ZonedDateTime getDate(int year, int monthOfYear, int dayOfMonth) {
    return getDate(year, monthOfYear, dayOfMonth, ZoneId.systemDefault());
  }

  private class DefinedSqlSet {

    public DefinedSqlSet(String[] before, String[] after, Map<String, String> params) {
      this.before = before;
      this.after = after;
      this.params = params;
    }
    
    public String[] getBefore() {
      return before;
    }
    
    public String[] getAfter() {
      return after;
    }
    
    public Map<String, String> getParams() {
      return params;
    }
    
    private String[] before;
    private String[] after;
    private Map<String, String> params;
  }
  
  private class TestSql {
    
    public TestSql(String file, Map<String, String> params) {
      super();
      this.file = file;
      this.params = params;
    }

    public String getFile() {
      return file;
    }
    
    public Map<String, String> getParams() {
      return params;
    }
    
    private String file;
    private Map<String, String> params;
  }
}
