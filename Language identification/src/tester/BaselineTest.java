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
        HashMap<Language, ArrayList<ArrayList<String>>> TrainingData = reader.getInputMap("_train");
        System.out.println("Read in Data!");
        BaselineClassifier classifier = new BaselineClassifier();
        System.out.println("Training...!");
        classifier.train(TrainingData);
        System.out.println("Finished Training. Now evaluating...!");
        //HashMap<Language, ArrayList<ArrayList<String>>> devData = reader.getInputMap("_dev");
        //classifier.accuracyByClass(devData);
        //double accuracy = classifier.accuracy(devData);
        //CHANGE TO TEST ON NEW DATA
        //System.out.println("Acccuracy on dev: " + accuracy);
        //double fscore = classifier.f1(devData);
        //System.out.println("F1: " + fscore);
        HashMap<Language, ArrayList<ArrayList<String>>> testData = reader.getInputMap("_test");
        double taccuracy = classifier.accuracy(testData);
        System.out.println("Acccuracy on test: " + taccuracy);
        //double tfscore = classifier.f1(testData);
        //System.out.println("F1: " + tfscore);
        //classifier.f1ByClass(data);
        
    }
}
