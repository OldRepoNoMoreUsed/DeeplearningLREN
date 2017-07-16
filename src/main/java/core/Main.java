package core;

import config.Configuration;
import generator.DataTestGenerator;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.ui.api.UIServer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.local.LocalWrapperDl4j;
import wrapper.spark.SparkWrapperDl4j;

import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private static Configuration config;
    private static DataTestGenerator dtg;

    public static void main(String[] args) {
        System.out.println(args.length);
        switch (args.length){
            case 0:
                config = new Configuration("config", true);
                break;
            case 1:
                config = new Configuration(args[0], false);
                break;
            default:
                displayHelp();
                break;
        }
        if(args.length > 0){
            if(config.isGenerateNifti()){
                if(config.isRandomGeneration()){
                    dtg = new DataTestGenerator(config.getNiftiSize(), config.getCubeSize(), config.getCubePrefix(), config.getSphereSize(), config.getSpherePrefix(), config.getStep());
                    dtg.generateSphereAndCubeSize(config.getBatchSize(), config.getNiftiHeight(), config.getNiftiDepth(), config.getNiftiWidth());
                }else{
                    dtg = new DataTestGenerator(config.getNiftiSize(), config.getCubeSize(), config.getCubePrefix(), config.getSphereSize(), config.getSpherePrefix(), config.getStep());
                    dtg.generateSphereAndCube();
                }
            }

            Hashtable<String, INDArray> regLabel = new Hashtable<>();
            int index = 0;
            Scanner in = new Scanner(System.in);
            for(int i = 0; i < config.getNbLabel(); i++){
                float[] labelArray = new float[config.getNbLabel()];
                for(int j = 0; j < config.getNbLabel(); j++){
                    if(j == index){
                        labelArray[j] = 1;
                    }else{
                        labelArray[j] = 0;
                    }
                }
                index++;
                System.out.println("Chaine de caracetere a chercher dans le fichier pour labeliser: ");
                String regex = in.nextLine();
                INDArray label = Nd4j.create(labelArray, new int[]{1, config.getNbLabel()});
                regLabel.put(regex, label);
            }
            System.out.println("Labels: " + regLabel.toString());

            DataReader dr = new DataReader(config.getNiftiDirectory(), config.getTrainRatio());
            dr.createDataSet(config.getMiniBatchSize(), regLabel);

            if(config.isUseSpark()){
                List<DataSet> trainData = dr.getTrainDataListNormalized();
                List<DataSet> testData = dr.getTestDataListNormalized();
                SparkWrapperDl4j network = new SparkWrapperDl4j(config.getSeed(), config.getSpark_timeout(), config.getSpark_heartbeat(), config.getSparkMaster(), "LREN_Deeplearning");
                network.loadSimpleCNN(config.getIteration(), config.getLearningRate(), config.getNbChannel(), config.getNbFilter(), config.getDenseOut(), config.getNbLabel(), config.getMatrixHeight(), config.getMatrixWidth(), config.getNbChannel());
                network.initTrainingMaster(config.getMiniBatchSize(), config.getAveragingFrequency(), config.getWorkerPrefetchNumBatch(), config.getBatchSizePerWorker());
                network.initSparkNet();
                network.sparkTrain(trainData);
                network.sparkEval(testData);
                network.saveModelToYAML();
            }else{
                INDArrayDataSetIterator trainData = dr.getIteratorTrainNormalized();
                INDArrayDataSetIterator testData = dr.getIteratorTestNormalized();
                LocalWrapperDl4j network = new LocalWrapperDl4j(config.getSeed());
                network.loadSimpleCNN(config.getIteration(), config.getLearningRate(), config.getNbChannel(), config.getNbFilter(), config.getDenseOut(), config.getNbLabel(), config.getMatrixHeight(), config.getMatrixWidth(), config.getNbChannel());
                network.init();
                network.localTrain(trainData);
                network.localEvaluation(testData);
                network.saveModelToYAML();
            }
        }

        //testWithCubeAndSphere();
        //testWithCubeAndSphereRandom();
        //testWithSpark();
        //launchExperience();
    }

    private static void displayHelp(){
        System.out.println("Ce programme ne peut prendre qu'un seul argument:");
        System.out.println(" - Cet arguement est une string => nom du fichier de configuration a charger (sans extension)");
        System.out.println("S'il est lancé sans cet argument il va genere un fichier de configuration");
    }

    private static void launchExperience(){
    /*DataReader dr = new DataReader("IRM_Experience/", 80);
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
        System.out.println("END");*/
    }

    private static void testWithCubeAndSphereRandom(){
        /*log.info("***** Generate Data *****");
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
        network.saveModelToYAML();*/
    }

    private static void testWithCubeAndSphere(){
        /*log.info("***** Generate Data *****");
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
        network.saveModelToYAML();*/
    }

    private static void testWithSpark(){
        /*log.info("***** core.Main start *****");
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
        network.sparkEval(testDataList);*/
    }
}