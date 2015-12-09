package org.dataReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Author: Martina Marek
 *
 * From a list of input sentences, creates test sentences of specific length.
 */
public class TwitterDataSimulator {

    HashMap<String, ArrayList<String>> testData;

    public TwitterDataSimulator() {
        ReadData reader = new ReadData();
        testData = reader.getInputSentences("_test");
    }

    /**
     * Splits the test data up in sentences in the specified range of lengths.
     *
     * @param minLength
     * @param maxLength
     * @return test data with shortened sentences
     */
    public HashMap<String, ArrayList<String>> getTestingData(int minLength, int maxLength) {
        HashMap<String, ArrayList<String>> shortSentences = new HashMap<String, ArrayList<String>>();
        Random rn = new Random();
        for (String lang: testData.keySet()) {
            ArrayList<String> newSentences = new ArrayList<String>();
            for (String sentence : testData.get(lang)) {
                String words[] = sentence.split(" ");
                if (words.length == 1) {
                    newSentences.add(sentence);
                }
                int i = 0;
                while (words.length - i > minLength) {
                    int randNum = rn.nextInt(maxLength - minLength + 1) + minLength;
                    String shortSentence = "";
                    int endInd;
                    if ((words.length - i - randNum) <= 0) {
                        endInd = words.length - i;
                    } else {
                        endInd = randNum;
                    }
                    for (int j = i; j < i + endInd; j++) {
                        shortSentence = shortSentence + words[j] + " ";
                    }
                    shortSentence = shortSentence.substring(0, shortSentence.length() - 1);
                    newSentences.add(shortSentence);
                    i = i + endInd;
                }
            }
            shortSentences.put(lang, newSentences);
        }
        return shortSentences;
    }
}
