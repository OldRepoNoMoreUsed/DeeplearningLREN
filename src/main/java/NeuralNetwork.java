import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * Created by nicolas on 26.05.17.
 */
public class NeuralNetwork {

    private MultiLayerConfiguration conf;
    private long seed;
    private int iterations;

    private int x;
    private int y;
    private int z;
    private int nbLabels;
    private int nIn;

    private MultiLayerNetwork network;

    public NeuralNetwork(int x, int y, int z, int nbLabels){
        this.seed = 12345;
        this.iterations = 1;
        this.nbLabels = nbLabels;
        this.x = x;
        this.y = y;
        this.z = z;
        this.conf = getNetwork1();
        buildNetwork(this.conf);
    }

    public NeuralNetwork(int choice, long seed, int iterations, int x, int y, int z, int nbLabels){
        this.seed = seed;
        this.iterations = iterations;
        this.x = x;
        this.y = y;
        this.z = z;
        this.nbLabels = nbLabels;
        this.nIn = x * y * z;
        switch (choice){
            case 1:
                this.conf = getNetwork1();
                break;
            case 2:
                this.conf = getConvolutionalNetwork1();
            default:
                this.conf = getNetwork1();
                break;
        }
        buildNetwork(this.conf);
    }

    public void buildNetwork(MultiLayerConfiguration conf){
        this.network = new MultiLayerNetwork(conf);
    }

    public MultiLayerConfiguration getNetwork1(){
        this.conf = new NeuralNetConfiguration.Builder()
                .seed(this.seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
                .activation(Activation.LEAKYRELU)
                .weightInit(WeightInit.XAVIER)
                .learningRate(0.02)
                .updater(Updater.NESTEROVS).momentum(0.9)
                .regularization(true).l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(28 * 28).nOut(500).build())
                .layer(1, new DenseLayer.Builder().nIn(500).nOut(100).build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).activation(Activation.SOFTMAX).nIn(100).nOut(10).build())
                .pretrain(false).backprop(true)
                .build();

        return conf;
    }

    public MultiLayerConfiguration getConvolutionalNetwork1(){
        //Lenet model
        this.conf = new NeuralNetConfiguration.Builder()
                .seed(this.seed)
                .iterations(this.iterations)
                .regularization(false).l2(0.005)
                .activation(Activation.RELU)
                .learningRate(0.0001)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.RMSPROP).momentum(0.9)
                .list()
                .layer(0, convInit("cnn1", this.nIn, 50, new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0}, 0)) //nIn maybe z
                .layer(1, maxPool("maxPool1", new int[]{2, 2}))
                .layer(2, conv5x5("cnn2", 100, new int[]{5, 5}, new int[]{1, 1}, 0))
                .layer(3, maxPool("maxPool2", new int[]{2, 2}))
                .layer(4, new DenseLayer.Builder().nOut(500).build())
                .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nOut(nbLabels)
                    .activation(Activation.SOFTMAX)
                    .build())
                .backprop(true).pretrain(false)
                .setInputType(InputType.convolutional(x, y, z))
                .build();
        return conf;
    }

    private ConvolutionLayer convInit(String name, int in, int out, int[] kernel, int[] stride, int[] pad, double bias){
        return new ConvolutionLayer.Builder(kernel, stride, pad).name(name).nIn(in).nOut(out).biasInit(bias).build();
    }

    private SubsamplingLayer maxPool(String name, int[] kernel){
        return new SubsamplingLayer.Builder(kernel, new int[]{2, 2}).name(name).build();
    }

    private ConvolutionLayer conv5x5(String name, int out, int[] stride, int[] pad, double bias){
        return new ConvolutionLayer.Builder(new int[]{5, 5}, stride, pad).name(name).nOut(out).biasInit(bias).build();
    }

    public MultiLayerNetwork getNetwork() {
        return this.network;
    }
}
