/**
 * Created by May on 10/20/15.
 */
package dataReader;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import util.Language;


public class ReadData {

    static String[] filenames = {"abk", "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"cho" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo"};
    	//,"ell" ,"eml" ,"eng" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin" ,"fra" ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb" ,"her" ,"hil", "hin","hrv" ,"hun" ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa" ,"mus" ,"mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new" ,"nld" ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms" ,"pol" ,"por" ,"ppl" ,"pus" ,"quc" ,"que" ,"rol" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som" ,"spa" ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa" ,"swe" ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };
    //static String[] filenames = {"abk", "asm", "ben", "hin" };
    static final String INVALID_CHARACTERS = ".,;:!%- ";
    static final String Path = "data/";
    static final String extension = ".txt";
    static int num_paragraphs = 0, maxParagraphs = 1000;

    public HashMap getInputMap() {

        HashMap<Language, ArrayList<ArrayList<String>>> hmap = new HashMap<Language, ArrayList<ArrayList<String>>>();

        try {

            for (int i = 0; i < filenames.length; i++) {
                //For Each File
                BufferedReader br = new BufferedReader(new FileReader(Path + filenames[i] + extension));

                ArrayList<String> words = null;
                String sCurrentLine;
                ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();


                while ((sCurrentLine = br.readLine()) != null) {

                    //for each line read , convert into word lists
                    if(num_paragraphs++ > maxParagraphs) break;
                    words = new ArrayList<String>(Arrays.asList(sCurrentLine.split(" ")));

                    CopyOnWriteArrayList<String> copy_words = new CopyOnWriteArrayList<String>(words);
                    Iterator<String> it = copy_words.iterator();
                    //pre-process the array list

                    while(it.hasNext())
                    {
                        String tempword = it.next();
                        System.out.println(tempword);
                        //if the value is one of the invalid characters, remove
                        if(INVALID_CHARACTERS.contains(tempword)) {
                            words.remove(tempword);

                        }

                        for(int k = 0; k < INVALID_CHARACTERS.length(); k++){
                            //check if any of the invalid characters exist within the word; this can be changed to check for only for '.' and ','
                            if(tempword.contains(String.valueOf(INVALID_CHARACTERS.charAt(k)))){
                                ArrayList<String> temp_words = new ArrayList<String>(Arrays.asList(tempword.split(String.valueOf(INVALID_CHARACTERS.charAt(k)))));
                                words.addAll(temp_words);
                                words.remove(tempword);
                            }

                        }
                    }

                    sentences.add(words);           // add to list of array lists

                }




                num_paragraphs = 0;
                hmap.put(new Language(filenames[i]), sentences);  // add to hash map
            }

        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }

        return hmap;
    }


}
