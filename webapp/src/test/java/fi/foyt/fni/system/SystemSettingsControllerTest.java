package fi.foyt.fni.system;

import javax.inject.Inject;

import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(CdiRunner.class)
public class SystemSettingsControllerTest {

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Test
  public void testSiteUrlHttpDefaultPort() {
    System.setProperty("fni-http-port", "80");
    System.setProperty("fni-host", "test.host.com");
    assertEquals("http://test.host.com", systemSettingsController.getSiteUrl(false, false));
  }

  @Test
  public void testSiteUrlHttpDefaultPortContextPath() {
    System.setProperty("fni-http-port", "80");
    System.setProperty("fni-host", "test.host.com");
    System.setProperty("fni-context-path", "/ctx");
    assertEquals("http://test.host.com/ctx", systemSettingsController.getSiteUrl(false, true));
  }

  @Test
  public void testSiteUrlHttpsDefaultPort() {
    System.setProperty("fni-https-port", "443");
    System.setProperty("fni-host", "test.host.com");
    assertEquals("https://test.host.com", systemSettingsController.getSiteUrl(true, false));
  }
  
  @Test
  public void testSiteUrlHttpsDefaultPortContextPath() {
    System.setProperty("fni-https-port", "443");
    System.setProperty("fni-host", "test.host.com");
    System.setProperty("fni-context-path", "/ctx");
    assertEquals("https://test.host.com/ctx", systemSettingsController.getSiteUrl(true, true));
  }
  
  @Test
  public void testSiteUrlHttpNonDefaultPort() {
    System.setProperty("fni-http-port", "1234");
    System.setProperty("fni-host", "test.host.com");
    assertEquals("http://test.host.com:1234", systemSettingsController.getSiteUrl(false, false));
  }
  
  @Test
  public void testSiteUrlHttpNonDefaultPortContextPath() {
    System.setProperty("fni-http-port", "1234");
    System.setProperty("fni-host", "test.host.com");
    System.setProperty("fni-context-path", "/ctx");
    assertEquals("http://test.host.com:1234/ctx", systemSettingsController.getSiteUrl(false, true));
  }

  @Test
  public void testSiteUrlHttpsNonDefaultPort() {
    System.setProperty("fni-https-port", "1234");
    System.setProperty("fni-host", "test.host.com");
    assertEquals("https://test.host.com:1234", systemSettingsController.getSiteUrl(true, false));
  }
  
  @Test
  public void testSiteUrlHttpsNonDefaultPortContextPath() {
    System.setProperty("fni-https-port", "1234");
    System.setProperty("fni-host", "test.host.com");
    System.setProperty("fni-context-path", "/ctx");
    assertEquals("https://test.host.com:1234/ctx", systemSettingsController.getSiteUrl(true, true));
  }

}
