package classifier;

import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.SVMLightClassifier;
import edu.stanford.nlp.classify.SVMLightClassifierFactory;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counters;
import util.Language;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by May on 11/20/15.
 */
public class NGramChunkClassify {

    SVMLightClassifier<ClassicCounter<String>, Language> c;
    public HashMap<Language, ClassicCounter<String>> nGramProb;

    int nGram = 3;
    int topCounts = 1000;
    double minProb = 1/((double) topCounts * 100);
    int total = 0, error = 0;

    public NGramChunkClassify(){
        nGramProb = new HashMap<Language, ClassicCounter<String>>();
    }

    public void train(
            HashMap<Language, ArrayList<String>> trainingData) {

        for(Language language : trainingData.keySet()) {
            ClassicCounter<String> features = new ClassicCounter<String>();
            for (String sentence: trainingData.get(language)) {
                features = countNGrams(sentence, features);
            }
            //Counters.retainTop(features, topCounts);
            //Counters.normalize(features);
            System.out.println(nGramProb.containsKey(language));
            for(Language key: nGramProb.keySet() )
            {
                System.out.println("IN ITERATOR: " + key);
                if(key.equals(language)){
                    ClassicCounter<String> tempCounter = new ClassicCounter<String>(nGramProb.get(language));
                    tempCounter.addAll(features);
                    nGramProb.remove(language);
                    nGramProb.put(language, tempCounter);
                    System.out.println("Feature len: " + tempCounter.size() );
                    System.out.println("Feature len: " + nGramProb.get(language).size() );

                }
            }

            nGramProb.put(language, features);
            System.out.println("Feature len: " + nGramProb.get(language).size());
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



    public void accuracy(HashMap<Language, ArrayList<String>> testSentences) {

        for (Language lang: testSentences.keySet()) {
            System.out.println("Processing lang: " + lang);
            int i = 0;
            for (String paragraph: testSentences.get(lang)) {
                //if (i > 100) continue;
                Language guess = classify(paragraph);
                if (!lang.equals(guess)) {
                    error++;
                }
                total++;
                i++;
            }
        }
    }

    public double getAccuracy(){
        return  (total - error) / (float) total;
    }

    public void retainTop(String language)
    {
        Counters.retainTop(nGramProb.get(new Language(language)), topCounts);
    }
}
