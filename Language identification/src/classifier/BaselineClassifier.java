package classifier;

import java.util.ArrayList;
import java.util.HashMap;
import util.Language;

public class BaselineClassifier extends Classifier {
	HashMap<String, String> mostFrequentWords;

	// Find most frequent word for every language
	public void train(HashMap<String, ArrayList<String>> trainingData) {
		mostFrequentWords = new HashMap<String, String> ();
		for(String language : trainingData.keySet()) {
			HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
			String maxWord = "";
			int maxCount = 0;
			for(String paragraph : trainingData.get(language)) {
				for(String word : paragraph.split(" ")) {
					// Add word to word counts
					int newCount = 0;
					if (wordCounts.containsKey(word)) {
						newCount = wordCounts.get(word) + 1;
					} else {
						newCount = 1;
					}
					wordCounts.put(word, newCount);

					// Check if word is current max
					if(newCount > maxCount) {
						maxCount = newCount;
						maxWord = word;
					}
				}
			}
			mostFrequentWords.put(language, maxWord);
			//System.out.println("String: " + language + " max Word: " + maxWord);
		}
	}
	
	public String classify(String sentence) {
		for (String key: mostFrequentWords.keySet()) {
			if (sentence.contains(mostFrequentWords.get(key))) {
				return key;
			}
		}
		return "UNKNOWN";
	}
}
