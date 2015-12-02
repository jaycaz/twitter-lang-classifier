package classifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.util.Pair;
import util.Evaluator;
import util.Language;

public abstract class Classifier {
	public abstract void train(HashMap<Language, ArrayList<String>> trainingData);
	public abstract Language classify(String sentence);

	/**
	 * Given a set of test sentences, returns a list of true labels and a list of guesses
	 *
	 * @param testSentences
	 * @return Pair of guesses, labels
     */
	private Pair<ArrayList<String>, ArrayList<String>> getGuessLabelLists(HashMap<Language, ArrayList<String>> testSentences) {
		ArrayList<String> guesses = new ArrayList<>();
		ArrayList<String> labels = new ArrayList<>();
		for (Language lang: testSentences.keySet()) {
			for (String paragraph: testSentences.get(lang)) {
				labels.add(lang.getName());
				guesses.add(classify(paragraph).getName());
			}
		}
		return new Pair<> (guesses, labels);
	}

	public double accuracy(HashMap<Language, ArrayList<String>> testSentences) {
		Evaluator eval = new Evaluator();
		Pair<ArrayList<String>, ArrayList<String>> guessLabels = getGuessLabelLists(testSentences);
		ArrayList<String> guesses = guessLabels.first();
		ArrayList<String> labels = guessLabels.second();
		return eval.accuracy(guesses.toArray(new String[guesses.size()]), labels.toArray(new String[labels.size()]));
	}


	public ClassicCounter<String> accuracyByClass(HashMap<Language, ArrayList<String>> testSentences) {
		Evaluator eval = new Evaluator();
		Pair<ArrayList<String>, ArrayList<String>> guessLabels = getGuessLabelLists(testSentences);
		ArrayList<String> guesses = guessLabels.first();
		ArrayList<String> labels = guessLabels.second();
		return eval.accuracyByClass(guesses.toArray(new String[guesses.size()]), labels.toArray(new String[labels.size()]));
	}

	public double f1(HashMap<Language, ArrayList<String>> testSentences) {
		ClassicCounter<String> f1 = f1ByClass(testSentences);
		return f1.getCount("total");
	}

	public ClassicCounter<String> f1ByClass(HashMap<Language, ArrayList<String>> testSentences) {
		Evaluator eval = new Evaluator();
		Pair<ArrayList<String>, ArrayList<String>> guessLabels = getGuessLabelLists(testSentences);
		ArrayList<String> guesses = guessLabels.first();
		ArrayList<String> labels = guessLabels.second();
		return eval.f1ByClass(guesses.toArray(new String[guesses.size()]), labels.toArray(new String[labels.size()]), false);
	}

	public void f1Acc(HashMap<Language, ArrayList<String>> testSentences) {
		ClassicCounter<String> f = f1ByClass(testSentences);
		ClassicCounter<String> a = accuracyByClass(testSentences);
		for (String lang: f.keySet()) {
			System.out.println("F1 for lang: "+ lang + " = " + f.getCount(lang) + ", accuracy: " + a.getCount(lang));
		}
	}

	public Pair<Pair<Double, Double>, ArrayList<Pair<String, Pair<Double, Double>>>> accuracyAndF1ByClass(HashMap<Language, ArrayList<String>> testSentences) {
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

	public void writeScoresToFile(String filename, HashMap<Language, ArrayList<String>> testSentences) {
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

	public void writeAccuracyByClassSortedToFile (String filename, HashMap<Language, ArrayList<String>> testData) {
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
