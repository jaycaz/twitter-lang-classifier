/**
 * Created by May on 10/20/15.
 */
package dataReader;

import util.FilePaths;
import util.Language;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipFile;


public class ReadData {

    //static String[] filenames = {"abk", "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo","ell" ,"eml" ,"eng" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin" ,"fra" ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb", "hil", "hin","hrv" ,"hun" ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa","mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new" ,"nld" ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms" ,"pol" ,"por" ,"ppl" ,"pus" ,"quc" ,"que" ,"roh" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som" ,"spa" ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa" ,"swe" ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };
    static private String[] filenames = {"afr", "deu", "fra"};

    //static final String INVALID_CHARACTERS = ".,;:!%-0123456789'";
    static final String INVALID_CHARACTERS = ".,;:!%#|{}()&^%$@?+=”•’»[]_*+\\/-\"…–—“„0123456789'";
    public static final String DATA_PATH = FilePaths.DATA_PATH;
    public static final String EXTENSION = ".txt";
    public static final String ZIP_EXTENSION = ".txt.zip";
    static int num_paragraphs = 0, maxParagraphs = 100;

    public static final String TRAIN = "_train";
    public static final String TEST = "_test";
    public static final String DEV = "_dev";
    public static final String[] DATA_TYPES = {TRAIN, TEST, DEV, ""};

    public static Path getLangPath(String langCode) {
        return getLangPath(langCode, "");
    }

    public static Path getLangPath(String langCode, String dataType) {
        String fileName = langCode + dataType + EXTENSION;
        Path filePath = FileSystems.getDefault().getPath(DATA_PATH, fileName).toAbsolutePath();
        return filePath;
    }

    public static Path getLangZipPath(String langCode) {
        return getLangZipPath(langCode, "");
    }

    public static Path getLangZipPath(String langCode, String dataType) {
        String zipFileName = langCode + dataType + ZIP_EXTENSION;
        Path zipFilePath = FileSystems.getDefault().getPath(DATA_PATH, zipFileName).toAbsolutePath();
        return zipFilePath;
    }


    // TODO: Make all methods into static methods?
    public BufferedReader getLangReader(String langCode) throws IllegalArgumentException, IOException {
        return getLangReader(langCode, "");
    }

    /**
     * Gets a BufferedReader pointing to open language file
     * @param langCode The code for the language requested
     * @param dataType One of the values {"_train", "_test", "_dev", ""} - this will be added to the filename being read
     * @return open BufferedReader for language file, or null if an error occurred
     */
    public BufferedReader getLangReader(String langCode, String dataType) throws IllegalArgumentException, IOException {
        if(!Arrays.asList(DATA_TYPES).contains(dataType)) {
            throw new IllegalArgumentException("Invalid dataType '" + dataType + "', choices are " + Arrays.toString(DATA_TYPES));
        }

        // Look for .txt or .txt.zip files for language
        // TODO: fileName and zipFileName are duplicated elsewhere, put into one method?
        String fileName = langCode + dataType + EXTENSION;
        Path filePath = getLangPath(langCode, dataType);
        //PathMatcher txtMatcher = FileSystems.getDefault().getPathMatcher("glob:" + filePath);

        String zipFileName = langCode + dataType + ZIP_EXTENSION;
        Path zipFilePath = getLangZipPath(langCode, dataType);
        //PathMatcher zipMatcher = FileSystems.getDefault().getPathMatcher("glob:" + zipFilePath);


        // Open .txt file if exists, otherwise look for .txt.zip and unzip
        BufferedReader br = null;
        if(filePath.toFile().exists()) {
            // .txt file found
            try {
                FileInputStream input = new FileInputStream(filePath.toFile());
                br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            }
            catch (FileNotFoundException e) {
                // TODO: This line may be unnecessary b/c the file is already being checked
                System.out.println("Txt matcher found file " + fileName + ", but could not open");
                throw e;
            }
        }
        else if (zipFilePath.toFile().exists()) {
            try {
                // no .txt file, look for .txt.zip
                ZipFile zip = new ZipFile(zipFilePath.toString());
                InputStream input = zip.getInputStream(zip.getEntry(fileName));
                br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            }
            catch (FileNotFoundException e) {
                System.out.println("Zip matcher found file " + zipFileName + ", but could not open");
                throw e;
            }
            catch (IOException e) {
                System.out.println("Could not retrieve entry '" + fileName + "' in zip file '" + zipFileName + "'");
                throw e;
            }
        }
        else {
            // File could not be found
            throw new FileNotFoundException("Could not find files '" + fileName + "' or '" +
                    zipFileName + "' for langugage '" + langCode + "'");
        }

        return br;
    }

//    /**
//     * Compiles all words in one language file
//     * @param langCode The language code to use
//     * @param dataType One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read
//     * @return
//     */
//    public ArrayList<String> getInputLangWords(String langCode, String dataType) throws IOException {
//        BufferedReader br = getLangReader(langCode, dataType);
//        ArrayList<String> allWords = new ArrayList<String>();
//
//        if(br == null) {
//            throw new IOException("Language file for '" + langCode + dataType + "' could not be opened");
//        }
//
//        String line = null;
//        while((line = br.readLine()) != null) {
//            allWords.addAll(Arrays.asList(line.split("\\s")));
//        }
//        br.close();
//
//        return allWords;
//    }

//    /**
//     * Gets a list of sentences for each language
//     * @param dataType One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read
//     * @return HashMap containing every sentence from every language document found
//     */
//    public HashMap<Language, ArrayList<ArrayList<String>>> getInputMap(String dataType) {
//        HashMap<Language, ArrayList<ArrayList<String>>> hmap = new HashMap<Language, ArrayList<ArrayList<String>>>();
//
//        BufferedReader br;
//        for(String lang : filenames) {
//            try {
//                br = getLangReader(lang, dataType);
//            }
//            catch (IOException e) {
//                // no .txt or .txt.zip found
//                System.out.println("*** Language file for '" + lang + dataType + "' could not be opened, skipping");
//                continue;
//            }
//
//            Language langObj;
//            try {
//                langObj = new Language(lang);
//            }
//            catch (IllegalArgumentException e) {
//                System.out.println("*** Language code '" + lang + "' is invalid, skipping");
//                continue;
//            }
//
//            try {
//                ArrayList<String> words = null;
//                String sCurrentLine;
//                ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
//
//                while ((sCurrentLine = br.readLine()) != null) {
//
//                    //for each line read , convert into word lists
//                    if (num_paragraphs++ > maxParagraphs) break;
//
//                    words = new ArrayList<String>(Arrays.asList(sCurrentLine.split(" ")));
//
//                    CopyOnWriteArrayList<String> copy_words = new CopyOnWriteArrayList<String>(words);
//                    Iterator<String> it = copy_words.iterator();
//                    //pre-process the array list
//
//                    while (it.hasNext()) {
//                        String tempword = it.next();
//                        //System.out.println(tempword);
//                        //if the value is one of the invalid characters, remove
//                        String editWord = "";
//                        for (int cindex = 0; cindex < tempword.length(); cindex++) {
//                            if (!INVALID_CHARACTERS.contains(String.valueOf(tempword.charAt(cindex)))) {
//                                editWord += tempword.charAt(cindex);
//
//                            }
//                        }
//                        words.remove(tempword);
//                        if(editWord != "") {
//                            words.add(editWord);
//                        }
//   /*
//                        if (INVALID_CHARACTERS.contains(tempword)) {
//                            words.remove(tempword);
//
//                        }
//
//                        for (int k = 0; k < INVALID_CHARACTERS.length(); k++) {
//                            //check if any of the invalid characters exist within the word; this can be changed to check for only for '.' and ','
//                            if (tempword.contains(String.valueOf(INVALID_CHARACTERS.charAt(k)))) {
//                                ArrayList<String> temp_words = new ArrayList<String>(Arrays.asList(tempword.split(String.valueOf(INVALID_CHARACTERS.charAt(k)))));
//                                words.addAll(temp_words);
//                                words.remove(tempword);
//                            }
//
//
//                        }   */
//                    }
//                    sentences.add(words);           // add to list of array lists
//                }
//
//                num_paragraphs = 0;
//                hmap.put(new Language(lang), sentences);  // add to hash map
//                br.close();
//            }
//            catch (Exception e) {
//                System.out.println(e.fillInStackTrace());
//            }
//        }
//
//        return hmap;
//    }


    /**
     * Gets a hashmap of sentences for every language
     */
    public HashMap<Language, ArrayList<String>> getInputSentences(String dataType) {
        //INPUT: dataType: One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read.
        HashMap<Language, ArrayList<String>> hmap = new HashMap<Language, ArrayList<String>>();

        for(String lang : filenames) {

            BufferedReader br;
            try {
                br = getLangReader(lang, dataType);
            }
            catch (IOException e) {
                // no .txt or .txt.zip found
                System.out.println("Language file for '" + lang + dataType + "' could not be opened, skipping");
                continue;
            }

            try {

                String sCurrentLine;
                ArrayList<String> sentences = new ArrayList<String>();

                while ((sCurrentLine = br.readLine()) != null) {

                    //for each line read
                    if (num_paragraphs++ > maxParagraphs) break;

                    String tempsentence = sCurrentLine;
                    //if the value is one of the invalid characters, remove
                    String editSentence = "";
                    for (int cindex = 0; cindex < sCurrentLine.length(); cindex++) {
                        if (!INVALID_CHARACTERS.contains(String.valueOf(sCurrentLine.charAt(cindex)))) {
                            editSentence += sCurrentLine.charAt(cindex);

                        }
                    }

                    if(editSentence != "") {
                        sentences.add(editSentence);           // Add to the list of sentences
                    }
                }

                num_paragraphs = 0;
                hmap.put(new Language(lang), sentences);  // add to hash map
                br.close();
            } catch (Exception e) {
                System.out.println(e.fillInStackTrace());
            }
        }

        return hmap;

    }


//    public HashMap<Language, ArrayList<String>> getLanguageData(String dataType, String lang) {
//        //INPUT: dataType: One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read.
//        HashMap<Language, ArrayList<String>> hmap = new HashMap<Language, ArrayList<String>>();
//
//
//
//        BufferedReader br;
//        try {
//            br = getLangReader(lang, dataType);
//        }
//        catch (IOException e) {
//            // no .txt or .txt.zip found
//            System.out.println("Language file for '" + lang + dataType + "' could not be opened, returning null");
//            return null;
//        }
//
//        try {
//
//            String sCurrentLine;
//            ArrayList<String> sentences = new ArrayList<String>();
//
//            while ((sCurrentLine = br.readLine()) != null) {
//
//                //for each line read
//              //  if (num_paragraphs++ > maxParagraphs) break;
//
//                String tempsentence = sCurrentLine;
//                //if the value is one of the invalid characters, remove
//                String editSentence = "";
//                for (int cindex = 0; cindex < sCurrentLine.length(); cindex++) {
//                    if (!INVALID_CHARACTERS.contains(String.valueOf(sCurrentLine.charAt(cindex)))) {
//                        editSentence += sCurrentLine.charAt(cindex);
//
//                    }
//                }
//
//                if(editSentence != "") {
//                    sentences.add(editSentence);           // Add to the list of sentences
//                }
//            }
//
//
//
//            num_paragraphs = 0;
//            hmap.put(new Language(lang), sentences);  // add to hash map
//            br.close();
//        } catch (Exception e) {
//            System.out.println(e.fillInStackTrace());
//        }
//
//
//    return hmap;
//
//
//
//}

    private BufferedReader br_chunk;
    public HashMap<Language, ArrayList<String>> getNextChunk(String dataType, String language) {
        HashMap<Language, ArrayList<String>> hmap = new HashMap<Language, ArrayList<String>>();
        int max = 500;

        try {
            if(br_chunk == null)
                br_chunk = getLangReader(language, dataType);
        }
        catch (IOException e) {
            // no .txt or .txt.zip found
            System.out.println("Language file for '" + language + dataType + "' could not be opened, returning null");
            return null;
        }

        try {
            String sCurrentLine;
            ArrayList<String> sentences = new ArrayList<String>();
            int count = 0;
            while ((sCurrentLine = br_chunk.readLine()) != null && count++ < max) {
                String tempsentence = sCurrentLine;
                //if the value is one of the invalid characters, remove
                String editSentence = "";
                for (int cindex = 0; cindex < sCurrentLine.length(); cindex++) {
                    if (!INVALID_CHARACTERS.contains(String.valueOf(sCurrentLine.charAt(cindex)))) {
                        editSentence += sCurrentLine.charAt(cindex);

                    }
                }

                if(editSentence != "") {
                    sentences.add(editSentence);           // Add to the list of sentences
                }
            }
            System.out.println("read: " + sentences.size());
            hmap.put(new Language(language), sentences);  // add to hash map
            if(sCurrentLine == null && sentences.size() == 0) {
                br_chunk.close();
                br_chunk = null;
                return null;
            }

        }
        catch(IOException e){
            System.out.println(e.fillInStackTrace());
        }

        return hmap;
    }
}
