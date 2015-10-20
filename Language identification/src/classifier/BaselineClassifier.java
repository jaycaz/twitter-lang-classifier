package classifier;

import java.util.ArrayList;
import java.util.HashMap;

public class BaselineClassifier implements Classifier {
	HashMap<String, String> mostFrequentWords;
	
	public void train(ArrayList<ArrayList<String>> trainingSentences) {
		mostFrequentWords = new HashMap<String, String> ();
	}
	
	public int classify(ArrayList<String> sentence) {
		
		return 0;
	}
}
