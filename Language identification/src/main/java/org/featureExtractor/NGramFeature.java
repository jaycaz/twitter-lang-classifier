package org.featureExtractor;

import edu.stanford.nlp.stats.ClassicCounter;

/**
 * Created by May on 11/19/15.
 */
public class NGramFeature {
    int nGram;

    public NGramFeature(int n){
        nGram = n;
    }

    public ClassicCounter<String> countNGrams(String sentence, ClassicCounter<String> counter) {
        sentence = sentence.toLowerCase().replaceAll(" ", "_");
        sentence = "_" + sentence + "_";
        for (int i = 0; i < sentence.length() - nGram; i++) {
            counter.incrementCount(sentence.substring(i, i + nGram), 1);
        }
        return counter;
    }
}
