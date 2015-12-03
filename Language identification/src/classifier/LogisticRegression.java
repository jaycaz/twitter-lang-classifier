package classifier;

import java.util.ArrayList;
import java.util.HashMap;

import util.Language;
import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counters;
import featureExtractor.NGramFeatures;

public class LogisticRegression extends Classifier {
	LinearClassifier<ClassicCounter<String>, String> c;
	NGramFeatures nGramExtractor;
	int topCounts = 1000;
	int nGram = 4;

	
	public void train(HashMap<String, ArrayList<String>> trainingData) {
		nGramExtractor = new NGramFeatures();
		GeneralDataset<ClassicCounter<String>, String> dataSet=new Dataset<ClassicCounter<String>, String>();
		for(String language : trainingData.keySet()) {
			ClassicCounter<String> features = new ClassicCounter<String>(); 
			for(String sentence : trainingData.get(language)) {
				features = nGramExtractor.getFeatures(sentence, features, 0, nGram);
			}
			Counters.retainTop(features, topCounts);
			RVFDatum<ClassicCounter<String>, String> d = new RVFDatum(features, language);
			dataSet.add(d);
		}
		LinearClassifierFactory<ClassicCounter<String>, String> lcFactory = new LinearClassifierFactory<ClassicCounter<String>, String>();
		c = lcFactory.trainClassifier(dataSet);
	}
	
	public void writeToFile(String filename) {
		LinearClassifier.writeClassifier(c, filename);
	}
	
	public void loadClassifier(String filename) {
		c = LinearClassifier.readClassifier(filename);
	}
	
	public String classify(String sentence) {
		ClassicCounter<String> features = new ClassicCounter<String>(); 
		features = nGramExtractor.getFeatures(sentence, features, 0, nGram);
		RVFDatum<ClassicCounter<String>, String> d = new RVFDatum(features);
		Object label = c.classOf(d);
		return (String) label;
	}

}
