package org.main;

import org.apache.commons.lang.StringUtils;
import org.classifier.NGramClassifier;

/**
 * Created by jacaz_000 on 12/9/2015.
 */
public class RunClassifier {

    /**
     * Run class with arguments:
     * CL Args:
     * <TweetString>, a string you would like to classify
     */
    public static void main(String args[]) {

        // Get input string from command line
        String inputText;
        if(args.length > 0) {
            inputText = StringUtils.join(args, " ");
        }
        else {
            System.out.println("No input given to classify");
            return;
        }

        // Check for a pre-trained classifier
        NGramClassifier classifier = new NGramClassifier();
        classifier.loadFile("trained");

        //System.out.println("Classifying input: '" + inputText + "'");
        String lang = classifier.classify(inputText);
        System.out.println(lang);
    }
}
