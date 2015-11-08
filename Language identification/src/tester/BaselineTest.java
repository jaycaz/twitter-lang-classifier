package tester;

import java.util.ArrayList;

import util.Language;

import java.util.HashMap;

import dataReader.ReadData;
import edu.stanford.nlp.stats.MultiClassPrecisionRecallExtendedStats;
import classifier.BaselineClassifier;
import classifier.FrequencyClassifier;

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
        classifier.accuracyByClass(data);
        double accuracy = classifier.accuracy(data);
        System.out.println("Acccuracy: " + accuracy);
        double fscore = classifier.f1(data);
        System.out.println("F1: " + fscore);
        //classifier.f1ByClass(data);
        FrequencyClassifier fC = new FrequencyClassifier();
        fC.train(data);
    }
}
