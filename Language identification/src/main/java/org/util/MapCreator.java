package org.util;

import org.dataReader.DSLReader;
import org.dataReader.ReadData;
import edu.stanford.nlp.stats.IntCounter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: Martina Marek
 *
 *
 */
public class MapCreator {

    static final String INVALID_CHARACTERS = ".,;:!%#|{}()&^%$@?+=”•’»[]_*+\\/-\"…–—“„0123456789'";

    /**
     * Creates a map that maps all unique characters in the given files to an index
     *
     * Takes only characters into account that appear at least 1000 times in a document to sort out noise.
     * Does not take special characters or numbers into account (ignores any character out of this list:
     * ".,;:!%#|{}()&^%$@?+=”•’»[]_*+\\/-\"…–—“„0123456789'").
     *
     * @param path: where the files are located
     * @param files: array list of strings with all filenames
     * @return map that maps each unique character to an index
     */
    public HashMap<Character, Integer> createUniqueCharacterMap (String path, String[] files) {
        IntCounter<Character> counter = new IntCounter<>();
        HashMap<Character, Integer> charMap = new HashMap<Character, Integer>();
        for (String file: files) {
            try {
                BufferedReader reader = ReadData.getLangReader(file);
                String line = null;
                while((line = reader.readLine()) != null) {
                    for (int i = 0; i < line.length(); i++) {
                        if (INVALID_CHARACTERS.indexOf(line.charAt(i)) == -1) {
                            counter.incrementCount(line.charAt(i), 1);
                        }
                    }
                }
                int i = 0;
                for (char c: counter.keySet()) {
                    if (counter.getCount(c) > 1000) {
                        charMap.put(c, i);
                        i++;
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("File " + file + " not found.");
                //throw e;
            } catch (IOException e) {
                System.out.println("IOException with file: " + file);
                //throw e;
            }
        }
        return charMap;
    }

    /**
     * Creates a map that maps all unique characters in a DSL shared task file to an index
     *
     * @param filename of the DSL shared task file
     * @param group: array list of strings with all filenames
     * @return map that maps each unique character to an index
     */
    public HashMap<Character, Integer> createUniqueCharacterMap (String filename, String group) {
        IntCounter<Character> counter = new IntCounter<>();
        HashMap<Character, Integer> charMap = new HashMap<Character, Integer>();
        DSLReader reader = new DSLReader();
        HashMap<String, ArrayList<String>> data = reader.readInData(filename, group);
        int index = 0;
        for (String lang: data.keySet()) {
            for (String sentence: data.get(lang)) {
                for (char c: sentence.toCharArray()) {
                    if (!charMap.containsKey(c)) {
                        charMap.put(c, index);
                        index++;
                    }
                }
            }
        }
        return charMap;
    }

    /**
     * Loads a previously saved map of characters to indices
     *
     * @param filename
     * @return
     */
    public HashMap<Character, Integer> createUniqueCharacterMapFromFile (String filename) {
        HashMap<Character, Integer> charMap = new HashMap<Character, Integer>();
        try {
            FileInputStream input = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            String line = reader.readLine();
            reader.readLine();
            while((line = reader.readLine()) != null) {
                char key = line.charAt(0);
                int index = Integer.parseInt(line.substring(line.indexOf(",") + 2));
                charMap.put(key, index);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File " + filename + " not found.");
            //throw e;
        } catch (IOException e) {
            System.out.println("IOException with file: " + filename);
            //throw e;
        }
        return charMap;
    }

    /**
     * Writes a map of characters to index to the file specified
     *
     * @param filename
     * @param map
     */
    public void saveMapToFile(String filename, HashMap<Character, Integer> map) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write("# Saved Map: key, value, total elements: " + map.size() + "\n\n");
            for (Object key: map.keySet()) {
                writer.write(key + ", " + map.get(key) + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
    }
}
