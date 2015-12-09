import java.util.ArrayList;
import java.util.HashMap;

import org.classifier.FrequencyClassifier;
import org.dataReader.ReadData;

public class FrequencyClassifierTest {
	
	public static void main(String []args) {
		System.out.println("FrequencyClassifierTest is active!");
	    ReadData reader = new ReadData();
	    HashMap<String, ArrayList<String>> trainingData = reader.getInputSentences("_train");
	    System.out.println("Read in Data!");
		FrequencyClassifier classifier = new FrequencyClassifier();
	    System.out.println("Training...!");
        classifier.train(trainingData);
        System.out.println("Finished Training. Now evaluating...!");
        //HashMap<Language, ArrayList<ArrayList<String>>> devData = reader.getInputMap("_dev");
        //org.classifier.accuracyByClass(devData);
        //double accuracy = org.classifier.accuracy(devData);
        //System.out.println("Acccuracy: " + accuracy);
        //double fscore = org.classifier.f1(devData);
        //System.out.println("F1: " + fscore);
        HashMap<String, ArrayList<String>> testData = reader.getInputSentences("_test");
        double taccuracy = classifier.accuracy(testData);
        System.out.println("Acccuracy on test: " + taccuracy);
        //double tfscore = org.classifier.f1(testData);
        //System.out.println("F1: " + tfscore);
	}
}
