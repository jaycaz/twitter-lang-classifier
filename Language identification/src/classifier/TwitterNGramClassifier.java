package classifier;

import dataReader.ReadData;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.util.Pair;
import featureExtractor.NGramFeatures;
import util.Evaluator;
import util.Language;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by May on 12/2/15.
 */
public class TwitterNGramClassifier {


    private HashMap<String, ClassicCounter<String>> nGramProb;
    private NGramFeatures nGramExtractor;
    private int nGramMax = 4;
    private int nGramMin = 4;

    int nGram = 3;
    int topCounts = 1000;
    double minProb = 1/((double) topCounts * 100);
    int total = 0, error = 0;

    public TwitterNGramClassifier(){
        nGramProb = new HashMap<String, ClassicCounter<String>>();
    }

    public void train(ReadData reader , String filename) {


        HashMap<String, ArrayList<String>> trainingData;
        trainingData = reader.getNextTweets(filename);
        ClassicCounter<String> features = new ClassicCounter<String>();
        while( trainingData != null){
            for (String language : trainingData.keySet()) {
                if(language == null) continue;
                for (String sentence : trainingData.get(language)) {
                    features = countNGrams(sentence, features);
                }
              //  Counters.retainTop(features, topCounts);
                //Counters.normalize(features);

                boolean present = false;
                String k = null;
                for (String key : nGramProb.keySet()) {
                //    System.out.println(key + language);
                    if (key.equals(language)) {
                        present = true;
                        k = key;
                    }
                }
                if (!present) {
                    nGramProb.put(language, features);
                    //System.out.println("Feature len: .. " + nGramProb.get(language).size());
                }
                else{
                    ClassicCounter<String> tempCounter = new ClassicCounter<String>(nGramProb.get(k));
                    tempCounter.addAll(features);
                    nGramProb.put(language, tempCounter);

                    //System.out.println("Feature len: " + tempCounter.size());
                    //System.out.println("Feature len: .. " + nGramProb.get(language).size());
                }

            }
            trainingData = reader.getNextTweets(filename);
        }

    }

    public void trainOnFull (ReadData reader , String filename) {
        HashMap<String, ClassicCounter<String>> counts = new HashMap<String, ClassicCounter<String>>();
        nGramExtractor = new NGramFeatures();
        HashMap<String, ArrayList<String>> trainingData;
        trainingData = reader.getNextTweets(filename);
        while( trainingData != null) {
            for (String language: trainingData.keySet()) {
                ClassicCounter<String> features = new ClassicCounter<String>();
                if (counts.containsKey(language)) {
                    features = counts.get(language);
                }
                for (String sentence : trainingData.get(language)) {
                    features = nGramExtractor.getFeatures(sentence, features, nGramMin, nGramMax);
                }
                counts.put(language, features);
            }
            trainingData = reader.getNextTweets(filename);
        }
        for (String language: counts.keySet()) {
            ClassicCounter<String> features = counts.get(language);
            Counters.retainTop(features, topCounts);
            Counters.normalize(features);
            nGramProb.put(language, features);
        }

    }



    private ClassicCounter<String> countNGrams(String sentence, ClassicCounter<String> counter) {
        sentence = sentence.toLowerCase().replaceAll(" ", "_");
        sentence = "_" + sentence + "_";
        for (int i = 0; i < sentence.length() - nGram; i++) {
            counter.incrementCount(sentence.substring(i, i + nGram), 1);
        }
        return counter;
    }


    public double accuracy2(HashMap<String, ArrayList<String>> testSentences) {
        Evaluator eval = new Evaluator();
        Pair<ArrayList<String>, ArrayList<String>> guessLabels = getGuessLabelLists(testSentences);
        ArrayList<String> guesses = guessLabels.first();
        ArrayList<String> labels = guessLabels.second();
        return eval.accuracy(guesses.toArray(new String[guesses.size()]), labels.toArray(new String[labels.size()]));
    }

    private Pair<ArrayList<String>, ArrayList<String>> getGuessLabelLists(HashMap<String, ArrayList<String>> testSentences) {
        ArrayList<String> guesses = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (String lang: testSentences.keySet()) {
            int i = 0;
            for (String paragraph: testSentences.get(lang)) {
                if (i < 100) {
                    labels.add(lang);
                    guesses.add(classifyLog(paragraph));
                    i++;
                }
            }
        }
        return new Pair<> (guesses, labels);
    }

    public String classifyLog(String sentence) {
        ClassicCounter<String> nGrams = new ClassicCounter<String>();
        nGrams = nGramExtractor.getFeatures(sentence, nGrams, nGramMin, nGramMax);
        double maxProb = Double.NEGATIVE_INFINITY;
        String maxLang = null;
        for (String lang: nGramProb.keySet()) {
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

    public String classify(String sentence) {
        ClassicCounter<String> nGrams = new ClassicCounter<String>();
        nGrams = countNGrams(sentence, nGrams);
        double maxProb = -1;
        String maxLang = null;
        for (String lang: nGramProb.keySet()) {
       //     System.out.println("testing language:  " + lang);
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
                //System.out.println("MAX: " + maxProb + maxLang );
            }
        }
        if (maxLang == null) return "UNKNOWN";
        //System.out.println("returning result: "+ maxLang);
        return maxLang;
    }



    public void accuracy(HashMap<String, ArrayList<String>> testSentences) {

        for (String lang: testSentences.keySet()) {
            System.out.println("Processing lang: " + lang);
            int i = 0;
            for (String paragraph: testSentences.get(lang)) {
                //if (i > 100) continue;
                String guess = classify(paragraph);
                //System.out.println("Supposed to be: " + lang + " guessed " + guess);
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
