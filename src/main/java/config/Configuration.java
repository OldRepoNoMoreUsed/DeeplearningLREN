package config;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;
import java.util.Properties;

/**
 * Created by nicolas on 13.07.17.
 */
public class Configuration {
    private Properties properties;
    private OutputStream output;
    private String filename;

    private boolean useSpark;
    private String sparkMaster;
    private boolean generateNifti;
    private boolean randomGeneration;
    private int niftiWidth;
    private int niftiHeight;
    private int niftiDepth;
    private int cubeSize;
    private int sphereSize;
    private String cubePrefix;
    private String spherePrefix;
    private int step;
    private int niftiSize;
    private int matrixHeight;
    private int matrixWidth;
    private int seed;
    private int iteration;
    private int nbChannel;
    private int nbLabel;
    private int batchSize;
    private int miniBatchSize;
    private String niftiDirectory;
    private boolean loadNetworkConfig;
    private String spark_timeout;
    private String spark_heartbeat;
    private int trainRatio;
    private float learningRate;
    private int nbFilter;
    private int denseOut;
    private int averagingFrequency;
    private int workerPrefetchNumBatch;
    private int batchSizePerWorker;
    private String savedFileName;
    private String saveType;
    private boolean savingModel;
    private boolean loadFromSaved;
    private String fileModelName;
    private boolean normalize;
    private boolean useUI;

    public Configuration(String filename, boolean generate){
        this.filename = filename;
        this.properties = new Properties();
        if(generate){
            generatePropertiesFile();
        }else{
            loadProperties();
        }
    }

    public void generatePropertiesFile(){
        System.out.println("Generate configuration file ....");
        try{
            output = new FileOutputStream(filename);
            properties.setProperty("Normalize", "true");
            properties.setProperty("isUseUI", "false");
            properties.setProperty("NIFTI_Directory", "generate");
            properties.setProperty("Use_Spark", "false");
            properties.setProperty("Spark_Master", "local[*]");
            properties.setProperty("Generate_Nifti", "true");
            properties.setProperty("Random_generation", "true");
            properties.setProperty("NIFTI_Width", "160");
            properties.setProperty("NIFTI_Height", "190");
            properties.setProperty("NIFTI_Depth", "190");
            properties.setProperty("CubeSize", "10");
            properties.setProperty("SphereSize", "10");
            properties.setProperty("Cube_prefix", "cube");
            properties.setProperty("Sphere_prefix", "sphere");
            properties.setProperty("Step", "10");
            properties.setProperty("Nifti_Size", "100");
            properties.setProperty("Matrix_Height", "2880");
            properties.setProperty("Matrix_Width", "2048");
            properties.setProperty("Seed", "12345");
            properties.setProperty("Iteration", "100");
            properties.setProperty("Nb_Channel", "1");
            properties.setProperty("Nb_Label", "2");
            properties.setProperty("BatchSize", "300");
            properties.setProperty("MinibatchSize", "56");
            properties.setProperty("Ratio_TrainTest", "80");
            properties.setProperty("Spark_timeout", "600s");
            properties.setProperty("Spark_heartBeat", "600s");
            properties.setProperty("LearningRate", "0.2");
            properties.setProperty("Nb_Filter", "10");
            properties.setProperty("Nb_out_denseLayer", "50");
            properties.setProperty("Averaging_Frequency", "5");
            properties.setProperty("WorkerPrefetchNumBatch", "2");
            properties.setProperty("BatchSizePerWorker", "56");
            properties.setProperty("SavedFileName", "SavedConfig");
            properties.setProperty("SaveType", "FULL");
            properties.setProperty("SavingModel", "true");
            properties.setProperty("LoadModelFromFile", "true");
            properties.setProperty("FileModelName", "SavedConfig.zip");
            properties.store(output, "Fichier de configuration genere");
        }catch(IOException io){
            io.printStackTrace();
        }finally{
            if(output != null){
                try{
                    output.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Done!");
    }

    public void loadProperties(){
        System.out.println("Load configuration file...");
        InputStream input;
        try{
            input = new FileInputStream(filename);
            properties.load(input);
            useUI = Boolean.parseBoolean(properties.getProperty("isUseUI", "false"));
            normalize = Boolean.parseBoolean(properties.getProperty("Normalize", "true"));
            niftiDirectory= properties.getProperty("NIFTI_Directory", "/generate");
            useSpark = Boolean.parseBoolean(properties.getProperty("Use_Spark", "false"));
            sparkMaster =  properties.getProperty("Spark_Master", "local[*]");
            generateNifti = Boolean.parseBoolean(properties.getProperty("Generate_Nifti", "true"));
            randomGeneration = Boolean.parseBoolean(properties.getProperty("Random_generation", "true"));
            niftiWidth = Integer.parseInt(properties.getProperty("NIFTI_Width", "160"));
            niftiHeight = Integer.parseInt(properties.getProperty("NIFTI_Height", "190"));
            niftiDepth = Integer.parseInt(properties.getProperty("NIFTI_Depth", "190"));
            cubeSize = Integer.parseInt(properties.getProperty("CubeSize", "10"));
            sphereSize = Integer.parseInt(properties.getProperty("SphereSize", "10"));
            cubePrefix = properties.getProperty("Cube_prefix", "cube");
            spherePrefix = properties.getProperty("Sphere_prefix", "sphere");
            step = Integer.parseInt(properties.getProperty("Step", "10"));
            niftiSize = Integer.parseInt(properties.getProperty("Nifti_Size", "100"));
            matrixHeight = Integer.parseInt(properties.getProperty("Matrix_Height", "2880"));
            matrixWidth = Integer.parseInt(properties.getProperty("Matrix_Width", "2048"));
            seed = Integer.parseInt(properties.getProperty("Seed", "12345"));
            iteration = Integer.parseInt(properties.getProperty("Iteration", "100"));
            nbChannel = Integer.parseInt(properties.getProperty("Nb_Channel", "1"));
            nbLabel = Integer.parseInt(properties.getProperty("Nb_Label", "2"));
            batchSize = Integer.parseInt(properties.getProperty("BatchSize", "300"));
            miniBatchSize = Integer.parseInt(properties.getProperty("MinibatchSize", "56"));
            trainRatio = Integer.parseInt(properties.getProperty("Ratio_TrainTest", "80"));
            spark_timeout = properties.getProperty("Spark_timeout", "600s");
            spark_heartbeat = properties.getProperty("Spark_heartBeat", "600s");
            learningRate = Float.parseFloat(properties.getProperty("LearningRate", "0.2"));
            nbFilter = Integer.parseInt(properties.getProperty("Nb_Filter", "10"));
            denseOut = Integer.parseInt(properties.getProperty("Nb_out_denseLayer", "50"));
            averagingFrequency = Integer.parseInt(properties.getProperty("Averaging_Frequency", "5"));
            workerPrefetchNumBatch = Integer.parseInt(properties.getProperty("WorkerPrefetchNumBatch", "2"));
            batchSizePerWorker = Integer.parseInt(properties.getProperty("BatchSizePerWorker", "56"));
            savedFileName = properties.getProperty("SavedFileName", "SavedConfig");
            saveType = properties.getProperty("SaveType", "FULL");
            savingModel = Boolean.parseBoolean(properties.getProperty("SavingModel", "true"));
            loadFromSaved = Boolean.parseBoolean(properties.getProperty("LoadModelFromFile", "true"));
            fileModelName = properties.getProperty("FileModelName", "SavedConfig");
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Done!");
    }

    public boolean isUseUI(){ return useUI; }

    public boolean isNormalize(){ return normalize; }

    public boolean isloadFromModel(){
        return loadFromSaved;
    }

    public String getFileModelName(){ return fileModelName; }

    public String getSaveType(){ return saveType;}

    public Boolean getSavingModel(){ return savingModel; }

    public String getSavedFileName(){
        return savedFileName;
    }

    public int getBatchSizePerWorker() {
        return batchSizePerWorker;
    }

    public int getWorkerPrefetchNumBatch() {
        return workerPrefetchNumBatch;
    }

    public int getAveragingFrequency() {
        return averagingFrequency;
    }

    public int getDenseOut() {
        return denseOut;
    }

    public int getNbFilter() {
        return nbFilter;
    }

    public float getLearningRate() {
        return learningRate;
    }

    public int getTrainRatio() {
        return trainRatio;
    }

    public String getSpark_timeout() {
        return spark_timeout;
    }

    public String getSpark_heartbeat() {
        return spark_heartbeat;
    }

    public int getNiftiSize(){ return niftiSize; }

    public String getNiftiDirectory(){ return niftiDirectory; }

    public boolean isUseSpark() {
        return useSpark;
    }

    public String getSparkMaster() {
        return sparkMaster;
    }

    public boolean isGenerateNifti() {
        return generateNifti;
    }

    public boolean isRandomGeneration() {
        return randomGeneration;
    }

    public int getNiftiWidth() {
        return niftiWidth;
    }

    public int getNiftiHeight() {
        return niftiHeight;
    }

    public int getNiftiDepth() {
        return niftiDepth;
    }

    public int getCubeSize() {
        return cubeSize;
    }

    public int getSphereSize() {
        return sphereSize;
    }

    public String getCubePrefix() {
        return cubePrefix;
    }

    public String getSpherePrefix() {
        return spherePrefix;
    }

    public int getStep() {
        return step;
    }

    public int getMatrixHeight() {
        return matrixHeight;
    }

    public int getMatrixWidth() {
        return matrixWidth;
    }

    public int getSeed() {
        return seed;
    }

    public int getIteration() {
        return iteration;
    }

    public int getNbChannel() {
        return nbChannel;
    }

    public int getNbLabel() {
        return nbLabel;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getMiniBatchSize() {
        return miniBatchSize;
    }
}
