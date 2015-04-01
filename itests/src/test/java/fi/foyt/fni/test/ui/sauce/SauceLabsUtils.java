package fi.foyt.fni.test.ui.sauce;

import java.util.Arrays;
import java.util.List;

public class SauceLabsUtils {

  public static List<String[]> getDefaultSauceBrowsers() {
    return Arrays.asList(new String[][]{
//      ((String[]) new String[] { "firefox", "36.0", "Windows 8.1" }),
//      ((String[]) new String[] { "safari", "8.0", "OS X 10.10" }),
      ((String[]) new String[] { "chrome", "41.0", "Linux" })
    });
  }
  
}
