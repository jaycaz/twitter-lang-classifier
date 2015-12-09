import org.classifier.NGramClassifier;
import org.dataReader.ReadData;

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
        NGramClassifier classifier = new NGramClassifier();
        HashMap<String, ArrayList<String>> TrainingData;


        while((TrainingData = reader.getNextTweets("data/twitter_train.txt"))!= null){
            classifier.train(TrainingData);
        }

        HashMap<String, ArrayList<String>> testData;
        double sum=0, count=0;
        while((testData = reader.getNextTweets("data/twitter_test.txt"))!= null){
            if(testData.size() == 0)
                continue;
            sum+=classifier.accuracy(testData);
            count+=1;
        }
        System.out.println(sum/count);




/*        TwitterNGramClassifier org.classifier = new TwitterNGramClassifier();

        HashMap<String, ArrayList<String>> TrainingData;
        org.classifier.train(reader, "data/twitter_train.txt");

        HashMap<String, ArrayList<String>> testData;

        while((testData = reader.getNextTweets("data/twitter_test.txt"))!= null){
            org.classifier.accuracy(testData);
        }
        double taccuracy = org.classifier.getAccuracy();
        System.out.println("Acccuracy on test: " + taccuracy);*/

     /*   try {
            BufferedReader br = new BufferedReader(new FileReader("test.txt"));
            String s = br.readLine();
            System.out.println(s );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }


}

