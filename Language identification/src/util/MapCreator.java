package util;

import edu.stanford.nlp.stats.IntCounter;
import edu.stanford.nlp.stats.Counters;
import scala.Char;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.HashMap;

/**
 * Created by martina on 11/21/15.
 */
public class MapCreator {

    static final String INVALID_CHARACTERS = ".,;:!%#|{}()&^%$@?+=”•’»[]_*+\\/-\"…–—“„0123456789'";

    public HashMap<Character, Integer> createUniqueCharacterMap (String path, String[] files) {
        IntCounter<Character> counter = new IntCounter<>();
        HashMap<Character, Integer> charMap = new HashMap<Character, Integer>();
        for (String file: files) {
            try {
                FileInputStream input = new FileInputStream(path + file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
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
