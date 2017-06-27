import org.apache.commons.io.FileUtils;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.stats.StatsListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by nicolas on 27.06.17.
 */
public class WrapperDL4J {
    private long seed;
    private int iteration;

    private StatsStorage statsStorage;
    private MultiLayerConfiguration conf;
    private MultiLayerNetwork network;

    public WrapperDL4J(long seed, int iteration, StatsStorage statsStorage){
        this.seed = seed;
        this.iteration = iteration;
        this.statsStorage = statsStorage;
    }

    public void loadSimpleCNN(){
        ConvolutionLayer layer0 = new ConvolutionLayer.Builder(5, 5)
                .nIn(1)
                .nOut(20)
                .stride(5, 5)
                .weightInit(WeightInit.XAVIER)
                .name("Convolution layer")
                .activation(Activation.RELU)
                .build();

        SubsamplingLayer layer1 = new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(2, 2)
                .stride(2, 2)
                .name("Max pooling layer")
                .build();

        DenseLayer layer2 = new DenseLayer.Builder()
                .activation(Activation.RELU)
                .name("Dense Layer")
                .nIn(200000)
                .nOut(100)
                .build();

        OutputLayer layer3 = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(2)
                .name("Output Layer")
                .activation(Activation.SOFTMAX)
                .build();

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iteration)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.1)
                .regularization(true)
                .l2(0.0004)
                .updater(Updater.NESTEROVS)
                .momentum(0.9)
                .list()
                .layer(0, layer0)
                .layer(1, layer1)
                .layer(2, layer2)
                .layer(3, layer3)
                .pretrain(false)
                .backprop(true)
                .setInputType(InputType.convolutional(1000, 1000, 1))
                .build();
        this.conf = conf;
        System.out.println("Configuration done !");

         /*
        Regarding the .setInputType(InputType.convolutionalFlat(28,28,1)) line: This does a few things.
        (a) It adds preprocessors, which handle things like the transition between the convolutional/subsampling layers
            and the dense layer
        (b) Does some additional configuration validation
        (c) Where necessary, sets the nIn (number of input neurons, or input depth in the case of CNNs) values for each
            layer based on the size of the previous layer (but it won't override values manually set by the user)
        InputTypes can be used with other layer types too (RNNs, MLPs etc) not just CNNs.
        For normal images (when using ImageRecordReader) use InputType.convolutional(height,width,depth).
        MNIST record reader is a special case, that outputs 28x28 pixel grayscale (nChannels=1) images, in a "flattened"
        row vector format (i.e., 1x784 vectors), hence the "convolutionalFlat" input type used here.
        Source: https://github.com/deeplearning4j/dl4j-examples/blob/master/dl4j-examples/src/main/java/org/deeplearning4j/examples/convolution/LenetMnistExample.java
        */
    }

    public void init(){
        network = new MultiLayerNetwork(conf);
        network.setListeners(new StatsListener(statsStorage));
        network.init();
    }

    public void train(INDArrayDataSetIterator iterator){
        int z = 0;
        while(iterator.hasNext()){
            z++;
            iterator.next();
        }
        System.out.println("iterator size: " + z);
        iterator.reset();
        network.fit(iterator);
        /*while(iterator.hasNext()){
            DataSet next = iterator.next();
            System.out.println("Labels: " + next.getLabels());
            network.fit(next);
        }*/
    }

    public void evalModel(INDArrayDataSetIterator iterator){
        Evaluation eval = new Evaluation();
        while(iterator.hasNext()){
            DataSet next = iterator.next();
            System.out.println("labels: " + next.getLabels());
            INDArray predict = network.output(next.getFeatureMatrix());
            eval.eval(next.getLabels(), predict);
        }
        System.out.println(eval.stats());
    }

    public void multipleEpochTrain(int epochs, INDArrayDataSetIterator iteratorTrain, INDArrayDataSetIterator iteratorTest){
        for(int i = 0; i < epochs; i++){
            network.fit(iteratorTrain);
            System.out.println("Epoch " + i + "done !");
            Evaluation eval = new Evaluation(2);
            while(iteratorTest.hasNext()){
                DataSet next = iteratorTest.next();
                INDArray output = network.output(next.getFeatureMatrix(), false );
                eval.eval(next.getLabels(), output);
            }
            System.out.println(eval.stats());
            iteratorTest.reset();
            iteratorTrain.reset();
        }
    }

    public void saveModelToJSON(){
        try{
            FileUtils.write(new File("SaveCNN.json"), network.getLayerWiseConfigurations().toJson());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void saveModelToYAML(){
        try{
            FileUtils.write(new File("SaveCNN.yaml"), network.getLayerWiseConfigurations().toYaml());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void saveModelToBin(){
        try{
            java.io.DataOutputStream dos = new java.io.DataOutputStream(Files.newOutputStream(Paths.get("SaveCNN.bin")));
            Nd4j.write(network.params(), dos);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
