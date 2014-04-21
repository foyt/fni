package fi.foyt.fni.test;

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

}
