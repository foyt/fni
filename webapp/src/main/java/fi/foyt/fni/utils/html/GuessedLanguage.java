package fi.foyt.fni.utils.html;

public class GuessedLanguage {

  public GuessedLanguage(String languageCode, double score) {
    this.languageCode = languageCode;
    this.score = score;
  }
  
  public String getLanguageCode() {
    return languageCode;
  }
  
  public double getScore() {
    return score;
  }
  
  private String languageCode;
  private double score;
}
