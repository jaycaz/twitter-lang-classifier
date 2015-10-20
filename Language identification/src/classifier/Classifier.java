package classifier;

import java.util.ArrayList;
import java.util.HashMap;
import util.Language;

public abstract class Classifier {
	public abstract void train(HashMap<Language, ArrayList<ArrayList<String>>> trainingData);
	public abstract Language classify(ArrayList<String> sentence);
	
	public double accuracy(HashMap<Language, ArrayList<ArrayList<String>>> testSentences) {
		int error = 0;
		int total = 0;
		HashMap<String, Double> performance = new HashMap<String, Double>();
		for (Language lang: testSentences.keySet()) {
			for (ArrayList<String> paragraph: testSentences.get(lang)) {
				Language guess = classify(paragraph);
				if (!lang.equals(guess)) {
					error++;
				}
				total++;
			}
		}
		return (1 - error) / (float) total;
	}
}
