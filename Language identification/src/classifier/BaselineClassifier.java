package classifier;

import java.util.ArrayList;
import java.util.HashMap;
import util.Language;

public class BaselineClassifier extends Classifier {
	HashMap<Language, String> mostFrequentWords;

	// Find most frequent word for every language
	public void train(HashMap<Language, ArrayList<ArrayList<String>>> trainingData) {
		mostFrequentWords = new HashMap<Language, String> ();

		for(Language language : trainingData.keySet()) {
			HashMap<String, Integer> wordCounts = new HashMap<>();
			String maxWord = "";
			int maxCount = 0;

			for(ArrayList<String> paragraph : trainingData.get(language)) {
				for(String word : paragraph) {
					// Add word to word counts
					int newCount = wordCounts.getOrDefault(word, 0) + 1;
					wordCounts.put(word, newCount);

					// Check if word is current max
					if(newCount > maxCount) {
						maxCount = newCount;
						maxWord = word;
					}
				}
			}

			mostFrequentWords.put(language, maxWord);
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
