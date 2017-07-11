import breeze.linalg.Options;
import org.apache.avro.generic.GenericData;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.spark.api.TrainingMaster;
import org.deeplearning4j.spark.impl.multilayer.SparkDl4jMultiLayer;
import org.deeplearning4j.spark.impl.paramavg.ParameterAveragingTrainingMaster;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/*
Il peut etre utile de faire un "ulimit -c unlimited" avant de lancer le programme.
 */
public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private static final long seed = 12345;
    private static String path = "generate/";
    private static final Random randGen = new Random(seed);
    private static final boolean useSparkLocal = false;
    private static final boolean useSpark = true;

    //Variable utile pour la création du réseau de neurones
    private static int height;
    private static int width;
    private static int nbChannel;
    private static int numEpochs;
    private static int nbLabels;

    //Variable utile a la generation des données de test
    private static int niftiSize = 100;
    private static int cubeSize = 10;
    private static int sphereSize = 10;
    private static String NIFTICubePrefix = "cube";
    private static String NIFTISpherePrefix = "sphere";
    private static int step = 10;

    public static void main(String[] args) {
        //testWithCubeAndSphere();
        //testWithCubeAndSphereRandom();
        //testWithSpark();
        launchExperience();


    }

    private static void launchExperience(){
    DataReader dr = new DataReader("IRM_Experience/", 80);
        Hashtable<String, INDArray> regLabel = new Hashtable<>();
        INDArray labelControle = Nd4j.create(new float[]{0, 1}, new int[]{1, 2});
        INDArray labelAlzheimer = Nd4j.create(new float[]{1, 0}, new int[]{1, 2});
        regLabel.put("AD", labelAlzheimer);
        regLabel.put("HC", labelControle);

        dr.createDataSet(28, regLabel);
        StatsStorage storage = new InMemoryStatsStorage();
        INDArrayDataSetIterator trainIterator = dr.getIteratorTrainNormalized();
        INDArrayDataSetIterator testIterator = dr.getIteratorTestNormalized();

        WrapperDL4J network = new WrapperDL4J(12345, 100, storage);
        System.out.println("Load...");
        network.loadSimpleCNN2();
        System.out.println("Init....");
        network.init();
        System.out.println("Train...");
        network.train(trainIterator);
        System.out.println("Evaluation....");
        network.evalModel(testIterator);
        System.out.println("Save...");
        network.saveModelToYAML();
        System.out.println("END");
    }

    private static void testWithCubeAndSphereRandom(){
        log.info("***** Generate Data *****");
        DataTestGenerator dtg = new DataTestGenerator(niftiSize, cubeSize, NIFTICubePrefix, sphereSize, NIFTISpherePrefix, step);
        dtg.generateSphereAndCubeSize(300, 190, 190, 160);
        log.info("***** Setup UI Server *****");

        StatsStorage statsStorage = new InMemoryStatsStorage();
        if(!useSparkLocal){
            UIServer uiServer = UIServer.getInstance();
            uiServer.attach(statsStorage);
        }

        log.info("***** Get info from datas *****");
        DataInput di = new DataInput(path);
        di.printInfo();

        log.info("**** Set up hyper-parameter");
        width =di.getX();
        height = di.getY();
        nbChannel = 1; //di.getZ(); //Surement a modifier getZ() par 1
        numEpochs = 100;
        nbLabels = 2;
        System.out.println("***** Hyper parameter *****");
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        System.out.println("Nb channel: " + nbChannel);
        System.out.println("Nb epochs: " + numEpochs);
        System.out.println("Nb label: " + nbLabels);
        System.out.println("****************************");

        log.info("***** Get an INDArrayDataSetIterator *****");
        di.createDataSet2(300, 56);
        INDArrayDataSetIterator iteratorTrain = di.getIteratorTrainNormalized();
        INDArrayDataSetIterator iteratorTest = di.getIteratorTestNormalized();

        log.info("***** Create network *****");
        WrapperDL4J network = new WrapperDL4J(seed, numEpochs, statsStorage);
        network.loadSimpleCNN2();
        network.init();

        log.info("***** Train model *****");
        network.train(iteratorTrain);

        log.info("***** Eval model *****");
        network.evalModel(iteratorTest);

        log.info("***** Save model *****");
        network.saveModelToYAML();
    }

    private static void testWithCubeAndSphere(){
        log.info("***** Generate Data *****");
        DataTestGenerator dtg = new DataTestGenerator(niftiSize, cubeSize, NIFTICubePrefix, sphereSize, NIFTISpherePrefix, step);
        dtg.generateSphereAndCube();
        log.info("***** Setup UI Server *****");

        StatsStorage statsStorage = new InMemoryStatsStorage();
        if(!useSparkLocal){
            UIServer uiServer = UIServer.getInstance();
            uiServer.attach(statsStorage);
        }

        log.info("***** Get info from datas *****");
        DataInput di = new DataInput(path);
        di.printInfo();

        log.info("**** Set up hyper-parameter");
        width =di.getX();
        height = di.getY();
        nbChannel = 1; //di.getZ(); //Surement a modifier getZ() par 1
        numEpochs = 100;
        nbLabels = 2;
        System.out.println("***** Hyper parameter *****");
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        System.out.println("Nb channel: " + nbChannel);
        System.out.println("Nb epochs: " + numEpochs);
        System.out.println("Nb label: " + nbLabels);
        System.out.println("****************************");

        log.info("***** Get an INDArrayDataSetIterator *****");
        di.createDataSet2(300, 56);
        INDArrayDataSetIterator iteratorTrain = di.getIteratorTrainNormalized();
        INDArrayDataSetIterator iteratorTest = di.getIteratorTestNormalized();

        log.info("***** Create network *****");
        WrapperDL4J network = new WrapperDL4J(seed, numEpochs, statsStorage);
        network.loadSimpleCNN();
        network.init();

        log.info("***** Train model *****");
        network.train(iteratorTrain);

        log.info("***** Eval model *****");
        network.evalModel(iteratorTest);

        log.info("***** Save model *****");
        network.saveModelToYAML();
    }

    private static void testWithSpark(){
        log.info("***** Main start *****");
        log.info("***** Generate Data *****");
        DataTestGenerator dtg = new DataTestGenerator(niftiSize, cubeSize, NIFTICubePrefix, sphereSize, NIFTISpherePrefix, step);
        //dtg.generateSphereAndCube();
        dtg.generateSphereAndCubeSize(300, 190, 190, 160);



        log.info("***** Setup UI Server *****");

        StatsStorage statsStorage = new InMemoryStatsStorage();
        if(!useSparkLocal){
            UIServer uiServer = UIServer.getInstance();
            uiServer.attach(statsStorage);
        }

        log.info("***** Get info from datas *****");
        DataInput di = new DataInput(path);
        di.printInfo();

        log.info("**** Set up hyper-parameter");
        width =di.getX();
        height = di.getY();
        nbChannel = 1; //di.getZ(); //Surement a modifier getZ() par 1
        numEpochs = 100;
        nbLabels = 2;
        System.out.println("***** Hyper parameter *****");
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        System.out.println("Nb channel: " + nbChannel);
        System.out.println("Nb epochs: " + numEpochs);
        System.out.println("Nb label: " + nbLabels);
        System.out.println("****************************");

        log.info("***** Get an INDArrayDataSetIterator *****");
        di.createDataSet2(300, 56);
        List<DataSet> trainDataList = di.getTrainDataListNormalized();
        List<DataSet> testDataList = di.getTestDataListNormalized();

        log.info("***** Create network *****");
        WrapperDL4J network = new WrapperDL4J(seed, numEpochs, statsStorage);
        network.loadSimpleCNN2();
        network.initSpark(useSparkLocal);
        network.initTrainingMaster();
        network.initSparkNet();
        network.sparkTrain(trainDataList);
        network.sparkEval(testDataList);
    }
}