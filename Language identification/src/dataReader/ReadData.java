/**
 * Created by May on 10/20/15.
 */
package dataReader;

import util.Language;

import java.io.*;
import java.io.BufferedReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipFile;


public class ReadData {


    static String[] filenames = {"abk", "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"cho" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo","ell" ,"eml" ,"eng" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin" ,"fra" ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb" ,"her" ,"hil", "hin","hrv" ,"hun" ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa" ,"mus" ,"mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new" ,"nld" ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms" ,"pol" ,"por" ,"ppl" ,"pus" ,"quc" ,"que" ,"roh" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som" ,"spa" ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa" ,"swe" ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };

    static final String INVALID_CHARACTERS = ".,;:!%- ";
    static final String path = "data/";
    static final String extension = ".txt";
    static final String zipExtension = ".txt.zip";
    static int num_paragraphs = 0, maxParagraphs = 1000;

    public static final String[] DATA_TYPES = {"_train", "_test", "_dev"};

    /**
     * Gets a BufferedReader pointing to open language file
     * @param langCode The code for the language requested
     * @param dataType One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read
     * @return open BufferedReader for language file, or null if an error occurred
     */
    private BufferedReader getLangReader(String langCode, String dataType) throws IllegalArgumentException {
        if(!Arrays.asList(DATA_TYPES).contains(dataType)) {
            throw new IllegalArgumentException("Invalid dataType '" + dataType + "', choices are " + Arrays.toString(DATA_TYPES));
        }

        // Look for .txt or .txt.zip files for language
        String fileName = langCode + dataType + extension;
        Path filePath = FileSystems.getDefault().getPath(path, fileName);
        PathMatcher txtMatcher = FileSystems.getDefault().getPathMatcher("glob:" + filePath);

        String zipFileName = langCode + dataType + zipExtension;
        Path zipFilePath = FileSystems.getDefault().getPath(path, zipFileName);
        PathMatcher zipMatcher = FileSystems.getDefault().getPathMatcher("glob:" + zipFilePath);


        // Open .txt file if exists, otherwise look for .txt.zip and unzip
        BufferedReader br = null;
        if(txtMatcher.matches(filePath)) {
            // .txt file found
            try {
                br = new BufferedReader(new FileReader(filePath.toString()));
            }
            catch (FileNotFoundException e) {
                // TODO: This line may be unnecessary b/c the file is already being checked
                System.out.println("Txt matcher found file " + fileName + ", but could not open");
            }
        }
        else if (zipMatcher.matches(zipFilePath)) {
            // no .txt file, look for .txt.zip
            try {
                ZipFile zip = new ZipFile(zipFilePath.toString());
                InputStream input = zip.getInputStream(zip.getEntry(fileName));
                br = new BufferedReader(new InputStreamReader(input));
            }
            catch (FileNotFoundException e) {
                System.out.println("Zip matcher found file " + zipFileName + ", but could not open");
                e.printStackTrace();
            }
            catch (IOException e) {
                System.out.println("Could not retrieve entry '" + fileName + "' in zip file '" + zipFileName + "'");
                e.printStackTrace();
            }
        }

        return br;
    }

    /**
     * Compiles all words in one language file
     * @param langCode The language code to use
     * @param dataType One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read
     * @return
     */
    public ArrayList<String> getInputLangWords(String langCode, String dataType) throws IOException {
        BufferedReader br = getLangReader(langCode, dataType);
        ArrayList<String> allWords = new ArrayList<String>();

        if(br == null) {
            throw new IOException("Language file for '" + langCode + dataType + "' could not be opened");
        }

        String line = null;
        while((line = br.readLine()) != null) {
            allWords.addAll(Arrays.asList(line.split("\\s")));
        }
        br.close();

        return allWords;
    }

    /**
     * Gets a list of sentences for each language
     * @param dataType One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read
     * @return HashMap containing every sentence from every language document found
     */
    public HashMap<Language, ArrayList<ArrayList<String>>> getInputMap(String dataType) {
        HashMap<Language, ArrayList<ArrayList<String>>> hmap = new HashMap<Language, ArrayList<ArrayList<String>>>();

        for(String lang : filenames) {
            BufferedReader br = getLangReader(lang, dataType);
            if (br == null) {
                // no .txt or .txt.zip found
                System.out.println("Language file for '" + lang + dataType + "' could not be opened, skipping");
                continue;
            }

            try {
                ArrayList<String> words = null;
                String sCurrentLine;
                ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();

                while ((sCurrentLine = br.readLine()) != null) {

                    //for each line read , convert into word lists
                    if (num_paragraphs++ > maxParagraphs) break;

                    words = new ArrayList<String>(Arrays.asList(sCurrentLine.split(" ")));

                    CopyOnWriteArrayList<String> copy_words = new CopyOnWriteArrayList<String>(words);
                    Iterator<String> it = copy_words.iterator();
                    //pre-process the array list

                    while (it.hasNext()) {
                        String tempword = it.next();
                        //System.out.println(tempword);
                        //if the value is one of the invalid characters, remove
                        String editWord = "";
                        for (int cindex = 0; cindex < tempword.length(); cindex++) {
                            if (!INVALID_CHARACTERS.contains(String.valueOf(tempword.charAt(cindex)))) {
                                editWord += tempword.charAt(cindex);
                            }
                        }
                        words.remove(tempword);
                        if(editWord != "") {
                            words.add(editWord);
                        }
   /*
                        if (INVALID_CHARACTERS.contains(tempword)) {
                            words.remove(tempword);

                        }

                        for (int k = 0; k < INVALID_CHARACTERS.length(); k++) {
                            //check if any of the invalid characters exist within the word; this can be changed to check for only for '.' and ','
                            if (tempword.contains(String.valueOf(INVALID_CHARACTERS.charAt(k)))) {
                                ArrayList<String> temp_words = new ArrayList<String>(Arrays.asList(tempword.split(String.valueOf(INVALID_CHARACTERS.charAt(k)))));
                                words.addAll(temp_words);
                                words.remove(tempword);
                            }


                        }   */
                    }
                    sentences.add(words);           // add to list of array lists
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


    /**
     * Gets a hashmap of sentences for every language
     */
    public HashMap<Language, ArrayList<String>> getInputSentences(String dataType) {
        //INPUT: dataType: One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read.
        HashMap<Language, ArrayList<String>> hmap = new HashMap<Language, ArrayList<String>>();

        for(String lang : filenames) {

            BufferedReader br = getLangReader(lang, dataType);
            if (br == null) {
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
                    sentences.add(sCurrentLine);           // Add to the list of sentences
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


}
