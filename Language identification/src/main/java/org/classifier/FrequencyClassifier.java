package org.classifier;

import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.stats.IntCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Martina Marek
 *
 * Classifier that saves the most frequent words of each language and classifies a sentence based on that.
 */

public class FrequencyClassifier extends Classifier {
	HashMap<String, ArrayList<String>> mostFrequentWords;
	int numWords;


	/**
	 * Constructs a FrequencyClassifier with the most frequent words specified numWords
	 *
	 * @param numWords
     */
	public FrequencyClassifier (int numWords) {
		this.numWords = numWords;
	}

	/**
	 * Constructs a FrequencyClassifier with the 100 most frequent words
	 */
	public FrequencyClassifier () {
		this(100);
	}


	/**
	 *
	 * @param trainingData
     */
	public void train(HashMap<String, ArrayList<String>> trainingData) {
		mostFrequentWords = new HashMap<String, ArrayList<String>> ();
		for(String language : trainingData.keySet()) {
			IntCounter<String> wordCounts = new IntCounter<String>();
			for(String paragraph : trainingData.get(language)) {
				for(String word : paragraph.split(" ")) {
					wordCounts.incrementCount(word, 1);
				}
			}
			List<String> allWords = Counters.toSortedList(wordCounts);
			if (allWords.size() >= numWords) {
				ArrayList<String> topWords = new ArrayList<String>(allWords.subList(0, numWords));
				mostFrequentWords.put(language, topWords);
			} else {
				mostFrequentWords.put(language, (ArrayList<String>) allWords);	
			}
		}
	}

	/**
	 *
	 * @param sentence to classify
	 * @return label
     */
	public String classify(String sentence) {
		IntCounter<String> languageCounts = new IntCounter();
		for (String word: sentence.split(" ")) {
			for (String lang: mostFrequentWords.keySet()) {
				if (mostFrequentWords.get(lang).contains(word)) {
					languageCounts.incrementCount(lang, 1);
				}
			}
		}
		if (languageCounts.isEmpty()) {
			return "UNKNOWN";
		} else {
			String maxLang = Counters.toSortedList(languageCounts).get(0);
			return maxLang;
		}
	}
}
