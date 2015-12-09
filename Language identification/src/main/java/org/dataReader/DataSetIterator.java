package org.dataReader;

import org.apache.commons.math3.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.util.FilePaths;
import org.util.MapCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Author: Martina Marek
 *
 * Data iterator for the RNN.
 *
 * source: CharacterIterator from DeepLearning4j: http://deeplearning4j.org/recurrentnetwork.html, modified to fit our data
 * and our model
 */
public class DataSetIterator {

    private String[] languages;
    private HashMap<String, Integer> languageToIndMap;
    private char[] uniqueChars;
    private HashMap<Character, Integer> charToIndMap;
    private int exampleLength;
    private int miniBatchSize;
    private int numExamplesToFetch;
    private int examplesSoFar = 0;
    private int currLanguageInd = 0;
    private Random rng;
    private HashMap<String, ArrayList<String>> trainingData;
    private HashMap<String, ArrayList<String>> devData;



    public DataSetIterator (int exampleLength, int miniBatchSize, int numExamplesToFetch, Random rng, String trainFilename, String testFilename, String group) {
        this.exampleLength = exampleLength;
        this.miniBatchSize = miniBatchSize;
        this.numExamplesToFetch = numExamplesToFetch;
        this.rng = rng;
        initializeUniqueCharMap(trainFilename, group);
        uniqueChars = new char[charToIndMap.size()];
        for (char c: charToIndMap.keySet()) uniqueChars[charToIndMap.get(c)] = c;
        DSLReader reader = new DSLReader();
        trainingData = reader.readInData(trainFilename, group);
        devData = reader.readInData(testFilename, group);
        languages = new String[trainingData.keySet().size()];
        int i = 0;
        for (String lang: trainingData.keySet()) {
            languages[i] = lang;
            i++;
        }
        languageToIndMap = new HashMap<>();
        for (int j = 0; j < languages.length; j++) languageToIndMap.put(languages[j], j);
    }

    private void initializeUniqueCharMap(String filename, String group) {
        MapCreator createUniqueChars = new MapCreator();
        charToIndMap = createUniqueChars.createUniqueCharacterMap(filename, group);
    }

    private void initializeUniqueCharMap() {
        MapCreator createUniqueChars = new MapCreator();
        String[] filenames = new String[languages.length];
        for (int i = 0; i < languages.length; i++) filenames[i] = languages[i] + ".txt";
        charToIndMap = createUniqueChars.createUniqueCharacterMap(FilePaths.DATA_PATH, languages);
    }

    public DataSet next() {
        return next(miniBatchSize);
    }

    /**
     * Fetches next example. Examples are created randomly for each language and uniformly over languages.
     *
     * @param num
     * @return next input for the RNN
     */
    public DataSet next(int num) {
        if (examplesSoFar + num > numExamplesToFetch) throw new NoSuchElementException();
        //Allocate space:
        INDArray input = Nd4j.zeros(new int[]{num, uniqueChars.length, exampleLength});
        INDArray labels = Nd4j.zeros(new int[]{num, languages.length, exampleLength});

        for (int i = 0; i < num; i++) {
            labels.putRow(i, getLanguageVector(currLanguageInd, exampleLength));
            String sentence = getRandomSentence(getLanguageByIndex(currLanguageInd), "train").getSecond();
            input.putRow(i, getFeatureVector(sentence, exampleLength));
            incrementLangaugeInd(); //just take one example of each language, then move on to next language
        }

        examplesSoFar += num;
        return new DataSet(input,labels);
    }

    /**
     * Creates a feature vector for a given sentence.
     *
     * @param sentence
     * @param exampleLength
     * @return feature vector
     */
    public INDArray getFeatureVector(String sentence, int exampleLength) {
        INDArray features  = Nd4j.zeros(new int[]{1, uniqueChars.length, exampleLength});
        for (int j = 0; j < exampleLength; j++) {
            char nextChar;
            if (j >= sentence.length()) nextChar = ' ';
            else nextChar = sentence.charAt(j);
            int charInd = getCharIndex(nextChar);
            if (charInd == -1) charInd = getCharIndex(' ');
            features.putScalar(new int[]{0,charInd,j}, 1.0);
        }
        return features;
    }

    /**
     *
     * @param language
     * @param exampleLength
     * @return label vector
     */
    public INDArray getLanguageVector(String language, int exampleLength) {
        return getLanguageVector(getLanguageIndex(language), exampleLength);
    }

    /**
     *
     * @param indexLang
     * @param exampleLength
     * @return label vector
     */
    public INDArray getLanguageVector(int indexLang, int exampleLength) {
        INDArray labels = Nd4j.zeros(new int[]{1, languages.length, exampleLength});
        for (int j = 0; j < exampleLength; j++) {
                labels.putScalar(new int[]{0,indexLang,j}, 1.0);
        }
        return labels;
    }

    private void incrementLangaugeInd() {
        if (currLanguageInd < languages.length - 1) currLanguageInd++;
        else currLanguageInd = 0;
    }

    public int getLanguageIndex (String language) {return languageToIndMap.get(language);}

    public String getLanguageByIndex (int index) {return languages[index];}

    public int getCharIndex (Character c) {
        if (!charToIndMap.containsKey(c)) return -1;
        return charToIndMap.get(c);
    }

    public Character getCharByIndex (int index) {return uniqueChars[index];}

    public int inputColumns() {
        return uniqueChars.length;
    }

    public int totalOutcomes() {
        return languages.length;
    }

    public void reset() {
        examplesSoFar = 0;
    }


    /**
     * Returns a random sentence from the specified data set
     *
     * @param dataTyp: either "train" or "test", depending which data set should be used
     * @return randomly selected sentence
     */
    public Pair<String, String> getRandomSentence(String dataTyp) {
        int randLang = rng.nextInt(languages.length);
        String lang = languages[randLang];
        return getRandomSentence(lang, dataTyp);
    }

    /**
     * Returns a random sentence from the specified language
     *
     * @param language
     * @param dataTyp: either "train" or "test", depending which data set should be used
     * @return randomly selected sentence
     */
    public Pair<String, String> getRandomSentence(String language, String dataTyp) {
        HashMap<String, ArrayList<String>> data;
        if (dataTyp.equals("train")) data = trainingData;
        else data = devData;
        ArrayList<String> list = data.get(language);
        int randSentence = rng.nextInt(list.size());
        String sent = list.get(randSentence);
        return new Pair<String, String>(language, sent);
    }

}
