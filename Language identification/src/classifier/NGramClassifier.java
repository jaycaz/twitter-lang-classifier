package classifier;

import java.util.ArrayList;
import java.util.HashMap;

import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.SVMLightClassifier;
import edu.stanford.nlp.classify.SVMLightClassifierFactory;
import edu.stanford.nlp.classify.SVMLightClassifier;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.IntCounter;
import util.Language;

public class NGramClassifier {

	SVMLightClassifier<ClassicCounter<String>, Language> c;
	HashMap<Language, ClassicCounter<String>> nGramProb;
	int nGram = 3;
	int topCounts = 1000;
	double minProb = 1/((double) topCounts * 100);
	
	public void train(
			HashMap<Language, ArrayList<String>> trainingData) {
		nGramProb = new HashMap<Language, ClassicCounter<String>>();
		for(Language language : trainingData.keySet()) {
			ClassicCounter<String> features = new ClassicCounter<String>(); 
			for (String sentence: trainingData.get(language)) {
				features = countNGrams(sentence, features);
			}
			Counters.retainTop(features, topCounts);
			Counters.normalize(features);
			nGramProb.put(language, features);
		}
	}
	
	public void trainClassifier(HashMap<Language, ArrayList<String>> trainingData) {
		GeneralDataset<ClassicCounter<String>, Language> dataSet=new Dataset<ClassicCounter<String>, Language>();
		for(Language language : trainingData.keySet()) {
			ClassicCounter<String> features = new ClassicCounter<String>(); 
			for(String sentence : trainingData.get(language)) {
				features = countNGrams(sentence, features);
			}
			Counters.retainTop(features, topCounts);
			RVFDatum<ClassicCounter<String>, Language> d = new RVFDatum(features, language);
			dataSet.add(d);
		}
		SVMLightClassifierFactory<ClassicCounter<String>, Language> lcFactory = new SVMLightClassifierFactory<ClassicCounter<String>, Language>();
		c = lcFactory.trainClassifier(dataSet);
	}
	
	private ClassicCounter<String> countNGrams(String sentence, ClassicCounter<String> counter) {
		sentence = sentence.toLowerCase().replaceAll(" ", "_");
		sentence = "_" + sentence + "_";
		for (int i = 0; i < sentence.length() - nGram; i++) {
			counter.incrementCount(sentence.substring(i, i + nGram), 1);
		}
		return counter;
	}
	
	/*private ClassicCounter<String> getFeatures(ArrayList<String> sentences, int n) {
		
		return features;
	}
	
	public void writeToFile(String filename) {
		SVMLightClassifier.writeClassifier(c, filename);
	}
	
	public void loadClassifier(String filename) {
		c = SVMLightClassifier.readClassifier(filename);
	}*/
	
	public Language classifyClassifier(String sentence) {
		ClassicCounter<String> features = new ClassicCounter<String>(); 
		features = countNGrams(sentence, features);
		RVFDatum<ClassicCounter<String>, Language> d = new RVFDatum(features);
		Object label = c.classOf(d);
		return (Language) label;
	}

	public Language classify(String sentence) {
		ClassicCounter<String> nGrams = new ClassicCounter<String>();
		nGrams = countNGrams(sentence, nGrams);
		double maxProb = -1;
		Language maxLang = null;
		for (Language lang: nGramProb.keySet()) {
			double prob = 1.0;
			ClassicCounter<String> langCounts = nGramProb.get(lang);
			for (String s: nGrams.keySet()) {
				double p = langCounts.getCount(s);
				if (p == 0) {
					prob = prob * minProb * nGrams.getCount(s);
				} else {
					prob = prob * p * nGrams.getCount(s);
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
		nGrams = countNGrams(sentence, nGrams);
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
	
	
	
	public double accuracy(HashMap<Language, ArrayList<String>> testSentences) {
		int error = 0;
		int total = 0;
		HashMap<String, Double> performance = new HashMap<String, Double>();
		for (Language lang: testSentences.keySet()) {
			System.out.println("Processing lang: " + lang);
			int i = 0;
			for (String paragraph: testSentences.get(lang)) {
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

}
