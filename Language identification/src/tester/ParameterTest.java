package tester;

import classifier.NGramClassifier;
import dataReader.ReadData;
import dataReader.TwitterDataSimulator;
import util.Language;

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
        HashMap<Language, ArrayList<String>> trainingData = reader.getInputSentences("_train");
        HashMap<Language, ArrayList<String>> testData = reader.getInputSentences("_test");
        System.out.println("Read in Data!");
        //evaluation(trainingData, trainingData);
        //outputDifferentParams(trainingData, testData);
        outputTwitterSimul(trainingData);
    }

    public static void evaluation(HashMap<Language, ArrayList<String>> trainingData, HashMap<Language, ArrayList<String>> testData) {
        NGramClassifier classifier = new NGramClassifier();
        classifier.train(trainingData);
        classifier.writeAccuracyByClassSortedToFile("AccuracyByClassSorted.txt", testData);
        classifier.f1ByClass(testData, true);

    }

    public static void outputTwitterSimul(HashMap<Language, ArrayList<String>> trainingData) {
        TwitterDataSimulator twitterSim = new TwitterDataSimulator();
        NGramClassifier classifier = new NGramClassifier();
        classifier.train(trainingData);
        try {
            BufferedWriter twitter = new BufferedWriter(new FileWriter("TwitterMatlab.csv"));
            twitter.write(", accuracy, f1 \n");
            for (int i = 5; i < 21; i = i + 5) {
                HashMap<Language, ArrayList<String>> testData = twitterSim.getTestingData(i, i);
                double f = classifier.f1(testData);
                double acc = classifier.accuracy(testData);
                twitter.write("Sentence length: " + Integer.toString(i) + ", " + Double.toString(acc) + ", " + Double.toString(f) + "\n");
            }
            twitter.close();
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
    }

    public static void outputDifferentParams(HashMap<Language, ArrayList<String>> trainingData, HashMap<Language, ArrayList<String>> testData) {
        System.out.println("Parameter Test is active!");
        NGramClassifier classifier = new NGramClassifier();
        int[] fNum = new int[]{1000, 2000, 3000, 4000, 5000};
        try {
            BufferedWriter acc = new BufferedWriter(new FileWriter("AccuracyMatlab.csv"));
            BufferedWriter f1 = new BufferedWriter(new FileWriter("F1Matlab.csv"));
            String firstLine = "";
            //String secondLine = "";
            for (int j = 5; j < 6; j++) {
                //for (int k = j; k < 7; k++) {
                    firstLine += ", " + Integer.toString(j);
                    //secondLine += ", " + Integer.toString(k);
                //}
            }
            acc.write(firstLine + "\n");
            f1.write(firstLine + "\n");
            //acc.write(secondLine + "\n");
            //f1.write(secondLine + "\n");
            for (int i : fNum) {
                classifier.setTopCounts(i);
                acc.write(Integer.toString(i));
                f1.write(Integer.toString(i));
                for (int j = 5; j < 6; j++) {
                    //for (int k = j; k < 7; k++) {
                        classifier.setNGram(j, j);
                        System.out.println("Training with parameters: TopCount: " + i + " and NGram: [" + j + "] ...!");
                        classifier.train(trainingData);
                        System.out.println("Finished Training. Now evaluating...!");
                        double a = classifier.accuracy(testData);
                        double f = classifier.f1(testData);
                        acc.write(", " + Double.toString(a));
                        f1.write(", " + Double.toString(f));
                        classifier.reset();
                    //}
                }
                acc.write("\n");
                f1.write("\n");
            }
            acc.close();
            f1.close();
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
    }
}
