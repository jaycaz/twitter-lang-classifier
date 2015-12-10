package org.classifier;

import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.util.Pair;
import org.util.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Martina Marek
 *
 * General org.classifier class that provides evaluation functions shared by all classifiers.
 */

public abstract class Classifier {
	public abstract void train(HashMap<String, ArrayList<String>> trainingData);

	public abstract String classify(String sentence);
	public String classify(String sentence, boolean beamSearch, int beamSize) {
		//throw new NotImplementedException("method not overridden");
		return null;
	};


	/**
	 * Given a set of test sentences, returns a list of true labels and a list of guesses
	 *
	 * @param testSentences
	 * @return Pair of guesses, labels
     */
	private Pair<ArrayList<String>, ArrayList<String>> getGuessLabelLists(HashMap<String, ArrayList<String>> testSentences) {
		ArrayList<String> guesses = new ArrayList<>();
		ArrayList<String> labels = new ArrayList<>();
		for (String lang: testSentences.keySet()) {
			int i = 0;
			for (String paragraph: testSentences.get(lang)) {
				if (i < 1000) {
					labels.add(lang);
					guesses.add(classify(paragraph));
					i++;
				}
			}
		}
		return new Pair<> (guesses, labels);
	}

	/**
	 * Given a set of test sentences, returns a list of true labels and a list of guesses
	 *
	 * @param testSentences
	 * @return Pair of guesses, labels
	 */
	private Pair<ArrayList<String>, ArrayList<String>> getGuessLabelLists(HashMap<String, ArrayList<String>> testSentences, boolean beamSearch, int beamSize) {
		ArrayList<String> guesses = new ArrayList<>();
		ArrayList<String> labels = new ArrayList<>();
		for (String lang: testSentences.keySet()) {
			int i = 0;
			for (String paragraph: testSentences.get(lang)) {
				if (i < 1000) {
					labels.add(lang);
					guesses.add(classify(paragraph, beamSearch, beamSize));
					i++;
				}
			}
		}
		return new Pair<> (guesses, labels);
	}

	/**
	 * Given a list of test sentences, compute the accuracy over all classes
	 *
	 * @param testSentences
	 * @return accuracy
     */
	public double accuracy(HashMap<String, ArrayList<String>> testSentences) {
		Evaluator eval = new Evaluator();
		Pair<ArrayList<String>, ArrayList<String>> guessLabels = getGuessLabelLists(testSentences);
		ArrayList<String> guesses = guessLabels.first();
		ArrayList<String> labels = guessLabels.second();
		return eval.accuracy(guesses.toArray(new String[guesses.size()]), labels.toArray(new String[labels.size()]));
	}

	/**
	 * Given a list of test sentences, compute the accuracy over all classes
	 *
	 * @param testSentences
	 * @return accuracy
	 */
	public double accuracy(HashMap<String, ArrayList<String>> testSentences, boolean beamSearch, int beamSize) {
		Evaluator eval = new Evaluator();
		Pair<ArrayList<String>, ArrayList<String>> guessLabels = getGuessLabelLists(testSentences, beamSearch, beamSize);
		ArrayList<String> guesses = guessLabels.first();
		ArrayList<String> labels = guessLabels.second();
		return eval.accuracy(guesses.toArray(new String[guesses.size()]), labels.toArray(new String[labels.size()]));
	}

	/**
	 * Computes the accuracy per class
	 *
	 * @param testSentences
	 * @return counter with labels and their corresponding accuracy
     */
	public ClassicCounter<String> accuracyByClass(HashMap<String, ArrayList<String>> testSentences) {
		Evaluator eval = new Evaluator();
		Pair<ArrayList<String>, ArrayList<String>> guessLabels = getGuessLabelLists(testSentences);
		ArrayList<String> guesses = guessLabels.first();
		ArrayList<String> labels = guessLabels.second();
		return eval.accuracyByClass(guesses.toArray(new String[guesses.size()]), labels.toArray(new String[labels.size()]));
	}

	/**
	 * Computes the F1 score over all classes.
	 *
	 * @param testSentences
	 * @return F1 score
     */
	public double f1(HashMap<String, ArrayList<String>> testSentences) {
		ClassicCounter<String> f1 = f1ByClass(testSentences);
		return f1.getCount("total");
	}

	/**
	 * Computes the F1 score by class and in total, option to print confusion matrix
	 *
	 * @param testSentences
	 * @param printConfusionMatrix: wether to print confusion matrix
	 * @param filename
	 * @return Counter of F1 scores
	 */
	public ClassicCounter<String> f1ByClass(HashMap<String, ArrayList<String>> testSentences, boolean printConfusionMatrix, String filename) {
		Evaluator eval = new Evaluator();
		Pair<ArrayList<String>, ArrayList<String>> guessLabels = getGuessLabelLists(testSentences);
		ArrayList<String> guesses = guessLabels.first();
		ArrayList<String> labels = guessLabels.second();
		return eval.f1ByClass(guesses.toArray(new String[guesses.size()]), labels.toArray(new String[labels.size()]), printConfusionMatrix, filename);
	}

	/**
	 * Computes the F1 score by class and in total
	 *
	 * @param testSentences
	 * @return Counter of F1 scores
     */
	public ClassicCounter<String> f1ByClass(HashMap<String, ArrayList<String>> testSentences) {
		return f1ByClass(testSentences, false, "");
	}



	public Pair<Pair<Double, Double>, ArrayList<Pair<String, Pair<Double, Double>>>> accuracyAndF1ByClass(HashMap<String, ArrayList<String>> testSentences) {
		ArrayList<Pair<String, Pair<Double, Double>>> performance = new ArrayList<Pair<String, Pair<Double, Double>>>();
		ClassicCounter<String> f1 = f1ByClass(testSentences);
		ClassicCounter<String> acc = accuracyByClass(testSentences);
		for (String lang: acc.keySet()) {
			performance.add(new Pair<String, Pair<Double, Double>>(lang, new Pair<Double, Double>(acc.getCount(lang), f1.getCount(lang))));
		}
		double accuracyAll = accuracy(testSentences);
		System.out.println("Total: Accuracy: " + accuracyAll);
		return new Pair(new Pair<Double, Double>(accuracyAll, f1.getCount("total")), performance);
	}

	public void writeScoresToFile(String filename, HashMap<String, ArrayList<String>> testSentences) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			Pair<Pair<Double, Double>, ArrayList<Pair<String, Pair<Double, Double>>>> scores = accuracyAndF1ByClass(testSentences);
			writer.write(" & Accuracy & F1 \\\\ \n\n");
			String line = "Total & " + scores.first().first() + " & " + scores.first().second() + " \\\\ \n\n";
			writer.write(line);
			for(Pair<String, Pair<Double, Double>> score: scores.second()) {
				String s = score.first() + " & " + score.second().first() + " & " + score.second().second() + "\\\\ \n\n";
				writer.write(s);
			}
			writer.close();
		} catch (Exception e) {
			System.out.println(e.fillInStackTrace());
		}
	}

	public void writeAccuracyByClassSortedToFile (String filename, HashMap<String, ArrayList<String>> testData) {
		ClassicCounter<String> acc = accuracyByClass(testData);
		List<Pair<String, Double>> list = Counters.toSortedListWithCounts(acc);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write("Language: Accuracy\n");
			for(Pair<String, Double> p: list) {
				out.write(p.first() + ": " + Double.toString(p.second()) + "\n");
			}
			out.close();
		} catch (Exception e) {
			System.out.println(e.fillInStackTrace());
		}
	}
}
