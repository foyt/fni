package fi.foyt.fni.utils.language;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.spieleck.app.cngram.NGramProfiles;
import de.spieleck.app.cngram.NGramProfiles.Ranker;

public class LanguageUtils {

	public static List<GuessedLanguage> getGuessedLanguages(String text, double threshold) throws IOException {
		List<GuessedLanguage> guessedLanguages = new ArrayList<GuessedLanguage>();

		Ranker ranker = getNGramRanker();

		ranker.account(text);
		NGramProfiles.RankResult result = ranker.getRankResult();

		for (int i = 0, l = result.getLength(); i < l; i++) {
			double score = result.getScore(i);
			if (score > threshold) {
				guessedLanguages.add(new GuessedLanguage(result.getName(i), score));
			} else {
				break;
			}
		}

		return guessedLanguages;
	}

	private static synchronized Ranker getNGramRanker() throws IOException {
		if (nGramRanker == null) {
			NGramProfiles nGramProfiles = new NGramProfiles();
			nGramRanker = nGramProfiles.getRanker();
		}

		return nGramRanker;
	}

	private static Ranker nGramRanker = null;
}
