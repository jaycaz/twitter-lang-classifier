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
import edu.stanford.nlp.stats.IntCounter;
import util.Language;

public class NGramClassifier extends Classifier {

	LinearClassifier<IntCounter<String>, Language> c;
	
	@Override
	public void train(
			HashMap<Language, ArrayList<ArrayList<String>>> trainingData) {
		GeneralDataset<IntCounter<String>, Language> dataSet=new Dataset<IntCounter<String>, Language>();
		for(Language language : trainingData.keySet()) {
			for(ArrayList<String> paragraph : trainingData.get(language)) {
				IntCounter<String> features = getFeatures(paragraph);
				RVFDatum<IntCounter<String>, Language> d = new RVFDatum(features, language);
				dataSet.add(d);
			}
		}
		LinearClassifierFactory<IntCounter<String>, Language> lcFactory = new LinearClassifierFactory<IntCounter<String>, Language>();
		c = lcFactory.trainClassifier(dataSet);
	}
	
	private IntCounter<String> getFeatures(ArrayList<String> sentence) {
		IntCounter features = new IntCounter(); 
		for (String word: sentence) {
			features.incrementCount(word, 1);
		}
		return features;
	}
	
	public void writeToFile(String filename) {
		LinearClassifier.writeClassifier(c, filename);
	}
	
	public void loadClassifier(String filename) {
		c = LinearClassifier.readClassifier(filename);
	}

	@Override
	public Language classify(ArrayList<String> sentence) {
		IntCounter<String> features = getFeatures(sentence);
		RVFDatum<IntCounter<String>, Language> d = new RVFDatum(features);
		Object label = c.classOf(d);
		return (Language) label;
	}
	
	

}
