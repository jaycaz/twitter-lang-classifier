 package org.util;

import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Author: Martina Marek
 *
 * Class that implements various evaluation metrics.
 */
public class Evaluator {

    /**
     * Given a list of guessed labels and the real labels, returns the overall accuracy.
     *
     * @param guesses
     * @param gold
     * @return accuracy
     */
    public double accuracy (String[] guesses, String[] gold) {
        assert guesses.length == gold.length : "Length of the guesses and real labels differ - aborted";
        int error = 0;
        int total = 0;
        for (int i = 0; i < guesses.length; i++) {

            if (!guesses[i].equals(gold[i])) error++;
            total++;

        }
        return (total - error) / (float) total;
    }

    /**
     * Given a list of guessed labels and the real labels, returns the accuracy for each class
     *
     * @param guesses
     * @param gold
     * @return Counter with accuracy for each label
     */
    public ClassicCounter<String> accuracyByClass (String[] guesses, String[] gold) {
        assert guesses.length == gold.length : "Length of the guesses and real labels differ - aborted";
        ClassicCounter<String> classAccuracy = new ClassicCounter<>();
        HashMap<String, Pair<Integer, Integer>> counts = new HashMap<>();
        for (int i = 0; i < guesses.length; i++) {
            if (guesses[i] != gold[i]) {
                Pair<Integer, Integer> count = counts.get(gold[i]);
                if (count == null) count = new Pair<>(0, 0);
                count.setSecond(count.second() + 1);
                counts.put(gold[i], count);
            }
            Pair<Integer, Integer> count = counts.get(gold[i]);
            if (count == null) count = new Pair<>(0, 0);
            count.setFirst(count.first() + 1);
            counts.put(gold[i], count);
        }
        for (String label: counts.keySet()) {
            Pair<Integer, Integer> p = counts.get(label);
            classAccuracy.incrementCount(label, (p.first() - p.second)/ (double) p.first());
        }
        return classAccuracy;
    }

    /**
     * Given a list of guessed labels and the real labels, returns the overall F1 and F1 by class.
     *
     * @param guesses
     * @param gold
     * @return Counter with F1 for each label and overall F1
     */
    public ClassicCounter<String> f1ByClass(String[] guesses, String[] gold) {
        return f1ByClass(guesses, gold, false, "");
    }

    /**
     * Given a list of guessed labels and the real labels, returns the overall F1 and F1 by class. If wanted, confusion matrix is printed.
     *
     * @param guesses
     * @param gold
     * @param printConfusionMatrix: if to print the confusion matrix
     * @param filename: where to print the confusion matrix
     * @return Counter with F1 for each label and overall F1
     */
    public ClassicCounter<String> f1ByClass(String[] guesses, String[] gold, boolean printConfusionMatrix, String filename) {
        assert guesses.length == gold.length : "Length of the guesses and real labels differ - aborted";
        HashMap<String, Integer> labelInd = new HashMap<>();
        ArrayList<String> languages = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < gold.length; i++) {
            if (!labelInd.containsKey(gold[i])) {
                labelInd.put(gold[i], index);
                languages.add(gold[i]);
                index++;
            }
            if (!labelInd.containsKey(guesses[i])) {
                labelInd.put(guesses[i], index);
                languages.add(guesses[i]);
                index++;
            }
        }
        int[][] confusionMatrix = new int[labelInd.size()][labelInd.size()];
        for (int i = 0; i < gold.length; i++) {
            int lInd = labelInd.get(gold[i]);
            int guessInd = labelInd.get(guesses[i]);
            confusionMatrix[lInd][guessInd]++;
        }
        double[] precision = new double[labelInd.size()];
        double[] recall = new double[labelInd.size()];
        for (int i = 0; i < labelInd.size(); i++) {
            int sum = 0;
            for (int j = 0; j < labelInd.size(); j++) {
                sum += confusionMatrix[i][j];
            }
            precision[i] = confusionMatrix[i][i] / ((double) sum);
        }
        for (int i = 0; i < labelInd.size(); i++) {
            int sum = 0;
            for (int j = 0; j < labelInd.size(); j++) {
                sum += confusionMatrix[j][i];
            }
            recall[i] = confusionMatrix[i][i] / ((double) sum);
        }
        ClassicCounter<String> f1Scores = new ClassicCounter<>();
        double total = 0;
        for (String s: labelInd.keySet()) {
            double f1 = (2 * precision[labelInd.get(s)] * recall[labelInd.get(s)]) /
                    ((double) precision[labelInd.get(s)] + recall[labelInd.get(s)]);
            total += f1;
            f1Scores.incrementCount(s, f1);
        }
        if (printConfusionMatrix) printConfusionMatrix(confusionMatrix, labelInd, languages, filename);
        f1Scores.incrementCount("total", total/f1Scores.size());
        return f1Scores;
    }


    /**
     * Prints confusion matrix to the specified file.
     *
     * @param confusionMatrix
     * @param labelInd
     * @param languages
     * @param filename
     */
    private void printConfusionMatrix(int[][] confusionMatrix, HashMap<String, Integer> labelInd, ArrayList<String> languages, String filename) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            String firstLine = "";
            String rest = "";
            for (int j = 0; j < labelInd.size(); j++) {
                firstLine += ", " + languages.get(j);
                rest += languages.get(j);
                for (int i = 0; i < labelInd.size(); i++) {
                    rest += ", " + Integer.toString(confusionMatrix[i][j]);
                }
                rest += "\n";
            }
            firstLine += "\n";
            out.write(firstLine);
            out.write(rest);
            out.close();
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
    }

}
