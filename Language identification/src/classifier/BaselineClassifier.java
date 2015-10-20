package classifier;

import java.util.ArrayList;
import java.util.HashMap;


public class BaselineClassifier extends Classifier {
	HashMap<String, String> mostFrequentWords;
		
	public void train(ArrayList<ArrayList<String>> trainingSentences) {
		mostFrequentWords = new HashMap<String, String> ();
	}
	
	public String classify(ArrayList<String> sentence) {
		for (String key: mostFrequentWords.keySet()) {
			if (sentence.contains(mostFrequentWords.get(key))) {
				return key;
			}
		}
		return "unkown language";
	}
}
