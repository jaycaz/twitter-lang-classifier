package featureExtractor;

import edu.stanford.nlp.stats.ClassicCounter;

public class NGramFeatures {

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
