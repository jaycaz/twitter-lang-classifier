package dataReader;

import util.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by martina on 11/18/15.
 */
public class TwitterDataSimulator {

    public HashMap<Language, ArrayList<String>> getTestingData(int minLength, int maxLength) {
        HashMap<Language, ArrayList<String>> shortSentences = new HashMap<Language, ArrayList<String>>();
        ReadData reader = new ReadData();
        HashMap<Language, ArrayList<String>> testData = reader.getInputSentences("_test");
        Random rn = new Random();
        for (Language lang: testData.keySet()) {
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
