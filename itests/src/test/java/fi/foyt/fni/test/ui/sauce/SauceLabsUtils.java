package fi.foyt.fni.test.ui.sauce;

import java.util.Arrays;
import java.util.List;

public class SauceLabsUtils {

  public static List<String[]> getDefaultSauceBrowsers() {
    return Arrays.asList(
      ((String[]) new String[] { "firefox", "29", "Windows 8.1" }),
      ((String[]) new String[] { "safari", "7", "OS X 10.9" }),
      ((String[]) new String[] { "chrome", "34", "Linux" })
    );
  }
  
}
