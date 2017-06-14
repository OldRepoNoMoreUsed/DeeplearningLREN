import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.listener.impl.LogRecordListener;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.api.Updater;
import org.deeplearning4j.nn.conf.LearningRatePolicy;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private static final long seed = 12345;
    private static String path = "src/main/resources/3D/sub";
    private static final Random randGen = new Random(seed);

    private static int height;
    private static int width;
    private static int nbChannel;
    private static int iteration;
    private static int nbLabels;

    public static void main(String[] args) {
        log.info("***** Main start *****");
        log.info("***** Get info from datas *****");
        DataInput di = new DataInput(path);
        di.printInfo();

        log.info("**** Set up hyper-parameter");
        width =di.getX();
        height = di.getY();
        nbChannel = di.getZ(); //Surement a modifier getZ() par 1
        iteration = 1;
        nbLabels = 2;

        log.info("***** Get an INDArrayDataSetIterator *****");
        INDArrayDataSetIterator iterator = di.getDataSetIterator();
        System.out.println("***** Iterator Info *****");
        System.out.printf("String of iterator: " + iterator.toString());
        System.out.println("\nTotal example: " + iterator.totalExamples());
        System.out.println("Labels: " + iterator.getLabels());
        System.out.println("***************************");

        log.info("***** Get CNN model *****");
        MultiLayerConfiguration conf = getCNNConf();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        log.info("***** Train model *****");
        training(iterator, model);

        log.info("***** Eval model *****");
        evalModel(iterator, model);

        log.info("***** END MAIN *****");
    }

    private static MultiLayerConfiguration getCNNConf(){
        ConvolutionLayer layer0 = new ConvolutionLayer.Builder(3, 3)
                .nIn(1)
                .nOut(20)
                .stride(1, 1)
                .padding(2, 2)
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
                .nOut(180)
                .build();

        OutputLayer layer3 = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(nbLabels)
                .activation(Activation.SOFTMAX)
                .build();

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iteration)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.001)
                .regularization(true)
                .l2(0.0004)
                .updater(org.deeplearning4j.nn.conf.Updater.NESTEROVS)
                .momentum(0.9)
                .list()
                    .layer(0, layer0)
                    .layer(1, layer1)
                    .layer(2, layer2)
                    .layer(3, layer3)
                .pretrain(false)
                .backprop(true)
                .setInputType(InputType.convolutional(5, 5, 1))
                .build();
        return conf;

    }

    private static void training(INDArrayDataSetIterator iterator, MultiLayerNetwork mlnet) {
        log.info("***** Training Model *****");
        mlnet.fit(iterator);
    }

    private static void evalModel(INDArrayDataSetIterator iterator, MultiLayerNetwork mlnet){
        log.info("***** Evaluating Model *****");
        iterator.reset();
        Evaluation eval = new Evaluation();
        while(iterator.hasNext()){
            DataSet next = iterator.next();
            INDArray predict = mlnet.output(next.getFeatureMatrix());
            eval.eval(next.getLabels(), predict);
        }
        System.out.println(eval.stats());
    }

    private static INDArray getGeneratedINDArray(int choice){
        DataTestGenerator dtg = new DataTestGenerator();
        if(choice != 1){
            return dtg.generateINDArray();
        }
        return dtg.generateNegativeINDArray();
    }

    public static void testWithDataSet(){
        int nRows = 9;
        int nColumns = 3;
        int nbChannels = 1;

        DataTestGenerator dtg = new DataTestGenerator();
        INDArrayDataSetIterator ds = dtg.generateDataSet(1000);

        NeuralNetwork neuralNetwork = new NeuralNetwork(1, seed, 1, nRows, nColumns, nbChannels, 2);
        MultiLayerNetwork mlnet = neuralNetwork.getNetwork();

        training(ds, mlnet);
        evalModel(ds, mlnet);
    }

    public static void test(){
        //Test la fonction fit avec des INDArray
        int nRows = 9;
        int nColumns = 3;
        int nbChannels = 1;
        int nIn = nRows * nColumns * nbChannels;

        log.info("Generate INDArray");
        INDArray dataPos = getGeneratedINDArray(0);
        INDArray dataNeg = getGeneratedINDArray(1);

        log.info("Setup the Neural Network...");
        NeuralNetwork neuralNetwork = new NeuralNetwork(1, seed, 1, nRows, nColumns, nbChannels, 2);
        MultiLayerNetwork mlnet = neuralNetwork.getNetwork();

        log.info("Train model");
        mlnet.fit(dataPos.reshape(9, 3));
        mlnet.fit(dataNeg.reshape(9, 3));
    }

}

/*{
        Random rand = new Random(seed);
        File parentDir = new File("src/main/resources/Dataset/Female");
        FileSplit filesInDir = new FileSplit(parentDir, new String[]{"gz"}, rand);
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        BalancedPathFilter pathFilter = new BalancedPathFilter(rand, new String[]{"gz"},labelMaker);
        System.out.println("taille file in dir: " + filesInDir.length());
        InputSplit[] filesInDirSplit = filesInDir.sample(pathFilter, 80, 20);
        InputSplit trainData = filesInDirSplit[0];
        InputSplit testData = filesInDirSplit[1];
        System.out.println("Taille trainData: " + trainData.length());
        System.out.println("Taille testData: " + testData.length());


        //TODO: Explorer les nativeImageLoader, y'a peut etre une piste pour le loading des nifti et les mettre dans DataVec
        //Todo: Explorer les RecordConverter, y'a aussi une piste a explorer pour le loading des nifti et datavec
        log.info("Starting...");
        log.info("Load and prepare data ...");
        //test();
        //testWithDataSet();
        DataInput data = new DataInput(path + "/Male/sub-01_T1w.nii.gz");
        int nRows = data.getX();
        int nColumns = data.getY();
        int nbChannels = data.getZ();
        int nIn = nRows * nColumns * nbChannels;

        /*ImageRecordReader recordReader = new ImageRecordReader(nRows, nColumns, nbChannels, labelMaker);
        try{
            recordReader.initialize(trainData);
        }catch(IOException e){
            e.printStackTrace();
        }
        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, 1, 0, 2);

        //Test de transformation de nifti en INDArray
        //INDArray arr = data.getData();
        INDArrayDataSetIterator ds = data.getDataSetIterator();
        System.out.println("Labels: " + ds.getLabels());
        //Fin du test

        //https://github.com/deeplearning4j/dl4j-examples/blob/master/dl4j-examples/src/main/java/org/deeplearning4j/examples/convolution/LenetMnistExample.java
        int layer = 0;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(1)
                .regularization(true).l1(0.0001).l2(0.00001)
                .learningRate(0.01)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(org.deeplearning4j.nn.conf.Updater.NESTEROVS).momentum(0.9)
                .useDropConnect(true)
                .list()
                .layer(layer++, new ConvolutionLayer.Builder(3, 3)
                        .name("First convolution")
                        .nIn(nRows)
                        .padding(1, 1)
                        .stride(1, 1) //Taille des pas de la convolution
                        .nOut(64)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(layer++, new LocalResponseNormalization.Builder().name("First normalization").build())
                .layer(layer++, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .name("First subsampling")
                        .kernelSize(2, 2)
                        .build())
                .layer(layer++, new ConvolutionLayer.Builder(3, 3)
                        .name("Second Convolution")
                        .padding(1, 1)
                        .stride(1, 1)
                        .nOut(64)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(layer++, new LocalResponseNormalization.Builder().name("Second Normalization").build())
                .layer(layer++, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .name("Second Subsampling")
                        .kernelSize(2, 2)
                        .build())
                .layer(layer++, new ConvolutionLayer.Builder(3, 3)
                        .name("Third Convolution")
                        .padding(1, 1)
                        .stride(1, 1)
                        .nOut(64)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(layer++, new ConvolutionLayer.Builder(3, 3)
                        .name("Fourth Convolution")
                        .padding(1, 1)
                        .stride(1, 1)
                        .nOut(64)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(layer++, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .name("Third Subsampling")
                        .kernelSize(2, 2)
                        .build())
                .layer(layer++, new DenseLayer.Builder().name("Dense Layer").weightInit(WeightInit.XAVIER).nOut(384).dropOut(0.5).build())
                .layer(layer++, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .name("OutputLayer")
                        .nOut(2)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX)
                        .build())
                .backprop(true).pretrain(false)
                .setInputType(InputType.convolutional(nRows, nColumns, nbChannels))
                .build();
        MultiLayerNetwork mlnet = new MultiLayerNetwork(conf);
        mlnet.init();
        mlnet.setListeners(new ScoreIterationListener(1));

        while(iterator.hasNext()){
            DataSet next = iterator.next();
            mlnet.fit(next);
        }
        //mlnet.fit(arr);
        training(ds, mlnet);
        evalModel(ds, mlnet);
        }*/
