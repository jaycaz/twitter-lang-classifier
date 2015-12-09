package org.featureExtractor;

import org.util.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by May on 11/9/15.
 */
public class WordFrequencyFeature {

    static final String INVALID_CHARACTERS = ".,;:!%- ";
    HashMap<Language, ArrayList<String>> hmap;
    public HashMap<Language, ArrayList<String>> extractWords(String sCurrentLine, String lang) {
    /*
    INPUT: A sentence and it's language as a string
    OUTPUT: returns a hashmap of arraylist of string and language.
     */

        try {

            hmap = new HashMap<Language, ArrayList<String>>();
            ArrayList<String> words = null;
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
            }

            hmap.put(new Language(lang), words);  // add to hash map
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
        return hmap;
    }
}
