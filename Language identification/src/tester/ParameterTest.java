import org.classifier.NGramClassifier;
import org.dataReader.ReadData;
import org.dataReader.TwitterDataSimulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by martina on 12/1/15.
 */
public class ParameterTest {

    public static void main(String []args) {
        ReadData reader = new ReadData();
        HashMap<String, ArrayList<String>> trainingData = reader.getInputSentences("_train");
        HashMap<String, ArrayList<String>> testData = reader.getInputSentences("_test");
        System.out.println("Read in Data!");
        //evaluation(trainingData, trainingData);
        outputDifferentParams(trainingData, testData);
        //outputTwitterSimul(trainingData, testData);
    }

    public static void evaluation(HashMap<String, ArrayList<String>> trainingData, HashMap<String, ArrayList<String>> testData) {
        NGramClassifier classifier = new NGramClassifier();
        classifier.train(trainingData);
        //org.classifier.writeAccuracyByClassSortedToFile("AccuracyByClassSorted.txt", testData);
        //org.classifier.f1ByClass(testData, true);

    }

    public static void outputTwitterSimul(HashMap<String, ArrayList<String>> trainingData, HashMap<String, ArrayList<String>> test) {
        TwitterDataSimulator twitterSim = new TwitterDataSimulator();
        NGramClassifier classifier = new NGramClassifier();
        classifier.train(trainingData);
        try {
            for (int i = 19; i < 30; i = i + 2) {
                BufferedWriter twitter = new BufferedWriter(new FileWriter("TwitterMatlab.csv", true));
                HashMap<String, ArrayList<String>> testData = twitterSim.getTestingData(i, i);
                double acc = classifier.accuracy(testData);
                twitter.write("Sentence length: " + Integer.toString(i) + ", " + Double.toString(acc) + "\n");
                twitter.close();
            }
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
    }

    public static void outputDifferentParams(HashMap<String, ArrayList<String>> trainingData, HashMap<String, ArrayList<String>> testData) {
        System.out.println("Parameter Test is active!");
        NGramClassifier classifier = new NGramClassifier();
        classifier.train(trainingData);
        int[] fNum = new int[]{5, 10, 15, 20, 30};
        try {
            BufferedWriter acc = new BufferedWriter(new FileWriter("Beamsearch.csv", true));
            acc.write("beam size, accuracy, time \n");
            long startTime = System.nanoTime();
            double a = classifier.accuracy(testData);
            long estimatedTime = System.nanoTime() - startTime;
            acc.write("no beam, " + a + ", " + estimatedTime/1000000000.0 + "\n");
            acc.close();
            for (int i : fNum) {
                System.out.println("Testing  on beam size:" + i);
                startTime = System.nanoTime();
                a = classifier.accuracy(testData, true, i);
                estimatedTime = System.nanoTime() - startTime;
                System.out.println(i + ": accuracy: " + a + ", time:" + estimatedTime);
                acc = new BufferedWriter(new FileWriter("Beamsearch.csv", true));
                acc.write(Integer.toString(i) + ", " + Double.toString(a) + ", " + estimatedTime/1000000000.0 + "\n");
                acc.close();
            }
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
    }
}
