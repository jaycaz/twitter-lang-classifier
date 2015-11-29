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
import java.util.Random;

/**
 * Created by martina on 11/27/15.
 *
 * source: CharacterIterator from DeepLearning4j: http://deeplearning4j.org/recurrentnetwork.html
 */
public class DataSetIterator {

    private String[] languages = {"afr", "deu", "fra"};
    //private String[] languages = {"abk", "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo","ell" ,"eml" ,"eng" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin" ,"fra" ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb", "hil", "hin","hrv" ,"hun" ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa","mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new" ,"nld" ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms" ,"pol" ,"por" ,"ppl" ,"pus" ,"quc" ,"que" ,"roh" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som" ,"spa" ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa" ,"swe" ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };
    private HashMap<String, Integer> languageToIndMap;
    private char[] uniqueChars;
    private HashMap<Character, Integer> charToIndMap;
    private int exampleLength;
    private int miniBatchSize;
    private int numExamplesToFetch;
    //private int examplesSoFar = 0;
    private int currLanguageInd = 0;
    private Random rng;
    HashMap<Language, ArrayList<String>> trainingData;

    public DataSetIterator (int exampleLength, int miniBatchSize, int numExamplesToFetch, Random rng) {
        this.exampleLength = exampleLength;
        this.miniBatchSize = miniBatchSize;
        this.numExamplesToFetch = numExamplesToFetch;
        this.rng = rng;
        languageToIndMap = new HashMap<>();
        for (int i = 0; i < languages.length; i++) languageToIndMap.put(languages[i], i);
        MapCreator createUniqueChars = new MapCreator();
        String[] filenames = new String[languages.length];
        for (int i = 0; i < languages.length; i++) filenames[i] = languages[i] + ".txt";
        charToIndMap = createUniqueChars.createUniqueCharacterMap(FilePaths.DATA_PATH, filenames);
        uniqueChars = new char[charToIndMap.size()];
        for (char c: charToIndMap.keySet()) uniqueChars[charToIndMap.get(c)] = c;
        ReadData reader = new ReadData();
        trainingData = reader.getInputSentences("_train");
    }

    //public boolean hasNext() {
       // return examplesSoFar + miniBatchSize <= numExamplesToFetch;
    //}

    public DataSet next() {
        return next(miniBatchSize);
    }

    public DataSet next(int num) {
        //if (examplesSoFar + num > numExamplesToFetch) throw new NoSuchElementException();
        //Allocate space:
        INDArray input = Nd4j.zeros(new int[]{num, uniqueChars.length, exampleLength});
        INDArray labels = Nd4j.zeros(new int[]{num, languages.length, exampleLength});

        for (int i = 0; i < num; i++) {
            Language l = new Language(languages[currLanguageInd]);
            ArrayList<String> list = new ArrayList<>();
            for (Language ll: trainingData.keySet()) {
                if (ll.equals(l)) {
                    list = trainingData.get(ll);
                }
            }
            int randSentence = rng.nextInt(list.size());
            String sentence = list.get(randSentence);
            for (int j = 0; j < exampleLength; j++) {
                char nextChar;
                if (j >= sentence.length()) nextChar = ' ';
                else nextChar = sentence.charAt(j);
                int charInd = getCharIndex(nextChar);
                if (charInd == -1) charInd = getCharIndex(' ');
                input.putScalar(new int[]{i,charInd,j}, 1.0);
                labels.putScalar(new int[]{i,currLanguageInd,j}, 1.0);
            }
            incrementLangaugeInd(); //just take one example of each language, then move on to next language
        }

        //examplesSoFar += num;
        return new DataSet(input,labels);
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

    //public void reset() {
        //examplesSoFar = 0;
    //}

    public Pair<String, String> getRandomSentence() {
        int randLang = rng.nextInt(languages.length);
        String lang = languages[randLang];
        Language l = new Language(lang);
        ArrayList<String> list = new ArrayList<>();
        for (Language ll: trainingData.keySet()) {
            if (ll.equals(l)) {
                list = trainingData.get(ll);
            }
        }
        int randSentence = rng.nextInt(list.size());
        String sent = list.get(randSentence);
        return new Pair<String, String>(lang, sent);
    }

}
