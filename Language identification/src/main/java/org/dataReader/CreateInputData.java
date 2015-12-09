package org.dataReader;

/**
 * Created by May on 11/10/15.
 */

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.ZipFile;

public class CreateInputData {

    static String[] filenames = {"abk",  "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"cho" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo","ell" ,"eml" ,"eng" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin" ,"fra" ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb" ,"her" ,"hil", "hin","hrv" ,"hun" ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa" ,"mus" ,"mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new" ,"nld" ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms" ,"pol" ,"por" ,"ppl" ,"pus" ,"quc" ,"que" ,"roh" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som" ,"spa" ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa" ,"swe" ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };
    //static String[] filenames = {"abk"};
    static final String INVALID_CHARACTERS = ".,;:!%- 0123456789";
    static final String path = "data/";
    static final String extension = ".txt";
    static final String zipExtension = ".txt.zip";

    public static final String[] DATA_TYPES = {"_train", "_test", "_dev"};

    public static void main(String []args) {
        CreateInputData.createInput("_test");

    }

    static private BufferedReader getLangReader(String langCode, String dataType) throws IllegalArgumentException {
        if (!Arrays.asList(DATA_TYPES).contains(dataType)) {
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

    /*
    * Creates input for the type of data mentioned (Train, test or Dev)
    * INPUT:_train, _test, _dev
     */
    static void createInput(String dataType) {
    // Read a sentence from a file and write out to a random filename. Create 300 files with random input data (labeled)

        String output_filename = "input" + dataType;
        String sCurrentLine;
        BufferedWriter bw;

        //Read from the input files :
        for(String lang : filenames) {
            BufferedReader br = getLangReader(lang, dataType);

            if (br == null) {
                // no .txt or .txt.zip found
                System.out.println("Language file for '" + lang + dataType + "' could not be opened, skipping");
                continue;
            }

            try {
                while ((sCurrentLine = br.readLine()) != null) {

                    sCurrentLine = clean_up_sentence(sCurrentLine);
                    if(sCurrentLine == "") continue;

                    int randInt = new Random().nextInt(300);
                    String out = "data/input/" + output_filename + String.valueOf(randInt);
                    bw = new BufferedWriter(new FileWriter(out, true));
                    bw.write(lang + ": ");
                    bw.write(sCurrentLine);
                    bw.write("\n");
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    static private String clean_up_sentence(String sentence)
    {

        //if the value is one of the invalid characters, remove
        String editSentence = "";
        for (int cindex = 0; cindex < sentence.length(); cindex++) {
            if (!INVALID_CHARACTERS.contains(String.valueOf(sentence.charAt(cindex)))) {
                editSentence += sentence.charAt(cindex);
            }
        }

        return editSentence;
    }



}


