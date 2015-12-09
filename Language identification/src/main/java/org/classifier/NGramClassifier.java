package org.classifier;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.IntCounter;
import org.featureExtractor.NGramFeatures;

/**
 * Author: Martina Marek
 *
 * Bayes org.classifier that takes n-gram features.
 */

public class NGramClassifier extends Classifier {
	private HashMap<String, ClassicCounter<String>> nGramProb;
	private HashMap<String, ClassicCounter<String>> nGramProbReduced;
	private NGramFeatures nGramExtractor;
	private int nGramMax;
	private int nGramMin;
	private int topCounts;
	private int topCountsReduced;
	private double minProb;


	public NGramClassifier(int nGramMin, int nGramMax, int topCounts, int topCountsReduced) {
		nGramProb = new HashMap<>();
		nGramProbReduced = new HashMap<>();
		nGramExtractor = new NGramFeatures();
		this.nGramMax = nGramMax;
		this.nGramMin = nGramMin;
		this.topCounts = topCounts;
		this.topCountsReduced = topCountsReduced;
		minProb = 1/((double) topCounts * 1000);
	}

	public NGramClassifier() {
		this(5, 5, 5000, 1000);
	}


	/**
	 * Trains a Bayes Classifier that uses n-Gram features
	 *
	 * @param trainingData
     */
	public void train(HashMap<String, ArrayList<String>> trainingData) {
		for(String language: trainingData.keySet()) {
			ClassicCounter<String> features = new ClassicCounter<>();
			for (String sentence: trainingData.get(language)) {
				features = nGramExtractor.getFeatures(sentence, features, nGramMin, nGramMax);
			}
			Counters.retainTop(features, topCounts);
			Counters.normalize(features);
			nGramProb.put(language, features);
			Counter<String> featuresReduced = Counters.getCopy(features);
			Counters.retainTop(featuresReduced, topCountsReduced);
			Counters.normalize(featuresReduced);
			nGramProbReduced.put(language, (ClassicCounter<String>) featuresReduced);
		}
	}

	/**
	 *
	 *
	 * @param sentence
	 * @param beamSearch: whether to use a beam search
	 * @param beamSize
	 * @return predicted label
     */
	public String classify(String sentence, boolean beamSearch, int beamSize) {
		ClassicCounter<String> nGrams = new ClassicCounter<String>();
		nGrams = nGramExtractor.getFeatures(sentence, nGrams, nGramMin, nGramMax);
		ArrayList<String> languages = new ArrayList<>();
		if (beamSearch) {
			languages = classifyTop(nGrams, beamSize);
		} else {
			languages.addAll(nGramProb.keySet());
		}
		double maxProb = Double.NEGATIVE_INFINITY;
		String maxLang = null;
		for (String lang: languages) {
			double prob = 0.0;
			ClassicCounter<String> langCounts = nGramProb.get(lang);
			for (String s: nGrams.keySet()) {
				double p = langCounts.getCount(s);
				if (p == 0) {
					prob = prob + Math.log(minProb) * nGrams.getCount(s);
				} else {
					prob = prob + Math.log(p) * nGrams.getCount(s);
				}
			}
			if (prob > maxProb) {
				maxProb = prob;
				maxLang = lang;
			}
		}
		if (maxLang == null) return "UNKNOWN";
		return maxLang;
	}

	private ArrayList<String> classifyTop(ClassicCounter<String> nGrams, int n) {
		ClassicCounter<String> performance = new ClassicCounter<>();
		for (String lang: nGramProbReduced.keySet()) {
			double prob = 0.0;
			ClassicCounter<String> langCounts = nGramProbReduced.get(lang);
			for (String s: nGrams.keySet()) {
				double p = langCounts.getCount(s);
				if (p == 0) {
					prob = prob + Math.log(minProb) * nGrams.getCount(s);
				} else {
					prob = prob + Math.log(p) * nGrams.getCount(s);
				}
			}
			performance.incrementCount(lang, prob);
		}
		Counters.retainTop(performance, n);
		ArrayList<String> topLabels = new ArrayList<>();
		for (String l: performance) topLabels.add(l);
		return topLabels;
	}

	/**
	 *
	 * @param sentence
	 * @return predicted label
	 */
	public String classify(String sentence) {
		return classify(sentence, false, 1);
	}
	
	public String classifyByCounts(String sentence) {
		IntCounter<String> languageCounts = new IntCounter<String>();
		ClassicCounter<String> nGrams = new ClassicCounter<String>();
		nGrams = nGramExtractor.getFeatures(sentence, nGrams, nGramMin, nGramMax);
		for (String n: nGrams.keySet()) {
			for (String lang: nGramProb.keySet()) {
				if (nGramProb.get(lang).containsKey(n)) {
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

	
	public void writeTopNFeaturesToFileLatex(String filename, int n) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (String lang: nGramProb.keySet()) {
				ClassicCounter<String> counts = nGramProb.get(lang);
				ClassicCounter<String> countsTop10 = new ClassicCounter<String>(counts);
				Counters.retainTop(countsTop10, n);
				String words = lang + " & ";
				for (String s: countsTop10) {
					words = words + s + " & ";
				}
				if (words.length() > 2) words = words.substring(0, words.length() - 2);
				writer.write(words + "\\\\ \n\n");
			}
			writer.close();
		} catch (Exception e) {
			System.out.println(e.fillInStackTrace());
		}
	}

	public void writeTopNFeaturesToFile(String filename, int n) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (String lang: nGramProb.keySet()) {
				ClassicCounter<String> counts = nGramProb.get(lang);
				ClassicCounter<String> countsTop10 = new ClassicCounter<String>(counts);
				Counters.retainTop(countsTop10, n);
				writer.write(lang.toString() + "\n");
				String words = "";
				for (String s: countsTop10) {
					words = words + s + ", ";
				}
				if (words.length() > 2) words = words.substring(0, words.length() - 2);
				writer.write(words + "\n\n");
			}
			writer.close();
		} catch (Exception e) {
			System.out.println(e.fillInStackTrace());
		}
	}

	public void writeTopNFeaturesWithCountToFile(String filename, int n) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (String lang: nGramProb.keySet()) {
				ClassicCounter<String> counts = nGramProb.get(lang);
				ClassicCounter<String> countsTop10 = new ClassicCounter<String>(counts);
				Counters.retainTop(countsTop10, n);
				writer.write(lang.toString() + "\n");
				writer.write(countsTop10 + "\n\n");
			}
			writer.close();
		} catch (Exception e) {
			System.out.println(e.fillInStackTrace());
		}
	}

	public void saveToFile (String pathname) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(pathname + "/params.txt"));
			writer.write(Integer.toString(nGramMin) + ", " + nGramMax);
			writer.close();
			FileOutputStream f = new FileOutputStream(pathname + "/prob.ser");
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(nGramProb);
			s.close();
			f = new FileOutputStream(pathname + "/probReduced.ser");
			s = new ObjectOutputStream(f);
			s.writeObject(nGramProbReduced);
			s.close();
		} catch (IOException e) {
			System.out.println(e.fillInStackTrace());
		}
	}

	public void loadFile (String pathname) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(pathname + "/params.txt")));
			String line = reader.readLine();
			nGramMin = Character.getNumericValue(line.charAt(0));
			nGramMax = Character.getNumericValue(line.charAt(3));
			FileInputStream f = new FileInputStream(new File(pathname + "/prob.ser"));
			ObjectInputStream s = new ObjectInputStream(f);
			nGramProb = (HashMap<String, ClassicCounter<String>>) s.readObject();
			s.close();
			f = new FileInputStream(new File(pathname + "/probReduced.ser"));
			s = new ObjectInputStream(f);
			nGramProbReduced = (HashMap<String, ClassicCounter<String>>) s.readObject();
			s.close();

		} catch (FileNotFoundException e) {
			System.out.println(e.fillInStackTrace());
		} catch (IOException e) {
			System.out.println(e.fillInStackTrace());
		} catch (ClassNotFoundException e) {
			System.out.println(e.fillInStackTrace());
		}

	}

	public void setNGram(int min, int max) {
		nGramMin = min;
		nGramMax = max;
	}

	public void setTopCounts (int top) {
		topCounts = top;
	}

	public void reset() {
		nGramProb.clear();
		nGramProbReduced.clear();
	}

}
