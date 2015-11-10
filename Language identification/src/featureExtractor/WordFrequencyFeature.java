package featureExtractor;

import util.Language;

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

                //if the value is one of the invalid characters, remove
                if (INVALID_CHARACTERS.contains(tempword) || tempword.matches("[0-9]+")) {
                    words.remove(tempword);
                }

                for (int k = 0; k < INVALID_CHARACTERS.length(); k++) {
                    //check if any of the invalid characters exist within the word; this can be changed to check for only for '.' and ','
                    if (tempword.contains(String.valueOf(INVALID_CHARACTERS.charAt(k)))) {
                        ArrayList<String> temp_words = new ArrayList<String>(Arrays.asList(tempword.split(String.valueOf(INVALID_CHARACTERS.charAt(k)))));
                        for (int i = 0; i < temp_words.size(); i++) {
                            if (!INVALID_CHARACTERS.contains(temp_words.get(i)) && !temp_words.get(i).matches("[0-9]+")) {
                                words.add(temp_words.get(i));
                            }
                        }
                        words.remove(tempword);
                    }

                }
            }

            hmap.put(new Language(lang), words);  // add to hash map
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
        return hmap;
    }
}
