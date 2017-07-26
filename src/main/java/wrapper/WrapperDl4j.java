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
import org.deeplearning4j.util.ModelSerializer;
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

    public void loadSimpleCNN(int iteration, float learningRate, int nbChannel, int nbFilter, int nbDenseOut, int nbClasse, int height, int width, int depth, float l2Value, double momentum){
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
                .activation(Activation.SOFTMAX)
                .build();
        this.conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iteration)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(learningRate)
                .regularization(true)
                .l2(l2Value)
                .updater(Updater.NESTEROVS)
                .momentum(momentum)
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

    public void saveModelToJSON(String modelName){
        try{
            FileUtils.write(new File(modelName + ".json"), network.getLayerWiseConfigurations().toJson());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void saveModelToYAML(String modelName){
        try{
            FileUtils.write(new File(modelName + ".yaml"), network.getLayerWiseConfigurations().toYaml());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void saveModelToBin(String modelName){
        try{
            DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(modelName + ".bin")));
            Nd4j.write(network.params(), dos);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void saveModel(String modelName){
        File locationToSave = new File(modelName + ".zip");
        boolean saveUpdater = true;
        try{
            ModelSerializer.writeModel(this.network, locationToSave, saveUpdater);
        }catch(IOException e){
            System.out.println("Erreur lors de la sauvegarde du modèle");
            e.printStackTrace();
        }
    }

    public void loadFromModelSaved(String location){
        try{
            this.network = ModelSerializer.restoreMultiLayerNetwork(location);
        }catch(IOException e){
            System.out.println("Erreur lors du chargement du modèle");
            e.printStackTrace();
        }

    }
}
