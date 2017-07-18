package wrapper;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by nicolas on 13.07.17.
 */
public class WrapperDl4j {
    protected long seed;
    protected long iteration;
    protected MultiLayerConfiguration conf;
    protected MultiLayerNetwork network;

    public WrapperDl4j(long seed){
        this.seed = seed;
        this.iteration = iteration;
    }

    public MultiLayerConfiguration getConfiguration(){
        return conf;
    }
    public MultiLayerNetwork getNetwork(){
        return network;
    }

    public void loadSimpleCNN(int iteration, float learningRate, int nbChannel, int nbFilter, int nbDenseOut, int nbClasse, int height, int width, int depth){
        System.out.println("***** Load simple CNN model *****");
        ConvolutionLayer convolutionLayer = new ConvolutionLayer.Builder(20, 20)
                .nIn(nbChannel)
                .nOut(nbFilter)
                .stride(20, 20)
                .weightInit(WeightInit.XAVIER)
                .name("Convolution layer")
                .activation(Activation.RELU)
                .build();
        SubsamplingLayer subsamplingLayer = new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(20, 20)
                .stride(20, 20)
                .name("Max pooling layer")
                .build();
        DenseLayer denseLayer = new DenseLayer.Builder()
                .activation(Activation.RELU)
                .name("Dense layer")
                .nOut(nbDenseOut)
                .build();
        OutputLayer outputLayer = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(nbClasse)
                .name("Output layer")
                .activation(Activation.RELU)
                .build();
        this.conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iteration)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(learningRate)
                .regularization(true)
                .l2(0.0004)
                .updater(Updater.NESTEROVS)
                .list()
                    .layer(0, convolutionLayer)
                    .layer(1, subsamplingLayer)
                    .layer(2, denseLayer)
                    .layer(3, outputLayer)
                .pretrain(false)
                .backprop(true)
                .setInputType(InputType.convolutional(height, width, depth))
                .build();
        System.out.println("***** Configuration done *****");
    }

    public void saveModelToJSON(){
        try{
            FileUtils.write(new File("SaveNetwork.json"), network.getLayerWiseConfigurations().toJson());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void saveModelToYAML(){
        try{
            FileUtils.write(new File("SaveNetwork.yaml"), network.getLayerWiseConfigurations().toYaml());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void saveModelToBin(){
        try{
            DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get("SaveNetwork.bin")));
            Nd4j.write(network.params(), dos);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
