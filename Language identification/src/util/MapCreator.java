package util;

import dataReader.DSLReader;
import dataReader.ReadData;
import edu.stanford.nlp.stats.IntCounter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by martina on 11/21/15.
 */
public class MapCreator {

    static final String INVALID_CHARACTERS = ".,;:!%#|{}()&^%$@?+=”•’»[]_*+\\/-\"…–—“„0123456789'";

    /**
     * Creates a unique map for characters
     * @param path
     * @param files
     * @return
     */
    public HashMap<Character, Integer> createUniqueCharacterMap (String path, String[] files) {
        IntCounter<Character> counter = new IntCounter<>();
        HashMap<Character, Integer> charMap = new HashMap<Character, Integer>();
        for (String file: files) {
            try {
//                FileInputStream input = new FileInputStream(path + file);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
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
