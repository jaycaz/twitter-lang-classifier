package classifier;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import dataReader.DataSetIterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.util.Pair;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.plot.iterationlistener.NeuralNetPlotterIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author: Martina Marek
 *
 * Implements a RNN.
 *
 * source: RNN from DeepLearning4j: http://deeplearning4j.org/recurrentnetwork.html, modified to fit our data
 * and our model
 *
 */
public class RNN {

    private static Logger log = LoggerFactory.getLogger(RNN.class);
    private static int total;
    private static int error;


    public static void main( String[] args ) throws Exception {
        int lstmLayerSize = 100;					//Number of units in each GravesLSTM layer
        int miniBatchSize = 10;						//Size of mini batch to use when  training
        int examplesPerEpoch = 5*miniBatchSize;	//i.e., how many examples to learn on between generating samples
        int exampleLength = 100;					//Length of each training example
        int numEpochs = 50;							//Total number of training + sample generation epochs
        int nSamplesToGenerate = 50;					//Number of samples to generate after each training epoch
        Random rng = new Random(12345);

        //Get a DataSetIterator that handles vectorization of text into something we can use to train
        // our GravesLSTM network.
        DataSetIterator iter = new DataSetIterator(exampleLength, miniBatchSize, examplesPerEpoch, new Random(12345), "DSLCC/train.txt", "DSLCC/devel.txt", "A");
        int nOut = iter.totalOutcomes();


        //Set up network configuration:
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(10)
                .learningRate(0.01)
                .rmsDecay(0.95)
                .seed(12345)
                .regularization(true)
                .l2(0.001)
                .list(3)
                .layer(0, new GravesLSTM.Builder().nIn(iter.inputColumns()).nOut(lstmLayerSize)
                        .updater(Updater.RMSPROP)
                        .activation("tanh").weightInit(WeightInit.DISTRIBUTION)
                        .dist(new UniformDistribution(-0.08, 0.08)).build())
                .layer(1, new GravesLSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
                        .updater(Updater.RMSPROP)
                        .activation("tanh").weightInit(WeightInit.DISTRIBUTION)
                        .dist(new UniformDistribution(-0.08, 0.08)).build())
                .layer(2, new RnnOutputLayer.Builder(LossFunction.MCXENT).activation("softmax")        //MCXENT + softmax for classification
                        .updater(Updater.RMSPROP)
                        .nIn(lstmLayerSize).nOut(nOut).weightInit(WeightInit.DISTRIBUTION)
                        .dist(new UniformDistribution(-0.08, 0.08)).build())
                .pretrain(false).backprop(true)
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);


        net.init();
        net.setListeners(new ScoreIterationListener(10));
        //net.setListeners(new NeuralNetPlotterIterationListener (1));
        //net.setListeners(new Collection<IterationListener>(new ScoreIterationListener(10), new NeuralNetPlotterIterationListener(1, true)));

        //MultiLayerNetwork net = loadModel("RNN");

        //Print the  number of parameters in the network (and for each layer)
        Layer[] layers = net.getLayers();
        int totalNumParams = 0;
        for( int i=0; i<layers.length; i++ ){
            int nParams = layers[i].numParams();
            System.out.println("Number of parameters in layer " + i + ": " + nParams);
            totalNumParams += nParams;
        }
        System.out.println("Total number of network parameters: " + totalNumParams);

        //Do training, and then generate and print samples from network
        for( int i=0; i<numEpochs; i++ ){
            DataSet next = iter.next();
            net.fit(next);

            System.out.println("--------------------");
            System.out.println("Completed epoch " + i);
            System.out.println("Trying to identify " + nSamplesToGenerate + " languages... ");
            String[] samples = sampleLanguages(net,iter,exampleLength,nSamplesToGenerate);
            /*for( int j=0; j<samples.length; j++ ){
                System.out.println("----- Sample " + j + " -----");
                System.out.println(samples[j]);
            }*/
            System.out.println("----- Error " + ((float) error)/total + " -----");
            iter.reset();	//Reset iterator for another epoch
        }
        System.out.println("\n\nSaving model...");
        saveModel("RNN", net);


        //String[] samples = sampleLanguages(net,iter,rng,exampleLength,nSamplesToGenerate);
        System.out.println("\n\nExample complete");
    }

    /**
     * Classifies a specified number of languages
     *
     * @param net
     * @param iter
     * @param charactersToSample
     * @param numSamples
     * @return output String
     */
    private static String[] sampleLanguages(MultiLayerNetwork net,
                                                         DataSetIterator iter, int charactersToSample, int numSamples ){
        String[] labels = new String[numSamples];
        String[] predicted = new String[numSamples];

        for (int i = 0; i < numSamples; i++) {
            Pair<String, String> ex = iter.getRandomSentence("train");
            labels[i] = ex.getFirst();
            INDArray input = iter.getFeatureVector(ex.getSecond(), ex.getSecond().length());

            net.rnnClearPreviousState();
            INDArray output = net.rnnTimeStep(input);
            //INDArray out1 = net.output(input);
            int maxInd = -1;
            double max = Double.MIN_VALUE;
            for (int k = 0; k < output.length(); k++) {
                if (output.getDouble(k) > max) {
                    max = output.getDouble(k);
                    maxInd = k;
                }
            }
            int index = maxInd/ex.getSecond().length();
            predicted[i] = iter.getLanguageByIndex(index);
        }

        int locError = 0;
        int locTotal = 0;
        String[] out = new String[numSamples];
        for( int i=0; i<numSamples; i++ ) {
            out[i] = "True language: " + labels[i] + ", predicted: " + predicted[i];
            if (labels[i] != predicted[i]) {
                locError++;
                error++;
            }
            total++;
            locTotal++;
        }
        System.out.println("----- current error " + ((float) locError)/locTotal + " -----");
        return out;
    }

    public static void saveModel(String pathname, MultiLayerNetwork net) {
        try {
            OutputStream fos = Files.newOutputStream(Paths.get(pathname + "/coefficients.bin"));
            DataOutputStream dos = new DataOutputStream(fos);
            Nd4j.write(net.params(), dos);
            dos.flush();
            dos.close();
            FileUtils.write(new File(pathname + "/conf.json"), net.getLayerWiseConfigurations().toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MultiLayerNetwork loadModel(String pathname) {
        try {
            MultiLayerConfiguration confFromJson = MultiLayerConfiguration.fromJson(FileUtils.readFileToString(new File(pathname + "/conf.json")));
            DataInputStream dis = new DataInputStream(new FileInputStream(pathname + "/coefficients.bin"));
            INDArray newParams = Nd4j.read(dis);
            dis.close();
            MultiLayerNetwork savedNetwork = new MultiLayerNetwork(confFromJson);
            savedNetwork.init();
            savedNetwork.setParameters(newParams);
            System.out.println("Parameters of loaded model: " + savedNetwork.params());
            return savedNetwork;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}