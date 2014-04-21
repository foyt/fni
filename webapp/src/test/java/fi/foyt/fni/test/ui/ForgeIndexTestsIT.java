package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import java.net.URLEncoder;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ForgeIndexTestsIT extends AbstractUITest {
  
  public ForgeIndexTestsIT() {
    driver = new ChromeDriver();
  }

  @Test
  public void testLoginRedirect() throws Exception {
    try {
      String appUrl = getAppUrl();
      String ctxPath = getCtxPath();
      driver.get(appUrl + "/forge/");
      assertEquals(appUrl + "/login?redirectUrl=" + URLEncoder.encode("/" + ctxPath + "/forge/", "UTF-8"), driver.getCurrentUrl());
    } finally {
      driver.close();
    }
  }
  
  private RemoteWebDriver driver; 
}
