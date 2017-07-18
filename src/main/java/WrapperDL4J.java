import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
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
import org.deeplearning4j.spark.api.TrainingMaster;
import org.deeplearning4j.spark.impl.multilayer.SparkDl4jMultiLayer;
import org.deeplearning4j.spark.impl.paramavg.ParameterAveragingTrainingMaster;
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
import java.util.List;

/**
 * Created by nicolas on 27.06.17.
 */
public class WrapperDL4J {
    private long seed;
    private int iteration;

    private StatsStorage statsStorage;
    private MultiLayerConfiguration conf;
    private MultiLayerNetwork network;

    private TrainingMaster trainingMaster;
    private SparkConf sparkConf;
    private JavaSparkContext sc;
    private SparkDl4jMultiLayer sparkNet;

    public WrapperDL4J(long seed, int iteration, StatsStorage statsStorage){
        this.seed = seed;
        this.iteration = iteration;
        this.statsStorage = statsStorage;
    }

    public MultiLayerConfiguration getConf(){
        return this.conf;
    }
    public MultiLayerNetwork getNetwork(){
        return this.network;
    }

    public void loadSimpleCNN(){
        ConvolutionLayer layer0 = new ConvolutionLayer.Builder(20, 20)
                .nIn(1) //Nb channel
                .nOut(10) //Nb filtre
                .stride(20, 20)
                .weightInit(WeightInit.XAVIER)
                .name("Convolution layer")
                .activation(Activation.RELU)
                .build();

        SubsamplingLayer layer1 = new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(20, 20)
                .stride(20, 20)
                .name("Max pooling layer")
                .build();

        DenseLayer layer2 = new DenseLayer.Builder()
                .activation(Activation.RELU)
                .name("Dense Layer")
                //.nIn(1562500)
                .nOut(50)
                .build();


        OutputLayer layer3 = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(2)//Nb Classe
                .name("Output Layer")
                .activation(Activation.SOFTMAX)
                .build();

        ConvolutionLayer layer4 = new ConvolutionLayer.Builder(5, 5)
                .nIn(1)
                .nOut(400)
                .stride(5, 5)
                .name("Convolution 2 layer")
                .activation(Activation.RELU)
                .build();

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iteration)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.1)
                .regularization(true)
                .l2(0.0004)
                .updater(Updater.RMSPROP)
                .list()
                .layer(0, layer0)
                .layer(1, layer1)
                .layer(2, layer2)
                //.layer(3, layer1)
                //.layer(4, layer2)
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

    public void loadSimpleCNN2(){
        System.out.println("***** Load CNN model *****");
        ConvolutionLayer layer0 = new ConvolutionLayer.Builder(20, 20)
                .nIn(1) //Nb channel
                .nOut(10) //Nb filtre
                .stride(20, 20)
                .weightInit(WeightInit.XAVIER)
                .name("Convolution layer")
                .activation(Activation.RELU)
                .build();

        SubsamplingLayer layer1 = new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                .kernelSize(20, 20)
                .stride(20, 20)
                .name("Max pooling layer")
                .build();

        DenseLayer layer2 = new DenseLayer.Builder()
                .activation(Activation.RELU)
                .name("Dense Layer")
                .nOut(50)
                .build();

        OutputLayer layer3 = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(2)//Nb Classe
                .name("Output Layer")
                .activation(Activation.SOFTMAX)
                .build();

        this.conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iteration)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.2)
                .regularization(true)
                .l2(0.0004)
                .updater(Updater.RMSPROP)
                .list()
                .layer(0, layer0)
                .layer(1, layer1)
                .layer(2, layer2)
                .layer(3, layer3)
                .pretrain(false)
                .backprop(true)
                .setInputType(InputType.convolutional(2880, 2048, 1))
                .build();
        System.out.println("Configuration done !");
    }

    public void init(){
        network = new MultiLayerNetwork(conf);
        //network.setListeners(new StatsListener(statsStorage));
        network.init();
    }

    public void initSpark(boolean useSparkLocal){
        System.out.println("***** Spark configuration *****");
        SparkConf sparkConf = new SparkConf();
        sparkConf.set("wrapper.spark.network.timeout", "600s");
        sparkConf.set("wrapper.spark.executor.heartbeatInterval", "600s");
        if(useSparkLocal){
            sparkConf.setMaster("wrapper.spark.local[*]");
        }
        sparkConf.setAppName("Deeplearning LREN");
        sc = new JavaSparkContext(sparkConf);
    }

    public void initTrainingMaster(){
        System.out.println("***** Init training Master *****");
        trainingMaster = new ParameterAveragingTrainingMaster.Builder(56)
                .averagingFrequency(5)
                .workerPrefetchNumBatches(2)
                .batchSizePerWorker(56)
                .build();

    }

    public void initSparkNet(){
        System.out.println("***** Init Spark Network *****");
        sparkNet = new SparkDl4jMultiLayer(sc, conf, trainingMaster);
    }

    public void sparkTrain(List<DataSet> listTrainData){
        System.out.println("***** Spark training *****");
        JavaRDD<DataSet> trainData = sc.parallelize(listTrainData);
        //Test
        trainData.persist(StorageLevel.DISK_ONLY());
        for(int i = 0; i < iteration; i++){
            sparkNet.fit(trainData);
        }

    }

    public void sparkEval(List<DataSet> listTestData){
        System.out.println("***** Spark Evaluation *****");
        JavaRDD<DataSet> testData = sc.parallelize(listTestData);
        //test
        testData.persist(StorageLevel.DISK_ONLY());
        Evaluation eval = sparkNet.evaluate(testData);
        System.out.println(eval.stats());
        trainingMaster.deleteTempFiles(sc);
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
    }

    public void evalModel(INDArrayDataSetIterator iterator){
        Evaluation eval = new Evaluation();
        while(iterator.hasNext()){
            DataSet next = iterator.next();
            INDArray predict = network.output(next.getFeatureMatrix());
            System.out.println("Valeur du label: " + next.getLabels());
            System.out.println("Prediction final: " + predict);
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
