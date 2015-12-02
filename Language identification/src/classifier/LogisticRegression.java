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
	LinearClassifier<ClassicCounter<String>, Language> c;
	NGramFeatures nGramExtractor;
	int topCounts = 1000;
	int nGram = 4;

	
	public void train(HashMap<Language, ArrayList<String>> trainingData) {
		nGramExtractor = new NGramFeatures();
		GeneralDataset<ClassicCounter<String>, Language> dataSet=new Dataset<ClassicCounter<String>, Language>();
		for(Language language : trainingData.keySet()) {
			ClassicCounter<String> features = new ClassicCounter<String>(); 
			for(String sentence : trainingData.get(language)) {
				features = nGramExtractor.getFeatures(sentence, features, 0, nGram);
			}
			Counters.retainTop(features, topCounts);
			RVFDatum<ClassicCounter<String>, Language> d = new RVFDatum(features, language);
			dataSet.add(d);
		}
		LinearClassifierFactory<ClassicCounter<String>, Language> lcFactory = new LinearClassifierFactory<ClassicCounter<String>, Language>();
		c = lcFactory.trainClassifier(dataSet);
	}
	
	public void writeToFile(String filename) {
		LinearClassifier.writeClassifier(c, filename);
	}
	
	public void loadClassifier(String filename) {
		c = LinearClassifier.readClassifier(filename);
	}
	
	public Language classify(String sentence) {
		ClassicCounter<String> features = new ClassicCounter<String>(); 
		features = nGramExtractor.getFeatures(sentence, features, 0, nGram);
		RVFDatum<ClassicCounter<String>, Language> d = new RVFDatum(features);
		Object label = c.classOf(d);
		return (Language) label;
	}

}
