/**
 * Created by May on 10/20/15.
 */
package dataReader;

import util.Language;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipFile;


public class ReadData {


    static String[] filenames = {"abk", "afr", "aka"}; //, "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"cho" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo","ell" ,"eml" ,"eng" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin" ,"fra" ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb" ,"her" ,"hil", "hin","hrv" ,"hun" ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa" ,"mus" ,"mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new" ,"nld" ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms" ,"pol" ,"por" ,"ppl" ,"pus" ,"quc" ,"que" ,"roh" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som" ,"spa" ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa" ,"swe" ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };

    static final String INVALID_CHARACTERS = ".,;:!%- ";
    static final String path = "data/";
    static final String extension = ".txt";
    static final String zipExtension = ".txt.zip";
    static int num_paragraphs = 0, maxParagraphs = 1000;

    public HashMap<Language, ArrayList<ArrayList<String>>> getInputMap(String dataType) {
//INPUT: dataType: One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read.

        HashMap<Language, ArrayList<ArrayList<String>>> hmap = new HashMap<Language, ArrayList<ArrayList<String>>>();
        File f = new File(path);

        PathMatcher txtMatcher = FileSystems.getDefault().getPathMatcher("glob:" + path + "*" + extension);
        PathMatcher zipMatcher = FileSystems.getDefault().getPathMatcher("glob:" + path + "*" + zipExtension);

        // Find all .txt files and all .txt.zip files in data/ directory
        String[] tfiles = f.list((dir, name) -> {
            return txtMatcher.matches(FileSystems.getDefault().getPath(dir.toString(), name));
        });
        String[] zfiles = f.list((dir, name) -> {
            return zipMatcher.matches(FileSystems.getDefault().getPath(dir.toString(), name));
        });
        List<String> txtFiles = new ArrayList<String>(Arrays.asList(tfiles));
        List<String> zipFiles = new ArrayList<String>(Arrays.asList(zfiles));
        txtFiles.sort((s1, s2) -> s1.compareTo(s2));
        zipFiles.sort((s1, s2) -> s1.compareTo(s2));

        for(String lang : filenames) {
            String fileName = lang + dataType + extension;
            Path filePath = FileSystems.getDefault().getPath(path, fileName);
            String zipFileName = lang +dataType + zipExtension;
            Path zipFilePath = FileSystems.getDefault().getPath(path, zipFileName);

            // Open .txt file if exists, otherwise look for .txt.zip and unzip
            BufferedReader br = null;
            if(Collections.binarySearch(txtFiles, fileName) >= 0) {
                // .txt file found
                try {
                    br = new BufferedReader(new FileReader(filePath.toString()));
                    System.out.println(filePath + " found, reading");
                }
                catch (FileNotFoundException e) {
                    // TODO: This line may be unnecessary b/c the file is already being checked
                    System.out.println("Error: could not find file " + filePath);
                }
            }
            else if (Collections.binarySearch(zipFiles, zipFileName) >= 0) {
                // no .txt file, look for .txt.zip
                try {
                    ZipFile zip = new ZipFile(zipFilePath.toString());
                    InputStream input = zip.getInputStream(zip.getEntry(fileName));
                    br = new BufferedReader(new InputStreamReader(input));
                    System.out.println(zipFileName + " found, reading");
                }
                catch (FileNotFoundException e) {
                    System.out.println("Could not find file '" + zipFilePath.toString() + "'");
                    e.printStackTrace();
                }
                catch (IOException e) {
                    System.out.println("Could not retrieve entry '" + fileName + "' in zip file '" + zipFileName + "'");
                    e.printStackTrace();
                }
            }
            else {
                // no .txt or .txt.zip
                System.out.println("Language file for '" + lang + "' could not be found, skipping");
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

                        }
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


    //Returns a hashmap of sentences and
    public HashMap<Language, ArrayList<String>> getInputSentences(String dataType) {
        //INPUT: dataType: One of the values {"_train", "_test", "_dev"} - this will be added to the filename being read.

        HashMap<Language, ArrayList<String>> hmap = new HashMap<Language, ArrayList<String>>();
        File f = new File(path);

        PathMatcher txtMatcher = FileSystems.getDefault().getPathMatcher("glob:" + path + "*" + extension);
        PathMatcher zipMatcher = FileSystems.getDefault().getPathMatcher("glob:" + path + "*" + zipExtension);

        // Find all .txt files and all .txt.zip files in data/ directory
        String[] tfiles = f.list((dir, name) -> {
            return txtMatcher.matches(FileSystems.getDefault().getPath(dir.toString(), name));
        });
        String[] zfiles = f.list((dir, name) -> {
            return zipMatcher.matches(FileSystems.getDefault().getPath(dir.toString(), name));
        });
        List<String> txtFiles = new ArrayList<String>(Arrays.asList(tfiles));
        List<String> zipFiles = new ArrayList<String>(Arrays.asList(zfiles));
        txtFiles.sort((s1, s2) -> s1.compareTo(s2));
        zipFiles.sort((s1, s2) -> s1.compareTo(s2));

        for(String lang : filenames) {
            String fileName = lang + dataType + extension;
            Path filePath = FileSystems.getDefault().getPath(path, fileName);
            String zipFileName = lang +dataType + zipExtension;
            Path zipFilePath = FileSystems.getDefault().getPath(path, zipFileName);

            // Open .txt file if exists, otherwise look for .txt.zip and unzip
            BufferedReader br = null;
            if(Collections.binarySearch(txtFiles, fileName) >= 0) {
                // .txt file found
                try {
                    br = new BufferedReader(new FileReader(filePath.toString()));
                    System.out.println(filePath + " found, reading");
                }
                catch (FileNotFoundException e) {
                    // TODO: This line may be unnecessary b/c the file is already being checked
                    System.out.println("Error: could not find file " + filePath);
                }
            }
            else if (Collections.binarySearch(zipFiles, zipFileName) >= 0) {
                // no .txt file, look for .txt.zip
                try {
                    ZipFile zip = new ZipFile(zipFilePath.toString());
                    InputStream input = zip.getInputStream(zip.getEntry(fileName));
                    br = new BufferedReader(new InputStreamReader(input));
                    System.out.println(zipFileName + " found, reading");
                }
                catch (FileNotFoundException e) {
                    System.out.println("Could not find file '" + zipFilePath.toString() + "'");
                    e.printStackTrace();
                }
                catch (IOException e) {
                    System.out.println("Could not retrieve entry '" + fileName + "' in zip file '" + zipFileName + "'");
                    e.printStackTrace();
                }
            }
            else {
                // no .txt or .txt.zip
                System.out.println("Language file for '" + lang + "' could not be found, skipping");
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
