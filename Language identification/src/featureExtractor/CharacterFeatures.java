package featureExtractor;

import java.util.*;

/**
 * Created by martina on 11/20/15.
 */
public class CharacterFeatures {

    public static void main( String[] args ) {
        String[] chars = {"a", "A", "á", "カ","毎","日"};
        for (int i = 0; i < chars.length; i++) {
            System.out.println("Char: " +chars[i] + " in Unicode: " + convertFromUnicodeToInt(chars[i]));
        }
    }


    /** Converts a char to its unicode number
     *
     * @param c: char to convert
     * @return unicode number
     */
    static public int convertFromUnicodeToInt (char c) {
        return (int) c;
    }

    /** Converts the first char of a string to its unicode number
     *
     * @param s: string for conversion, should have length 1
     * @return unicode number
     */
    static public int convertFromUnicodeToInt (String s) {
        return convertFromUnicodeToInt(s.charAt(0));
    }

}
