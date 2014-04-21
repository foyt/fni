package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.openqa.selenium.remote.RemoteWebDriver;

import fi.foyt.fni.test.AbstractTest;

public abstract class AbstractUITest extends AbstractTest {
  
  protected void testLoginRequired(RemoteWebDriver driver, String path) throws UnsupportedEncodingException {
    try {
      String appUrl = getAppUrl();
      String ctxPath = getCtxPath();
      driver.get(appUrl + path);
      assertEquals(appUrl + "/login?redirectUrl=" + URLEncoder.encode("/" + ctxPath + path, "UTF-8"), driver.getCurrentUrl());
    } finally {
      driver.close();
    }
  }
  
}