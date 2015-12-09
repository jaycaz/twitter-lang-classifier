import org.classifier.NGramClassifier;
import org.dataReader.ReadData;
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
        //org.classifier.writeToFile("nGramClassifier");
        System.out.println("Finished Training. Now evaluating...!");

        HashMap<String, ArrayList<String>> testData = reader.getInputSentences("_test");
        long startTime = System.nanoTime();
        System.out.print("Accuracy: " + classifier.accuracy(testData));
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Total time for evaluation: " + estimatedTime);
        //org.classifier.writeAccuracyByClassSortedToFile("AccuracyByClassSorted.txt", testData);
        //TwitterDataSimulator twitterSim = new TwitterDataSimulator();
        //HashMap<Language, ArrayList<String>> testData = twitterSim.getTestingData(5, 10);
        //System.out.println("Accuracy: " + org.classifier.accuracy(testData));
        //org.classifier.f1Acc(testData);
        //org.classifier.writeScoresToFile("ScoresNGramLatex.txt", testData);
        //double taccuracy = org.classifier.accuracy(testData);
        //System.out.println("Acccuracy on test: " + taccuracy);
        //double tfscore = org.classifier.f1(testData);
        //System.out.println("F1: " + tfscore);
        //org.classifier.f1ByClass(data);
        System.out.println("Done.");
    }
	
}
