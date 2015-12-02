package tester;

import classifier.TwitterNGramClassifier;
import dataReader.ReadData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by May on 12/1/15.
 */
public class TwitterTest {
    public static void main(String []args){
//        SplitData sp = new SplitData();
//        sp.splitFile("data/twitter_data_labled.tsv");

        ReadData reader = new ReadData();
        TwitterNGramClassifier classifier = new TwitterNGramClassifier();

        HashMap<String, ArrayList<String>> TrainingData;
        classifier.train(reader, "data/twitter_train.txt");

        HashMap<String, ArrayList<String>> testData;

        while((testData = reader.getNextTweets("data/twitter_test.txt"))!= null){
            classifier.accuracy(testData);
        }
        double taccuracy = classifier.getAccuracy();
        System.out.println("Acccuracy on test: " + taccuracy);

        }


}

