package classifier;

import java.util.ArrayList;
import java.util.HashMap;

import edu.stanford.nlp.stats.MultiClassPrecisionRecallExtendedStats;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.Triple;
import util.Language;

public abstract class Classifier {
	public abstract void train(HashMap<Language, ArrayList<ArrayList<String>>> trainingData);
	public abstract Language classify(ArrayList<String> sentence);
	
	public double accuracy(HashMap<Language, ArrayList<ArrayList<String>>> testSentences) {
		int error = 0;
		int total = 0;
		HashMap<String, Double> performance = new HashMap<String, Double>();
		for (Language lang: testSentences.keySet()) {
			System.out.println("Processing lang: " + lang);
			int i = 0;
			for (ArrayList<String> paragraph: testSentences.get(lang)) {
				if (i > 100) continue;
				Language guess = classify(paragraph);
				if (!lang.equals(guess)) {
					error++;
				}
				total++;
				i++;
			}
		}
		return (total - error) / (float) total;
	}
	
	public ArrayList<Pair<Language, Double>> accuracyByClass(HashMap<Language, ArrayList<ArrayList<String>>> testSentences) {
		ArrayList<Pair<Language, Double>> classAccuracy = new ArrayList<Pair<Language, Double>>();
		for (Language lang: testSentences.keySet()) {
			int error = 0;
			int total = 0;
			for (ArrayList<String> paragraph: testSentences.get(lang)) {
				Language guess = classify(paragraph);
				if (!lang.equals(guess)) {
					error++;
				}
				total++;
			}
			float accuracy = (total - error) / (float) total;
			classAccuracy.add(new Pair(lang, accuracy));
			System.out.println("Language:" + lang.getName() + ", Accuracy: " + accuracy);
		}
		return classAccuracy;
	}
	
	public double f1(HashMap<Language, ArrayList<ArrayList<String>>> testSentences) {
        MultiClassPrecisionRecallExtendedStats<Language> fscore = new MultiClassPrecisionRecallExtendedStats<Language>(null);
        ArrayList<Language> guesses = new ArrayList<Language>();
        ArrayList<Language> labels = new ArrayList<Language>();
        for (Language lang: testSentences.keySet()) {
        	for (ArrayList<String> paragraph: testSentences.get(lang)) {
				guesses.add(classify(paragraph));
				labels.add(lang);
			}
        }
        return fscore.score(guesses, labels);
	}
	
	public ArrayList<Pair<Language, Double>> f1ByClass(HashMap<Language, ArrayList<ArrayList<String>>> testSentences) {
        MultiClassPrecisionRecallExtendedStats<Language> fscore = new MultiClassPrecisionRecallExtendedStats<Language>(null);
		ArrayList<Pair<Language, Double>> classF1 = new ArrayList<Pair<Language, Double>>();
		for (Language lang: testSentences.keySet()) {
			ArrayList<Language> guesses = new ArrayList<Language>();
	        ArrayList<Language> labels = new ArrayList<Language>();
			for (ArrayList<String> paragraph: testSentences.get(lang)) {
				guesses.add(classify(paragraph));
				labels.add(lang);
			}
			double f1 = fscore.score(guesses, labels);
			classF1.add(new Pair(lang, f1));
			System.out.println("Language:" + lang.getName() + ", F1: " + f1);
		}
		return classF1;
	}
}
