package tester;

import java.util.ArrayList;
import util.Language;
import java.util.HashMap;

import dataReader.ReadData;
import classifier.BaselineClassifier;

/** Baseline Tester
 *
 */
public class BaselineTest {

    public String dataPath = "";

    public static void main(String []args) {
        System.out.println("BaselineTest is active!");
        ReadData reader = new ReadData();
        HashMap<Language, ArrayList<ArrayList<String>>> data = reader.getInputMap();
        System.out.println("Read in Data!");
        BaselineClassifier classifier = new BaselineClassifier();
        System.out.println("Training...!");
        classifier.train(data);
        System.out.println("Finished Training. Now evaluating...!");
        double accuracy = classifier.accuracy(data);
        System.out.println("Acccuracy: " + accuracy);
        
    }
}
