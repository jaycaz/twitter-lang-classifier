/**
 * Created by May on 10/20/15.
 */
package main.java.org.dataReader;
import main.java.org.util.FilePaths;
import main.java.org.util.Language;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipFile;
/*
This Class provides methods to read in data.
 */

public class ReadData {

    static String[] filenames = {"abk", "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym", "aze", "bam", "bel", "ben", "bih", "bis", "bos", "bpy", "bre", "bug", "bul", "cak", "cat", "cco", "ceb", "ces", "cha", "che", "chr", "chv", "ckb", "cor", "cos", "crh", "cym", "dan", "deu", "div", "dzo", "ell", "eml", "eng", "epo", "est", "eus", "ewe", "fao", "fas", "fij", "fin", "fra", "frp", "fry", "ful", "gla", "gle", "glg", "glv", "grn", "guj", "hat", "hau", "haw", "heb", "hil", "hin", "hrv", "hun", "hye", "ibo", "iku", "ilo", "ind", "isl", "jac", "jav", "jpn", "kab", "kal", "kan", "kat", "kaz", "kek", "khm", "kik", "kin", "kir", "kom", "kor", "kur", "lad", "lao", "lat", "lav", "lez", "lij", "lin", "lit", "lmo", "ltz", "lug", "mal", "mam", "mar", "min", "mkd", "mlg", "mlt", "mon", "mri", "msa", "mya", "mzn", "nah", "nap", "nav", "ndo", "nds", "nep", "new", "nld", "nno", "nob", "nor", "nya", "oci", "ori", "orm", "pam", "pan", "pdc", "pdt", "pms", "pol", "por", "ppl", "pus", "quc", "que", "roh", "ron", "rus", "scn", "sco", "sin", "slk", "slv", "sme", "smo", "sna", "snd", "som", "spa", "sqi", "srd", "srp", "sun", "swa", "swe", "tah", "tam", "tat", "tel", "tgk", "tgl", "tha", "tir", "ton", "tpi", "tsn", "tum", "tur", "twi", "udm", "uig", "ukr", "urd", "usp", "uzb", "vec", "ven", "vie", "vol", "war", "wln", "wol", "xal", "xho", "yid", "yor", "zh-yue", "zha", "zho", "zul"};

    static final String INVALID_CHARACTERS = ".,;:!%#|{}()&^%$@?+=”•’»[]_*+\\/-\"…–—“„0123456789'";
    public static final String DATA_PATH = FilePaths.DATA_PATH;
    public static final String EXTENSION = ".txt";
    public static final String ZIP_EXTENSION = ".txt.zip";
    static int num_paragraphs = 0, maxParagraphs = 10000;

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
        System.out.println(filePath);
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

    public static BufferedReader getLangReader(String langCode) throws IllegalArgumentException, IOException {
        return getLangReader(langCode, "");
    }

    /**
     * Gets a BufferedReader pointing to open language file
     *
     * @param langCode The code for the language requested
     * @param dataType One of the values {"_train", "_test", "_dev", ""} - this will be added to the filename being read
     * @return open BufferedReader for language file, or null if an error occurred
     */
    public static BufferedReader getLangReader(String langCode, String dataType) throws IllegalArgumentException, IOException {
        if (!Arrays.asList(DATA_TYPES).contains(dataType)) {
            throw new IllegalArgumentException("Invalid dataType '" + dataType + "', choices are " + Arrays.toString(DATA_TYPES));
        }

        // Look for .txt or .txt.zip files for language
        String fileName = langCode + dataType + EXTENSION;
        Path filePath = getLangPath(langCode, dataType);

        String zipFileName = langCode + dataType + ZIP_EXTENSION;
        Path zipFilePath = getLangZipPath(langCode, dataType);

        // Open .txt file if exists, otherwise look for .txt.zip and unzip
        BufferedReader br = null;
        if (filePath.toFile().exists()) {
            // .txt file found
            try {
                FileInputStream input = new FileInputStream(filePath.toFile());
                br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            } catch (FileNotFoundException e) {
                System.out.println("Txt matcher found file " + fileName + ", but could not open");
                throw e;
            }
        } else if (zipFilePath.toFile().exists()) {
            try {
                // no .txt file, look for .txt.zip
                ZipFile zip = new ZipFile(zipFilePath.toString());
                InputStream input = zip.getInputStream(zip.getEntry(fileName));
                br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            } catch (FileNotFoundException e) {
                System.out.println("Zip matcher found file " + zipFileName + ", but could not open");
                throw e;
            } catch (IOException e) {
                System.out.println("Could not retrieve entry '" + fileName + "' in zip file '" + zipFileName + "'");
                throw e;
            }
        } else {
            // File could not be found
            throw new FileNotFoundException("Could not find files '" + fileName + "' or '" +
                    zipFileName + "' for langugage '" + langCode + "'");
        }
        return br;
    }

    /**
     * Gets a hashmap of sentences for every language
     * Input: DataType: One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read.
     * Output: Hashmap of String , Arraylist<String>, containing the input sentences (MAX_Number allowed).
     */
    public static HashMap<String, ArrayList<String>> getInputSentences(String dataType) {

        HashMap<String, ArrayList<String>> hmap = new HashMap<String, ArrayList<String>>();

        for (String lang : filenames) {
            BufferedReader br;
            try {
                br = getLangReader(lang, dataType);
            } catch (IOException e) {
                // no .txt or .txt.zip found
                System.out.println("Language file for '" + lang + dataType + "' could not be opened, skipping");
                continue;
            }

            try {

                String sCurrentLine;
                ArrayList<String> sentences = new ArrayList<String>();

                while ((sCurrentLine = br.readLine()) != null) {
                    //for each line read

                    if (sCurrentLine.contains("<") || sCurrentLine.contains(">")) continue;
                    if (num_paragraphs++ > maxParagraphs) break;

                    String tempsentence = sCurrentLine;
                    //if the value is one of the invalid characters, remove
                    String editSentence = "";
                    for (int cindex = 0; cindex < sCurrentLine.length(); cindex++) {
                        if (!INVALID_CHARACTERS.contains(String.valueOf(sCurrentLine.charAt(cindex)))) {
                            editSentence += sCurrentLine.charAt(cindex);

                        }
                    }

                    if (editSentence != "") {
                        sentences.add(editSentence);           // Add to the list of sentences
                    } else {
                        num_paragraphs--;
                    }
                }

                num_paragraphs = 0;
                hmap.put(new Language(lang).getName(), sentences);  // add to hash map
                br.close();
            } catch (Exception e) {
                System.out.println(e.fillInStackTrace());
            }
        }

        return hmap;

    }

    /**
     * Gets a hashmap of sentences for a single language
     * Input: DataType: One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read; Name of the language
     * Output: Hashmap of String , Arraylist<String>, containing the input sentences (MAX_Number allowed).
     */
    public HashMap<Language, ArrayList<String>> getLanguageData(String dataType, String lang) {

        HashMap<Language, ArrayList<String>> hmap = new HashMap<Language, ArrayList<String>>();

        BufferedReader br;
        try {
            br = getLangReader(lang, dataType);
        } catch (IOException e) {
            // no .txt or .txt.zip found
            System.out.println("Language file for '" + lang + dataType + "' could not be opened, returning null");
            return null;
        }

        try {

            String sCurrentLine;
            ArrayList<String> sentences = new ArrayList<String>();
            num_paragraphs = 0;
            while ((sCurrentLine = br.readLine()) != null) {

                //for each line read
                if (num_paragraphs++ > maxParagraphs) break;

                //if the value is one of the invalid characters, remove
                String editSentence = "";
                for (int cindex = 0; cindex < sCurrentLine.length(); cindex++) {
                    if (!INVALID_CHARACTERS.contains(String.valueOf(sCurrentLine.charAt(cindex)))) {
                        editSentence += sCurrentLine.charAt(cindex);
                    }
                }

                if (editSentence != "") {
                    sentences.add(editSentence);           // Add to the list of sentences
                }
            }

            num_paragraphs = 0;
            hmap.put(new Language(lang), sentences);  // add to hash map
            br.close();
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }

        return hmap;

    }

    /**
     * Gets the next chunk of data from the file being read. Calling this in a loop would eventually read the complete data from all files.
     * Input: DataType: One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read; Name of the language
     * Output: Hashmap of String , Arraylist<String>, containing the input sentences of current language (MAX_Number allowed).
     */
    private BufferedReader br_chunk;
    public HashMap<Language, ArrayList<String>> getNextChunk(String dataType, String language) {
        HashMap<Language, ArrayList<String>> hmap = new HashMap<Language, ArrayList<String>>();
        int max = 500;

        try {
            //check if there is a file open. if not, open the file.
            if (br_chunk == null)
                br_chunk = getLangReader(language, dataType);
        } catch (IOException e) {
            // no .txt or .txt.zip found
            System.out.println("Language file for '" + language + dataType + "' could not be opened, returning null");
            return null;
        }

        try {

            String sCurrentLine;

            ArrayList<String> sentences = new ArrayList<String>();
            int count = 0;
            while ((sCurrentLine = br_chunk.readLine()) != null && count++ < max) {

                if (sCurrentLine.contains("<") || sCurrentLine.contains(">")) {
                    System.out.println("Removing line " + language);
                    continue;
                }

                String tempsentence = sCurrentLine;
                //if the value is one of the invalid characters, remove
                String editSentence = "";
                for (int cindex = 0; cindex < sCurrentLine.length(); cindex++) {
                    if (!INVALID_CHARACTERS.contains(String.valueOf(sCurrentLine.charAt(cindex)))) {
                        editSentence += sCurrentLine.charAt(cindex);

                    }
                }

                if (editSentence != "") {
                    sentences.add(editSentence);           // Add to the list of sentences
                }
            }

            hmap.put(new Language(language), sentences);  // add to hash map
            if (sCurrentLine == null && sentences.size() == 0) {
                //Check if the file has been completed, if so, close the reader and return null.
                br_chunk.close();
                br_chunk = null;
                return null;
            }

        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }

        return hmap;
    }

    /**
     * Gets the next chunk of data from the filename provided.
     * Input: File name to be read.
     * Output: Hashmap of String , Arraylist<String>, containing the input sentences of current language (MAX_Number allowed).
     */

    private BufferedReader brt;
    public HashMap<String, ArrayList<String>> getNextTweets(String filename) {
        HashMap<String, ArrayList<String>> hmap = new HashMap<String, ArrayList<String>>();
        int max = 500;

        try {
            if (brt == null)
                brt = new BufferedReader(new FileReader(filename));
        } catch (IOException e) {
            // no .txt or .txt.zip found
            System.out.println("' could not be opened, returning null");
            return null;
        }

        try {

            String line;
            ArrayList<String> sentences = new ArrayList<String>();
            int count = 0;
            String pLabel = null, label = null;
            while ((line = brt.readLine()) != null && count++ < max) {

                String[] words = line.split(" ");
                label = words[0];
                if (label.contains("und")) continue;

                String newLine = "";
                for (int i = 1; i < words.length; i++) {

                    if (words[i].contains("http://") || words[i].contains("@") || words[i].contains("#"))
                        continue;
                    String editWord = "";
                    //clean up the word
                    for (int cindex = 0; cindex < words[i].length(); cindex++) {
                        if (!INVALID_CHARACTERS.contains(String.valueOf(words[i].charAt(cindex))))
                            editWord += words[i].charAt(cindex);
                    }

                    //add the word to the line, if it is not empty.
                    if (editWord != "") {
                        newLine += " " + editWord;

                    }

                }

                //check if the label is already present in the hashmap, if so update it. If not, add.
                boolean found = false;
                for (String keyLabel : hmap.keySet()) {
                    if (label.equals(keyLabel)) {
                        ArrayList<String> list = hmap.get(keyLabel);
                        list.add(newLine);
                        hmap.put(label, list);
                        found = true;
                    }
                }
                if (!found) {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(newLine);
                    hmap.put(label, list);
                }

                sentences.add(newLine);
                pLabel = label;

            }


            if (line == null && sentences.size() == 0) {
                brt.close();
                brt = null;
                return null;
            }

        } catch (IOException e) {
            System.out.println(e.fillInStackTrace());
        }

        return hmap;
    }

}
