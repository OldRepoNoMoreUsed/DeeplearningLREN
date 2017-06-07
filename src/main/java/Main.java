import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.listener.impl.LogRecordListener;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private static final String[] allowedFormats = {"nii.gz"};
    private static final long seed = 12345;
    private static final Random randGen = new Random(seed);
    private static String path = "src/main/resources/Dataset/";
    private static int batchSize = 1;

    private static DataTestGenerator dtg;

    public static void main(String[] args) {

        //TODO: Explorer les nativeImageLoader, y'a peut etre une piste pour le loading des nifti et les mettre dans DataVec
        //Todo: Explorer les RecordConverter, y'a aussi une piste a explorer pour le loading des nifti et datavec
        log.info("Starting...");
        log.info("Load and prepare data ...");
        //test();
        testWithDataSet();
        //List<String> labels = new ArrayList<>(Arrays.asList("male", "female"));
        /*DataInput data = new DataInput(path + "/Male/sub-01_T1w.nii.gz");
        int nRows = data.getX();
        int nColumns = data.getY();
        int nbChannels = data.getZ();
        int nIn = nRows * nColumns * nbChannels;*/

        /*Test de transformation de nifti en INDArray*/
        //INDArray arr = data.getget();
        /*Fin du test*/

        /*File parentDir = new File("src/main/ressources/Dataset");
        FileSplit filesInDir = new FileSplit(parentDir, allowedFormats, randGen);
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        BalancedPathFilter pathFilter = new BalancedPathFilter(randGen, allowedFormats, labelMaker);

        log.info("Split into train and test data");
        InputSplit[] filesInDirSplit = filesInDir.sample(pathFilter, 80, 20);
        InputSplit trainData = filesInDirSplit[0];
        InputSplit testData = filesInDirSplit[1];

        System.out.println("longueur des données de train: " + filesInDir.length());

        log.info("train data: " + trainData.toString());
        log.info("test data: " + testData.toString());

        ImageRecordReader recordReader = new ImageRecordReader(nRows, nColumns, nbChannels, labelMaker);
        recordReader.setLabels(labels);
        try{
            recordReader.initialize(trainData);
            recordReader.setListeners(new LogRecordListener());
        }catch(IOException e){
            e.printStackTrace();
        }

        DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, nIn, 2);
        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        scaler.fit(dataIter);
        dataIter.setPreProcessor(scaler);
        */


        /*while(dataIter.hasNext()){
            DataSet next = dataIter.next();
            next.scale();
            mlnet.fit(next);
        }*/
    }

    public static void test(){
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

    public static void testWithDataSet(){
        int nRows = 9;
        int nColumns = 3;
        int nbChannels = 1;
        int nIn = nRows * nColumns * nbChannels;

        DataTestGenerator dtg = new DataTestGenerator();
        INDArrayDataSetIterator ds = dtg.generateDataSet(1000);

        NeuralNetwork neuralNetwork = new NeuralNetwork(1, seed, 1, nRows, nColumns, nbChannels, 2);
        MultiLayerNetwork mlnet = neuralNetwork.getNetwork();

        int pass = 0;
        while(ds.hasNext()){
            DataSet next = ds.next();
            mlnet.fit(next);
            pass++;
            System.out.println("Nombre de passage: " + pass);
        }
    }

    public static INDArray getGeneratedINDArray(int choice){
        DataTestGenerator dtg = new DataTestGenerator();
        if(choice != 1){
            return dtg.generateINDArray();
        }
        return dtg.generateNegativeINDArray();
    }
}
