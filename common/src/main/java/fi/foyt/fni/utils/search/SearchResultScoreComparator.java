package fi.foyt.fni.utils.search;

import java.util.Comparator;

public class SearchResultScoreComparator<T> implements Comparator<SearchResult<T>> {

  @Override
  public int compare(SearchResult<T> o1, SearchResult<T> o2) {
    Float score1 = o1.getScore();
    Float score2 = o2.getScore();
    
    if (score1 == null) {
      score1 = 0f;
    }

    if (score2 == null) {
      score2 = 0f;
    }
    
    return score2.compareTo(score1);
  }
  
}
