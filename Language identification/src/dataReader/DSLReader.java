package dataReader;

import classifier.NGramClassifier;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by martina on 12/2/15.
 */
public class DSLReader {

    public static void main(String []args) {
        String group = "F";
        HashMap<String, ArrayList<String>> data = readInData("DSLCC/train.txt", group);
        NGramClassifier classifier = new NGramClassifier();
        classifier.train(data);

        HashMap<String, ArrayList<String>> test = readInData("DSLCC/devel.txt", group);

        System.out.print("Accuracy: " + classifier.accuracy(test));

    }

    public static HashMap<String, ArrayList<String>> readInData(String filename, String group) {
        HashMap<String, ArrayList<String>> data = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            //int index = 0;
            while ((line = br.readLine()) != null) {
                //if (index > 100) break;
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
                //index++;
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

