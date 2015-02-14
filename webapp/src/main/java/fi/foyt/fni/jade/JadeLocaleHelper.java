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
  
  private Locale locale;
  
}
