package classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;
import util.Language;


public class FrequencyClassifier extends Classifier {
	HashMap<Language, ArrayList<String>> mostFrequentWords;
	int numWords = 100;

	// Find most frequent word for every language
	public void train(HashMap<Language, ArrayList<ArrayList<String>>> trainingData) {
		mostFrequentWords = new HashMap<Language, ArrayList<String>> ();

		for(Language language : trainingData.keySet()) {
			Counter<String> wordCounts = new Counter<String>();

			for(ArrayList<String> paragraph : trainingData.get(language)) {
				for(String word : paragraph) {
					// Add word to word counts
					wordCounts.incrementCount(word, 1);
				}
			}
			List<String> allWords = Counters.toSortedList(wordCounts);
			ArrayList<String> topWords = new ArrayList<String>(allWords.subList(0, numWords));
			mostFrequentWords.put(language, topWords);
			//System.out.println("Language: " + language + " max Word: " + maxWord);
		}
	}
	
	public Language classify(ArrayList<String> sentence) {
		for (Language key: mostFrequentWords.keySet()) {
			if (sentence.contains(mostFrequentWords.get(key))) {
				return key;
			}
		}
		return Language.UNKNOWN;
	}
}
