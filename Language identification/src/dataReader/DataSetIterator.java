package dataReader;

import org.apache.commons.math3.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import util.FilePaths;
import util.Language;
import util.MapCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Author: Martina Marek
 *
 * source: CharacterIterator from DeepLearning4j: http://deeplearning4j.org/recurrentnetwork.html, modified to fit our data
 * and our model
 */
public class DataSetIterator {

    //private String[] LANGUAGES = {"afr", "bre", "bug", "cak", "ces",  "deu", "eng", "fin", "fra", "swe"};
    private String[] languageList = {"afr", "deu"};
    //private String[] LANGUAGES = {"abk", "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo","ell" ,"eml" ,"eng" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin" ,"fra" ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb", "hil", "hin","hrv" ,"hun" ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa","mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new" ,"nld" ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms" ,"pol" ,"por" ,"ppl" ,"pus" ,"quc" ,"que" ,"roh" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som" ,"spa" ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa" ,"swe" ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };
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


    public DataSetIterator (int exampleLength, int miniBatchSize, int numExamplesToFetch, Random rng) {
        this.exampleLength = exampleLength;
        this.miniBatchSize = miniBatchSize;
        this.numExamplesToFetch = numExamplesToFetch;
        this.rng = rng;
        languageToIndMap = new HashMap<>();
        languages = new String[languageList.length];
        for (int i = 0; i < languageList.length; i++) {
            languages[i] = languageList[i];
            languageToIndMap.put(languages[i], i);
        }
        initializeUniqueCharMap();
        uniqueChars = new char[charToIndMap.size()];
        for (char c: charToIndMap.keySet()) uniqueChars[charToIndMap.get(c)] = c;
        ReadData reader = new ReadData();
        trainingData = reader.getInputSentences("_train");
        devData = reader.getInputSentences("_dev");
    }

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


    //public boolean hasNext() {
       // return examplesSoFar + miniBatchSize <= numExamplesToFetch;
    //}

    public DataSet next() {
        return next(miniBatchSize);
    }

    public DataSet next(int num) {
        if (examplesSoFar + num > numExamplesToFetch) throw new NoSuchElementException();
        //Allocate space:
        INDArray input = Nd4j.zeros(new int[]{num, uniqueChars.length, exampleLength});
        INDArray labels = Nd4j.zeros(new int[]{num, languages.length, exampleLength});

        for (int i = 0; i < num; i++) {
            //labels.putScalar(new int[]{i, currLanguageInd, exampleLength - 1}, 1.0);
            labels.putRow(i, getLanguageVector(currLanguageInd, exampleLength));
            String sentence = getRandomSentence(getLanguageByIndex(currLanguageInd), "train").getSecond();
            input.putRow(i, getFeatureVector(sentence, exampleLength));
            incrementLangaugeInd(); //just take one example of each language, then move on to next language
        }

        examplesSoFar += num;
        return new DataSet(input,labels);
    }

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

    public INDArray getLanguageVector(String language, int exampleLength) {
        return getLanguageVector(getLanguageIndex(language), exampleLength);
    }

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
