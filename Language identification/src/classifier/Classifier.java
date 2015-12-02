package classifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import edu.stanford.nlp.math.DoubleAD;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.stats.MultiClassPrecisionRecallExtendedStats;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.Triple;
import util.Language;

public abstract class Classifier {
	public abstract void train(HashMap<Language, ArrayList<String>> trainingData);
	public abstract Language classify(String sentence);


	public double accuracy(HashMap<Language, ArrayList<String>> testSentences) {
		int error = 0;
		int total = 0;
		HashMap<String, Double> performance = new HashMap<String, Double>();
		for (Language lang: testSentences.keySet()) {
			System.out.println("Processing lang: " + lang);
			int i = 0;
			for (String paragraph: testSentences.get(lang)) {
				if (i > 1000) continue;
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

	public ArrayList<Pair<Language, Double>> accuracyByClass(HashMap<Language, ArrayList<String>> testSentences) {
		ArrayList<Pair<Language, Double>> classAccuracy = new ArrayList<Pair<Language, Double>>();
		for (Language lang: testSentences.keySet()) {
			int error = 0;
			int total = 0;
			for (String paragraph: testSentences.get(lang)) {
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

	public double f1(HashMap<Language, ArrayList<String>> testSentences) {
		MultiClassPrecisionRecallExtendedStats<Language> fscore = new MultiClassPrecisionRecallExtendedStats<Language>(null);
		ArrayList<Language> guesses = new ArrayList<Language>();
		ArrayList<Language> labels = new ArrayList<Language>();
		for (Language lang: testSentences.keySet()) {
			for (String paragraph: testSentences.get(lang)) {
				guesses.add(classify(paragraph));
				labels.add(lang);
			}
		}
		return fscore.score(guesses, labels);
	}

	public ArrayList<Pair<Language, Double>> f1ByClass(HashMap<Language, ArrayList<String>> testSentences) {
		MultiClassPrecisionRecallExtendedStats<Language> fscore = new MultiClassPrecisionRecallExtendedStats<Language>(null);
		ArrayList<Pair<Language, Double>> classF1 = new ArrayList<Pair<Language, Double>>();
		for (Language lang: testSentences.keySet()) {
			ArrayList<Language> guesses = new ArrayList<Language>();
			ArrayList<Language> labels = new ArrayList<Language>();
			for (String paragraph: testSentences.get(lang)) {
				guesses.add(classify(paragraph));
				labels.add(lang);
			}
			double f1 = fscore.score(guesses, labels);
			classF1.add(new Pair(lang, f1));
			System.out.println("Language:" + lang.getName() + ", F1: " + f1);
		}
		return classF1;
	}

	public Pair<Double, ArrayList<Pair<Language, Double>>> accuracyAndF1ByClass(HashMap<Language, ArrayList<String>> testSentences) {
		ArrayList<Pair<Language, Pair<Double, Double>>> performance = new ArrayList<Pair<Language, Pair<Double, Double>>>();
		int errorAll = 0;
		int totalAll = 0;
		for (Language lang: testSentences.keySet()) {
			//System.out.println(lang);
			//System.out.println(testSentences.get(lang));
			int error = 0;
			int total = 0;
			int i = 0;
			for (String paragraph: testSentences.get(lang)) {
				i++;
				if (i > 100) continue;
				Language guess = classify(paragraph);
				if (!lang.equals(guess)) {
					error++;
					errorAll++;
				}
				total++;
				totalAll++;
			}
			float accuracy = (total - error) / (float) total;
			if (Float.isNaN(accuracy)) {
				System.out.println("NaN!! " + lang + " total: " + total + " error: " + error);
			}
			performance.add(new Pair(lang, accuracy));
		}
		double accuracyAll = (totalAll - errorAll) / (float) totalAll;
		System.out.println("Total: Accuracy: " + accuracyAll);
		return new Pair(accuracyAll, performance);
	}

	public void writeScoresToFile(String filename, HashMap<Language, ArrayList<String>> testSentences) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			Pair<Double, ArrayList<Pair<Language, Double>>> scores = accuracyAndF1ByClass(testSentences);
			writer.write(" & Accuracy \\\\ \n\n");
			String line = "Total & " + scores.first() + " \\\\ \n\n";
			writer.write(line);
			for(Pair<Language, Double> score: scores.second()) {
				String s = score.first().getName() + " & " + score.second() + "\\\\ \n\n";
				writer.write(s);
			}
			writer.close();
		} catch (Exception e) {
			System.out.println(e.fillInStackTrace());
		}
	}
}
