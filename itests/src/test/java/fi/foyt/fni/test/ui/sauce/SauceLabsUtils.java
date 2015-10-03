package fi.foyt.fni.test.ui.sauce;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SauceLabsUtils {

  public static List<String[]> getSauceBrowsers() {
    List<String[]> result = new ArrayList<>();
    
    String[] browsers = StringUtils.split(System.getProperty("it.browsers"), ",");
    if (browsers != null) {
      for (String browser : browsers) {
        result.add(StringUtils.split(browser, ":"));
      }
    }
    
    return result;
  }
  
}
