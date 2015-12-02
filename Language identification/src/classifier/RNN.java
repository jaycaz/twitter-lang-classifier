package classifier;

/*import org.apache.commons.io.FileUtils;
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
import org.slf4j.LoggerFactory;*/

public class RNN {

  /*  private static Logger log = LoggerFactory.getLogger(RNN.class);
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
    /*
            System.out.println("----- Accuracy " + ((float) error)/total + " -----");
            //iter.reset();	//Reset iterator for another epoch
        }
        System.out.println("\n\nExample complete");
    }


    private static String[] sampleLanguages(MultiLayerNetwork net,
                                                         DataSetIterator iter, Random rng, int charactersToSample, int numSamples ){
        //Create input for initialization

        String[] labels = new String[numSamples];
        String[] predicted = new String[numSamples];
        for (int i = 0; i < numSamples; i++) {
            Pair<String, String> ex = iter.getRandomSentence();
            labels[i] = ex.getFirst();
            INDArray initializationInput = Nd4j.zeros(new int[]{1, iter.inputColumns(), ex.getSecond().length()});
            INDArray gold = Nd4j.zeros(new int[]{1, iter.totalOutcomes(), ex.getSecond().length()});
            int langInd = iter.getLanguageIndex(ex.getFirst());
            for (int j = 0; j < ex.getSecond().length(); j++) {
                int ind;
                if (ex.getSecond().length() <= j) ind = iter.getCharIndex(' ');
                else ind = iter.getCharIndex(ex.getSecond().charAt(j));
                if (ind == -1) ind = iter.getCharIndex(' ');
                initializationInput.putScalar(new int[]{0,ind,j}, 1.0);
                gold.putScalar(new int[]{0,langInd,j}, 1.0);
            }
            net.rnnClearPreviousState();
            INDArray output = net.rnnTimeStep(initializationInput);
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
                error++;
                locError++;
            }
            total++;
            locTotal++;
        }
        System.out.println("----- current accuracy " + ((float) locError)/locTotal + " -----");
        return out;
    }
        */

}