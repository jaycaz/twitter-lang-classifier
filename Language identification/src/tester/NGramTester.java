package tester;

import java.util.ArrayList;
import java.util.HashMap;

import dataReader.TwitterDataSimulator;
import util.Language;
import classifier.BaselineClassifier;
import classifier.NGramClassifier;
import dataReader.ReadData;

public class NGramTester {

	public String dataPath = "";

    public static void main(String []args) {
        System.out.println("NGramTest is active!");
        ReadData reader = new ReadData();
        HashMap<Language, ArrayList<String>> TrainingData = reader.getInputSentences("_train");
        System.out.println("Read in Data!");
        NGramClassifier classifier = new NGramClassifier();
        System.out.println("Training...!");
        classifier.train(TrainingData);
        //classifier.writeToFile("nGramClassifier");
        System.out.println("Finished Training. Now evaluating...!");
        classifier.writeTopNFeaturesToFile("Top10NGrams.txt", 10);
        //HashMap<Language, ArrayList<ArrayList<String>>> devData = reader.getInputMap("_dev");
        //classifier.accuracyByClass(devData);
        //double accuracy = classifier.accuracy(devData);
        //CHANGE TO TEST ON NEW DATA
        //System.out.println("Acccuracy on dev: " + accuracy);
        //double fscore = classifier.f1(devData);
        //System.out.println("F1: " + fscore);
        //HashMap<Language, ArrayList<String>> testData = reader.getInputSentences("_test");
        TwitterDataSimulator twitterSim = new TwitterDataSimulator();
        HashMap<Language, ArrayList<String>> testData = twitterSim.getTestingData(5, 10);
        double taccuracy = classifier.accuracy(testData);
        System.out.println("Acccuracy on test: " + taccuracy);
        //double tfscore = classifier.f1(testData);
        //System.out.println("F1: " + tfscore);
        //classifier.f1ByClass(data);
        
    }
	
}
