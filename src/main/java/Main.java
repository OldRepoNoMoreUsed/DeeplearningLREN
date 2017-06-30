import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
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
        //dtg.generateSphereAndCubeSize(300, 190, 190, 160);

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
        iteration = 100;
        nbLabels = 2;
        System.out.println("***** Hyper parameter *****");
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        System.out.println("Nb channel: " + nbChannel);
        System.out.println("Nb iteration: " + iteration);
        System.out.println("Nb label: " + nbLabels);
        System.out.println("****************************");

        log.info("***** Get an INDArrayDataSetIterator *****");
        di.createDataSet2();
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
        //network.loadSimpleCNN();
        network.loadSimpleCNN2();
        network.init();

        System.out.println("***** Normalize data train *****");
        DataNormalization scaler = new NormalizerMinMaxScaler();
        scaler.fit(iteratorTrain);
        iteratorTrain.setPreProcessor(scaler);

        log.info("***** Train model *****");
        network.train(iteratorTrain);

        System.out.println("Normalize data test");
        scaler.fit(iteratorTest);
        iteratorTest.setPreProcessor(scaler);
        log.info("***** Eval model *****");
        network.evalModel(iteratorTest);

        /*log.info("***** Multiple epochs *****");
        network.multipleEpochTrain(3, iteratorTrain, iteratorTest);*/

        log.info("***** Save model *****");
        network.saveModelToYAML();

        log.info("***** END MAIN *****");
    }
}