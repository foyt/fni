package fi.foyt.fni.jade;

import java.util.Locale;

import fi.foyt.fni.i18n.ExternalLocales;

public class JadeLocaleHelper {

  public JadeLocaleHelper(Locale locale) {
    this.locale = locale;
  }
  
  public String text(String key, Object... params) {
    return ExternalLocales.getText(locale, key, params);
  }
  
  public String unformatted(String key) {
    return ExternalLocales.getUnformatted(locale, key);
  }
  
  public String language() {
    return locale.getLanguage();
  }

  public String locale() {
    return locale.toString();
  }
  
  private Locale locale;
}
