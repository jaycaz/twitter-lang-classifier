package classifier;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Classifier {
	public abstract void train(ArrayList<ArrayList<String>> trainingSentences);
	public abstract String classify(ArrayList<String> sentence);
	
	public double accuracy(HashMap<Language, ArrayList<ArrayList<String>>> testSentences) {
		int error = 0;
		int total = 0;
		HashMap<String, Double> performance = new HashMap<String, Double>();
		for (String key: testSentences.keySet()) {
			for (ArrayList<String> paragraph: testSentences.get(key)) {
				String label = classify(paragraph);
				if (label != key) {
					error++;
				}
				total++;
			}
		}
		return (1 - error) / (float) total;
	}
}
