import java.util.ArrayList;

import java.util.HashMap;

import org.dataReader.ReadData;
import org.classifier.BaselineClassifier;

/** Baseline Tester
 *
 */
public class BaselineTest {

    public String dataPath = "";

    public static void main(String []args) {
        System.out.println("BaselineTest is active!");
        ReadData reader = new ReadData();
        HashMap<String, ArrayList<String>> TrainingData = reader.getInputSentences("_train");
        System.out.println("Read in Data!");
        BaselineClassifier classifier = new BaselineClassifier();
        System.out.println("Training...!");
        classifier.train(TrainingData);
        System.out.println("Finished Training. Now evaluating...!");
        //HashMap<Language, ArrayList<ArrayList<String>>> devData = reader.getInputMap("_dev");
        //org.classifier.accuracyByClass(devData);
        //double accuracy = org.classifier.accuracy(devData);
        //CHANGE TO TEST ON NEW DATA
        //System.out.println("Acccuracy on dev: " + accuracy);
        //double fscore = org.classifier.f1(devData);
        //System.out.println("F1: " + fscore);
        HashMap<String, ArrayList<String>> testData = reader.getInputSentences("_test");
        double taccuracy = classifier.accuracy(testData);
        System.out.println("Acccuracy on test: " + taccuracy);
        //double tfscore = org.classifier.f1(testData);
        //System.out.println("F1: " + tfscore);
        //org.classifier.f1ByClass(data);
        
    }
}
