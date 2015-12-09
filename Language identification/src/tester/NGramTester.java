package tester;

import classifier.NGramClassifier;
import classifier.StackedLRNGram;
import dataReader.ReadData;
import java.util.ArrayList;
import java.util.HashMap;

public class NGramTester {

	public String dataPath = "";

    public static void main(String []args) {
        System.out.println("NGramTest is active!");
        ReadData reader = new ReadData();
        HashMap<String, ArrayList<String>> TrainingData = reader.getInputSentences("_train");
        System.out.println("Read in Data!");
        NGramClassifier classifier = new NGramClassifier();
        System.out.println("Training...!");
        classifier.train(TrainingData);
        System.out.println("Finished Training. Now evaluating...!");
        classifier.saveToFile("NGram");
        classifier.reset();
        //classifier.loadFile("NGram");
        HashMap<String, ArrayList<String>> testData = reader.getInputSentences("_test");
        long startTime = System.nanoTime();
        System.out.print("Accuracy: " + classifier.accuracy(testData, true, 10));
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Total time for evaluation: " + estimatedTime);
    }
	
}
