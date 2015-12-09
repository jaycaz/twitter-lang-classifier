package classifier;

import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;
import featureExtractor.NGramFeatures;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implements an optimized classifier that first classifies on a smaller subset of n-gram features with a Logistic Regression
 * classifier with a specified beam size, and then outputs the final decision on the full set of features with a Bayes classifier.
 *
 */
public class StackedLRNGram extends Classifier {
    LinearClassifier<ClassicCounter<String>, String> c;
    private HashMap<String, ClassicCounter<String>> nGramProb;
    private NGramFeatures nGramExtractor;
    private int nGramMax;
    private int nGramMin;
    private int topCounts;
    private int topCountsReduced;
    private double minProb;


    /**
     *
     * @param nGramMin
     * @param nGramMax
     * @param topCounts
     * @param topCountsReduced
     */
    public StackedLRNGram(int nGramMin, int nGramMax, int topCounts, int topCountsReduced) {
        nGramProb = new HashMap<>();
        nGramExtractor = new NGramFeatures();
        this.nGramMax = nGramMax;
        this.nGramMin = nGramMin;
        this.topCounts = topCounts;
        this.topCountsReduced = topCountsReduced;
        minProb = 1/((double) topCounts * 1000);
    }

    public StackedLRNGram() {
        this(5, 5, 5000, 1000);
    }


    /**
     * Trains a Bayes Classifier that uses n-Gram features and a Logistic Regression classifier on a smaller subset
     * of features.
     *
     * @param trainingData
     */
    public void train(HashMap<String, ArrayList<String>> trainingData) {
        GeneralDataset<ClassicCounter<String>, String> dataSet=new Dataset<ClassicCounter<String>, String>();
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
            RVFDatum<ClassicCounter<String>, String> d = new RVFDatum(featuresReduced, language);
            dataSet.add(d);
        }
        LinearClassifierFactory<ClassicCounter<String>, String> lcFactory = new LinearClassifierFactory<ClassicCounter<String>, String>();
        c = lcFactory.trainClassifier(dataSet);
    }

    /**
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

    /**
     * Returns the top n labels
     *
     * @param nGrams
     * @param n
     * @return list of top n labels
     */
    private ArrayList<String> classifyTop(ClassicCounter<String> nGrams, int n) {
        RVFDatum<ClassicCounter<String>, String> d = new RVFDatum(nGrams);
        Object probCounter = c.logProbabilityOf(d);
        Counters.retainTop((ClassicCounter<String>) probCounter, n);
        ArrayList<String> topLabels = new ArrayList<>();
        for (String l: (ClassicCounter<String>) probCounter) topLabels.add(l);
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
