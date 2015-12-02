package classifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.IntCounter;
import featureExtractor.NGramFeatures;
import util.Language;

public class NGramClassifier extends Classifier {
	private HashMap<Language, ClassicCounter<String>> nGramProb;
	private NGramFeatures nGramExtractor;
	private int nGramMax = 5;
	private int nGramMin = 5;
	
	private int topCounts = 5000;
	private double minProb = 1/((double) topCounts * 1000);
	
	public void train(
			HashMap<Language, ArrayList<String>> trainingData) {
		nGramProb = new HashMap<Language, ClassicCounter<String>>();
		nGramExtractor = new NGramFeatures();
		for(Language language: trainingData.keySet()) {
			ClassicCounter<String> features = new ClassicCounter<String>(); 
			for (String sentence: trainingData.get(language)) {
				features = nGramExtractor.getFeatures(sentence, features, nGramMin, nGramMax);
			}
			Counters.retainTop(features, topCounts);
			Counters.normalize(features);
			nGramProb.put(language, features);
		}
	}
	

	public Language classify(String sentence) {
		ClassicCounter<String> nGrams = new ClassicCounter<String>();
		nGrams = nGramExtractor.getFeatures(sentence, nGrams, nGramMin, nGramMax);
		double maxProb = Double.NEGATIVE_INFINITY;
		Language maxLang = null;
		for (Language lang: nGramProb.keySet()) {
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
		if (maxLang == null) return Language.UNKNOWN;
		return maxLang;
	}
	
	public Language classifyByCounts(String sentence) {
		IntCounter<Language> languageCounts = new IntCounter<Language>();
		ClassicCounter<String> nGrams = new ClassicCounter<String>();
		nGrams = nGramExtractor.getFeatures(sentence, nGrams, nGramMin, nGramMax);
		for (String n: nGrams.keySet()) {
			for (Language lang: nGramProb.keySet()) {
				if (nGramProb.get(lang).containsKey(n)) {
					languageCounts.incrementCount(lang, 1);
				}
			}
		}
		if (languageCounts.isEmpty()) {
			return Language.UNKNOWN;
		} else {
			Language maxLang = Counters.toSortedList(languageCounts).get(0);
			return maxLang;
		}
	}

	
	public void writeTopNFeaturesToFileLatex(String filename, int n) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (Language lang: nGramProb.keySet()) {
				ClassicCounter<String> counts = nGramProb.get(lang);
				ClassicCounter<String> countsTop10 = new ClassicCounter<String>(counts);
				Counters.retainTop(countsTop10, n);
				String words = lang.getName() + " & ";
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
			for (Language lang: nGramProb.keySet()) {
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
			for (Language lang: nGramProb.keySet()) {
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

	public void setNGram(int min, int max) {
		nGramMin = min;
		nGramMax = max;
	}

	public void setTopCounts (int top) {
		topCounts = top;
	}

	public void reset() {
		nGramProb.clear();
	}

}
