package classifier;

import java.util.ArrayList;
import java.util.HashMap;

import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.stats.ClassicCounter;
import util.Language;

public class NGramClassifier {

	//LinearClassifier<ClassicCounter<String>, Language> c;
	HashMap<Language, ClassicCounter<String>> nGramProb;
	int nGram = 3;
	int topCounts = 10000;
	double minProb = 1/(topCounts * 10);
	
	public void train(
			HashMap<Language, ArrayList<String>> trainingData) {
		/*GeneralDataset<ClassicCounter<String>, Language> dataSet=new Dataset<ClassicCounter<String>, Language>();
		for(Language language : trainingData.keySet()) {
			for(String sentence : trainingData.get(language)) {
				ClassicCounter<String> features = getFeatures(sentence, nGram);
				RVFDatum<ClassicCounter<String>, Language> d = new RVFDatum(features, language);
				dataSet.add(d);
			}
		}
		LinearClassifierFactory<ClassicCounter<String>, Language> lcFactory = new LinearClassifierFactory<ClassicCounter<String>, Language>();
		c = lcFactory.trainClassifier(dataSet);*/
		
		for(Language language : trainingData.keySet()) {
			nGramProb = new HashMap<Language, ClassicCounter<String>>();
			ClassicCounter<String> features = new ClassicCounter<String>(); 
			for (String sentence: trainingData.get(language)) {
				features = countNGrams(sentence, features);
			}
			Counters.retainTop(features, topCounts);
			Counters.normalize(features);
			nGramProb.put(language, features);
		}
	}
	
	private ClassicCounter<String> countNGrams(String sentence, ClassicCounter<String> counter) {
		sentence.replace(" ", "_");
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
		LinearClassifier.writeClassifier(c, filename);
	}
	
	public void loadClassifier(String filename) {
		c = LinearClassifier.readClassifier(filename);
	}*/

	public Language classify(String sentence) {
		//ClassicCounter<String> features = getFeatures(sentence);
		//RVFDatum<ClassicCounter<String>, Language> d = new RVFDatum(features);
		//Object label = c.classOf(d);
		//return (Language) label;
		ClassicCounter<String> nGrams = new ClassicCounter<String>();
		nGrams = countNGrams(sentence, nGrams);
		double maxProb = -1;
		Language maxLang = null;
		for (Language lang: nGramProb.keySet()) {
			double prob = 0;
			ClassicCounter<String> langCounts = nGramProb.get(lang);
			for (String s: nGrams.keySet()) {
				double p = langCounts.getCount(s);
				if (p == 0) {
					prob = prob * minProb;
				} else {
					prob = p * nGrams.getCount(s);
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
