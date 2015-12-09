package dataReader;

import classifier.NGramClassifier;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: Martina Marek
 *
 * Reads in the data from the DSL shared task.
 */
public class DSLReader {

    public static void main(String []args) {
        String group = "C";
        HashMap<String, ArrayList<String>> data = readInData("DSLCC/train.txt", group);
        NGramClassifier classifier = new NGramClassifier();
        classifier.train(data);

        HashMap<String, ArrayList<String>> test = readInData("DSLCC/devel.txt", group);

        System.out.print("Accuracy: " + classifier.accuracy(test));

    }

    /**
     * Reads in the data from the given file for the specified group
     *
     * @param filename
     * @param group
     * @return map of sentences
     */
    public static HashMap<String, ArrayList<String>> readInData(String filename, String group) {
        HashMap<String, ArrayList<String>> data = new HashMap<>();
        int max;
        if (filename.contains("train")) max = 18000;
        else max = 2000;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                index++;
                if (index%max > 100) continue;
                String[] tokens = line.split("\t");
                if (tokens[1].equals(group)) {
                    ArrayList<String> list = new ArrayList<>();
                    if (data.containsKey(tokens[2])) {
                        list = data.get(tokens[2]);
                        list.add(tokens[0]);
                    } else {
                        list.add(tokens[0]);
                    }
                    data.put(tokens[2], list);
                }
            }

        } catch (FileNotFoundException e) {
            // TODO: This line may be unnecessary b/c the file is already being checked
            System.out.println("Txt matcher found file " + filename + ", but could not open");
        } catch (IOException e) {
            System.out.println("Could not open or find file" + filename);

        }
        return data;
    }


}

