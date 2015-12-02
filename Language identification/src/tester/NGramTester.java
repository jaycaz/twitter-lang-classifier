package tester;

<<<<<<< HEAD
=======
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataReader.TwitterDataSimulator;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.util.Pair;
import scala.Int;
import util.Language;
import classifier.BaselineClassifier;
>>>>>>> origin/master
import classifier.NGramClassifier;
import dataReader.ReadData;
import dataReader.TwitterDataSimulator;
import util.Language;

import java.util.ArrayList;
import java.util.HashMap;

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

        HashMap<Language, ArrayList<String>> testData = reader.getInputSentences("_test");
        classifier.writeAccuracyByClassSortedToFile("AccuracyByClassSorted.txt", testData);
        //TwitterDataSimulator twitterSim = new TwitterDataSimulator();
        //HashMap<Language, ArrayList<String>> testData = twitterSim.getTestingData(5, 10);
        //System.out.println("Accuracy: " + classifier.accuracy(testData));
        //classifier.f1Acc(testData);
        //classifier.writeScoresToFile("ScoresNGramLatex.txt", testData);
        //double taccuracy = classifier.accuracy(testData);
        //System.out.println("Acccuracy on test: " + taccuracy);
        //double tfscore = classifier.f1(testData);
        //System.out.println("F1: " + tfscore);
        //classifier.f1ByClass(data);
        System.out.println("Done.");
    }
	
}
