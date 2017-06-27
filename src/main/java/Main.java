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
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private static final long seed = 12345;
    private static String path = "generate/";
    private static final Random randGen = new Random(seed);

    //Variable utile pour la création du réseau de neurones
    private static int height;
    private static int width;
    private static int nbChannel;
    private static int iteration;
    private static int nbLabels;

    //Variable utile a la generation des données de test
    private static int niftiSize = 100;
    private static int cubeSize = 10;
    private static int sphereSize = 10;
    private static String NIFTICubePrefix = "cube";
    private static String NIFTISpherePrefix = "sphere";
    private static int step = 10;

    public static void main(String[] args) {
        log.info("***** Main start *****");
        log.info("***** Generate Data *****");
        DataTestGenerator dtg = new DataTestGenerator(niftiSize, cubeSize, NIFTICubePrefix, sphereSize, NIFTISpherePrefix, step);
        //dtg.generateSphereAndCube();

        log.info("***** Setup UI Server *****");
        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage);

        log.info("***** Get info from datas *****");
        DataInput di = new DataInput(path);
        di.printInfo();

        log.info("**** Set up hyper-parameter");
        width =di.getX();
        height = di.getY();
        nbChannel = 1; //di.getZ(); //Surement a modifier getZ() par 1
        iteration = 1;
        nbLabels = 2;
        System.out.println("****************************");
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        System.out.println("Nb channel: " + nbChannel);
        System.out.println("Nb iteration: " + iteration);
        System.out.println("Nb label: " + nbLabels);
        System.out.println("****************************");

        log.info("***** Get an INDArrayDataSetIterator *****");
        di.createDataSetCube();
        INDArrayDataSetIterator iteratorTrain = di.getIteratorTrain();
        INDArrayDataSetIterator iteratorTest = di.getIteratorTest();

        System.out.println("***** Train Iterator Info *****");
        System.out.printf("String of iterator: " + iteratorTrain.toString());
        System.out.println("\nTotal example: " + iteratorTrain.totalExamples());
        System.out.println("Labels: " + iteratorTrain.getLabels());
        System.out.println("***************************");
        System.out.println("***** Test Iterator Info *****");
        System.out.printf("String of iterator: " + iteratorTest.toString());
        System.out.println("\nTotal example: " + iteratorTest.totalExamples());
        System.out.println("Labels: " + iteratorTest.getLabels());
        System.out.println("***************************");

        log.info("***** Get CNN model *****");
        WrapperDL4J network = new WrapperDL4J(seed, iteration, statsStorage);
        network.loadSimpleCNN();
        network.init();

        //verification labelisation
        try{
            log.info("***** Print iterator train *****");
            PrintWriter printer = new PrintWriter("Nifti.txt");
            while(iteratorTrain.hasNext()){
                DataSet ds = iteratorTrain.next();
                printer.print(ds.getFeatureMatrix());
                printer.println("************************************************************************************");
            }
            iteratorTrain.reset();
        }catch (IOException e){
            e.printStackTrace();
        }


        log.info("***** Train model *****");
        network.train(iteratorTrain);

        log.info("***** Eval model *****");
        network.evalModel(iteratorTest);

        /*log.info("***** Multiple epochs *****");
        network.multipleEpochTrain(3, iteratorTrain, iteratorTest);*/

        log.info("***** Save model *****");
        network.saveModelToYAML();

        log.info("***** END MAIN *****");
    }


    /*private static INDArray getGeneratedINDArray(int choice){
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
    }*/

}