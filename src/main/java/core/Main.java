package core;

import config.Configuration;
import generator.DataTestGenerator;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.WrapperDl4j;
import wrapper.local.LocalWrapperDl4j;
import wrapper.spark.SparkWrapperDl4j;

import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private static Configuration config;
    private static DataTestGenerator dtg;
    private static DataReader dr;

    public static void main(String[] args) {
        log.info("Start...");
        switch (args.length){
            case 0:
                config = new Configuration("config", true);
                break;
            case 1:
                config = new Configuration(args[0], false);
                dataGeneration();
                Hashtable<String, INDArray> regLabel = labelGeneration();
                readData(regLabel);
                launchExperience();
                break;
            default:
                displayHelp();
                break;
        }
    }

    private static void displayHelp(){
        System.out.println("Ce programme ne peut prendre qu'un seul argument:");
        System.out.println(" - Cet arguement est une string => nom du fichier de configuration a charger (sans extension)");
        System.out.println("S'il est lancé sans cet argument il va genere un fichier de configuration");
    }

    private static void dataGeneration(){
        if(config.isGenerateNifti()){
            if(config.isRandomGeneration()){
                dtg = new DataTestGenerator(config.getNiftiSize(), config.getCubeSize(), config.getCubePrefix(), config.getSphereSize(), config.getSpherePrefix(), config.getStep());
                dtg.generateSphereAndCubeSize(config.getBatchSize(), config.getNiftiHeight(), config.getNiftiDepth(), config.getNiftiWidth());
            }else{
                dtg = new DataTestGenerator(config.getNiftiSize(), config.getCubeSize(), config.getCubePrefix(), config.getSphereSize(), config.getSpherePrefix(), config.getStep());
                dtg.generateSphereAndCube();
            }
        }
    }

    private static Hashtable<String, INDArray> labelGeneration(){
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
        return regLabel;

    }

    private static void readData(Hashtable<String, INDArray> regLabel){
        dr = new DataReader(config.getNiftiDirectory(), config.getTrainRatio());
        dr.createDataSet(config.getMiniBatchSize(), regLabel);
    }

    private static void launchExperience(){
        if(config.isUseSpark()){
            List<DataSet> trainData;
            List<DataSet> testData;
            if(config.isNormalize()){
                trainData = dr.getTrainDataListNormalized();
                testData = dr.getTestDataListNormalized();
            }else{
                trainData = dr.getTrainDataList();
                testData = dr.getTestDataList();
            }
            SparkWrapperDl4j network = new SparkWrapperDl4j(config.getSeed(), config.getSpark_timeout(), config.getSpark_heartbeat(), config.getSparkMaster(), "LREN_Deeplearning");
            loadModel(network);
            network.initTrainingMaster(config.getMiniBatchSize(), config.getAveragingFrequency(), config.getWorkerPrefetchNumBatch(), config.getBatchSizePerWorker());
            network.initSparkNet();
            network.sparkTrain(trainData);
            network.sparkEval(testData);
            saveModel(network);
        }else {
            INDArrayDataSetIterator trainData;
            INDArrayDataSetIterator testData;
            if(config.isNormalize()){
                trainData = dr.getIteratorTrainNormalized();
                testData = dr.getIteratorTestNormalized();
            }else{
                trainData = dr.getIteratorTrain();
                testData = dr.getIteratorTest();
            }
            LocalWrapperDl4j network = new LocalWrapperDl4j(config.getSeed());
            loadModel(network);
            network.init();
            network.localTrain(trainData);
            network.localEvaluation(testData);
            saveModel(network);
        }
    }

    private static void loadModel(WrapperDl4j network){
        if(config.isloadFromModel()){
            network.loadFromModelSaved(config.getFileModelName());
        }else{
            network.loadSimpleCNN(config.getIteration(), config.getLearningRate(), config.getNbChannel(), config.getNbFilter(), config.getDenseOut(), config.getNbLabel(), config.getMatrixHeight(), config.getMatrixWidth(), config.getNbChannel());
        }
    }

    private static void saveModel(WrapperDl4j network){
        if(config.getSavingModel()){
            switch(config.getSaveType()){
                case "YAML":
                    network.saveModelToYAML(config.getSavedFileName());
                    break;
                case "JSON":
                    network.saveModelToJSON(config.getSavedFileName());
                    break;
                case "BIN":
                    network.saveModelToBin(config.getSavedFileName());
                    break;
                case "FULL":
                    network.saveModel(config.getSavedFileName());
            }
        }
    }
}