package main.java.org.classifier;

import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counters;
import main.java.org.featureExtractor.NGramFeatures;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: Martina Marek
 *
 * Logistic regression org.classifier that uses n-gram features for classification.
 *
 */
public class LogisticRegression extends Classifier {
	LinearClassifier<ClassicCounter<String>, String> c;
	NGramFeatures nGramExtractor;
	int topCounts;
	int nGram;

	/**
	 * Constructs a Logistic Regression org.classifier
	 *
	 * @param topCounts
	 * @param nGram
     */
	public LogisticRegression (int topCounts, int nGram) {
		nGramExtractor = new NGramFeatures();
		this.topCounts = topCounts;
		this.nGram = nGram;
	}

	/**
	 * Constructs a Logistic Regression org.classifier with 5-grams and the 5000 most frequent n-grams
	 */
	public LogisticRegression () {
		this(1000, 5);
	}


	/**
	 *
	 * @param trainingData
     */
	public void train(HashMap<String, ArrayList<String>> trainingData) {
		GeneralDataset<ClassicCounter<String>, String> dataSet=new Dataset<ClassicCounter<String>, String>();
		for(String language : trainingData.keySet()) {
			ClassicCounter<String> features = new ClassicCounter<String>(); 
			for(String sentence : trainingData.get(language)) {
				features = nGramExtractor.getFeatures(sentence, features, nGram, nGram);
			}
			Counters.retainTop(features, topCounts);
			RVFDatum<ClassicCounter<String>, String> d = new RVFDatum(features, language);
			dataSet.add(d);
		}
		LinearClassifierFactory<ClassicCounter<String>, String> lcFactory = new LinearClassifierFactory<ClassicCounter<String>, String>();
		c = lcFactory.trainClassifier(dataSet);
	}

	/**
	 * writes a trained org.classifier to a file
	 *
	 * @param filename: where to save the file
     */
	public void writeToFile(String filename) {
		LinearClassifier.writeClassifier(c, filename);
	}

	/**
	 * loads a previously saved org.classifier
	 *
	 * @param filename: location and name where the org.classifier is stored
     */
	public void loadClassifier(String filename) {
		c = LinearClassifier.readClassifier(filename);
	}

	/**
	 *
	 * @param sentence to classify
	 * @return label
     */
	public String classify(String sentence) {
		ClassicCounter<String> features = new ClassicCounter<String>(); 
		features = nGramExtractor.getFeatures(sentence, features, nGram, nGram);
		RVFDatum<ClassicCounter<String>, String> d = new RVFDatum(features);
		Object label = c.classOf(d);
		return (String) label;
	}

	public void setNGram(int nGram) {
		this.nGram = nGram;
	}


}
