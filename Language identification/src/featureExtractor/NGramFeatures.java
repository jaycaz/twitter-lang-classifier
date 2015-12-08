package featureExtractor;

import edu.stanford.nlp.stats.ClassicCounter;

/**
 * Author: Martina Marek
 *
 * N-gram feature extractor.
 */
public class NGramFeatures {

	/**
	 * returns the n-gram features of a given sentence
	 *
	 * @param sentence
	 * @param counter where to write the n-grams into
	 * @param nMin: lower bound for the n-gram range
	 * @param nMax: upper bound for the n-gram range
     * @return
     */
	public ClassicCounter<String> getFeatures(String sentence, ClassicCounter<String> counter, int nMin, int nMax) {
		sentence = sentence.toLowerCase().replaceAll(" ", "_");
		sentence = "_" + sentence + "_";
		for (int j = nMin - 1; j < nMax; j++) {
			for (int i = 0; i < sentence.length() - j; i++) {
				if (j == 0 && sentence.substring(i, i + j + 1).equals("_")) continue;
				counter.incrementCount(sentence.substring(i, i + j + 1), 1);
			}
		}
		return counter;
	}
	
}
