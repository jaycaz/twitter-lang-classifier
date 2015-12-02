package classifier;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
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
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RNN {

    private static Logger log = LoggerFactory.getLogger(RNN.class);
    private static int total;
    private static int error;


    public static void main( String[] args ) throws Exception {
        int lstmLayerSize = 500;					//Number of units in each GravesLSTM layer
        int miniBatchSize = 30;						//Size of mini batch to use when  training
        int examplesPerEpoch = 50*miniBatchSize;	//i.e., how many examples to learn on between generating samples
        int exampleLength = 100;					//Length of each training example
        int numEpochs = 100;							//Total number of training + sample generation epochs
        int nSamplesToGenerate = 50;					//Number of samples to generate after each training epoch
        Random rng = new Random(12345);

        //Get a DataSetIterator that handles vectorization of text into something we can use to train
        // our GravesLSTM network.
        DataSetIterator iter = new DataSetIterator(exampleLength, miniBatchSize, examplesPerEpoch, new Random(12345));
        int nOut = iter.totalOutcomes();

        //Set up network configuration:
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
                .learningRate(0.1)
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
        net.setListeners(new ScoreIterationListener(1));

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
            String[] samples = sampleLanguages(net,iter,rng,exampleLength,nSamplesToGenerate);
            /*for( int j=0; j<samples.length; j++ ){
                System.out.println("----- Sample " + j + " -----");
                System.out.println(samples[j]);
            }*/
            System.out.println("----- Error " + ((float) error)/total + " -----");
            iter.reset();	//Reset iterator for another epoch
        }
        System.out.println("\n\nExample complete");
    }


    private static String[] sampleLanguages(MultiLayerNetwork net,
                                                         DataSetIterator iter, Random rng, int charactersToSample, int numSamples ){
        //Create input for initialization

        String[] labels = new String[numSamples];
        String[] predicted = new String[numSamples];
        String[] predicted1 = new String[numSamples];
        //INDArray gold = Nd4j.zeros(new int[]{numSamples, iter.totalOutcomes(), charactersToSample});
        //INDArray allOuts = Nd4j.zeros(new int[]{numSamples, iter.totalOutcomes(), charactersToSample};

        for (int i = 0; i < numSamples; i++) {
            Pair<String, String> ex = iter.getRandomSentence("train");
            labels[i] = ex.getFirst();
            INDArray input = iter.getFeatureVector(ex.getSecond(), ex.getSecond().length());

            net.rnnClearPreviousState();
            INDArray output = net.rnnTimeStep(input);
            //INDArray out1 = net.output(input);
            INDArray out2 = output.tensorAlongDimension(output.size(2)-1,1,0);
            //INDArray out3 = out1.tensorAlongDimension(out1.size(2)-1,1,0);
            //gold.putRow(i, iter.getLanguageVector(ex.getFirst(), charactersToSample));
            //allOuts.putRow(i, output);
            int maxInd = -1;
            int maxInd2 = -1;
            double max = Double.MIN_VALUE;
            double max2 = Double.MIN_VALUE;
            for (int k = 0; k < output.length(); k++) {
                if (output.getDouble(k) > max) {
                    max = output.getDouble(k);
                    maxInd = k;
                }
            }
            for (int j = 0; j < out2.length(); j++) {
                if (out2.getDouble(j) > max2) {
                    max2 = out2.getDouble(j);
                    maxInd2 = j;
                }
            }
            int index = maxInd/ex.getSecond().length();
            predicted[i] = iter.getLanguageByIndex(index);
            predicted1[i] = iter.getLanguageByIndex(maxInd2);
        }
        //Evaluation eval = new Evaluation();
        //eval.eval(gold, allOuts);
        //log.info(eval.stats());

        int locError = 0;
        int locError2 = 0;
        int locTotal = 0;
        String[] out = new String[numSamples];
        for( int i=0; i<numSamples; i++ ) {
            out[i] = "True language: " + labels[i] + ", predicted: " + predicted[i];
            if (labels[i] != predicted[i]) {
                locError++;
                error++;
            }
            if (labels[i] != predicted1[i]) {
                locError2++;
            }
            total++;
            locTotal++;
        }
        System.out.println("----- current error " + ((float) locError)/locTotal + " -----");
        //System.out.println("----- current accuracy with last" + ((float) locError2)/locTotal + " -----");
        return out;
    }

}