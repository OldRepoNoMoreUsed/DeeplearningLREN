package core;

import niftijio.NiftiVolume;
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by nicolas on 21.06.17.
 */
public class DataReader {
    private String workFolder;
    private List<String> filenames;
    private int batchSize;
    private INDArrayDataSetIterator iteratorTrain;
    private INDArrayDataSetIterator iteratorTest;
    private int trainRatio;

    public INDArrayDataSetIterator getIteratorTest() {
        return iteratorTest;
    }

    public INDArrayDataSetIterator getIteratorTrain() {
        return iteratorTrain;
    }

    public INDArrayDataSetIterator getIteratorTrainNormalized(){
        System.out.println("***** Normalize data train *****");
        normalize(iteratorTrain);
        return iteratorTrain;
    }

    public INDArrayDataSetIterator getIteratorTestNormalized(){
        System.out.println("***** Normalize data test *****");
        normalize(iteratorTest);
        return iteratorTest;
    }

    public List<DataSet> getTrainDataList(){
        return asList(iteratorTrain);
    }

    public List<DataSet> getTestDataList(){
        return asList(iteratorTest);
    }

    public List<DataSet> getTrainDataListNormalized(){
        normalize(iteratorTrain);
        return asList(iteratorTrain);
    }

    public List<DataSet> getTestDataListNormalized(){
        normalize(iteratorTest);
        return asList(iteratorTest);
    }

    public DataReader(String workFolder, int trainRatio){
        this.workFolder = workFolder;
        this.trainRatio = trainRatio;
        File folder = new File(workFolder);
        File[] listOfFiles = folder.listFiles();
        List<String> fileNames = new ArrayList<>();
        for(File file : listOfFiles){
            try{
                fileNames.add(file.getCanonicalPath());
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        this.filenames = fileNames;
        this.batchSize = listOfFiles.length;
        System.out.println("Nombre de fichier d'exemple dans la liste: " + fileNames.size());
    }

    private INDArray getData(String path){
        try{
            NiftiVolume volume = NiftiVolume.read(path);
            int nx = volume.header.dim[1];
            int ny = volume.header.dim[2];
            int nz = volume.header.dim[3];
            int dim = volume.header.dim[4];
            int w = 0;
            if(dim == 0){
                dim = 1;
            }
            double[] tab = new double[nx*ny*nz*dim];
            for(int d = 0; d < dim; d++){
                for(int k = 0; k < nz; k++){
                    for(int j = 0; j < ny; j++){
                        for(int i = 0; i < nx; i++){
                            tab[w] = volume.data.get(i,j,k,d);
                            w++;
                        }
                    }
                }
            }
            INDArray array = Nd4j.create(tab, new int[]{1, 1, 2880, 2048});
            return array;

        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void createDataSet(int minibatchSize, Hashtable<String, INDArray> regLabel){
        System.out.println("Create dataset...");

        int nbTrain = 0;
        List<INDArray> labelsTrain = new ArrayList<>();
        List<INDArray> labelsTest = new ArrayList<>();
        List<INDArray> featuresTrain = new ArrayList<>();
        List<INDArray> featuresTest = new ArrayList<>();
        Enumeration<String> a = regLabel.keys();

        int hctrain = 0;
        int adtrain = 0;
        int adTest = 0;
        int hcTest = 0;

        for(String path : filenames){
            Enumeration e = regLabel.keys();
            while(e.hasMoreElements()){
                String key = (String) e.nextElement();
                Pattern pattern = Pattern.compile(key);
                Matcher matcher = pattern.matcher(path);
                if(matcher.find()){
                    INDArray array = getData(path);
                    if(array != null){
                        if(nbTrain < trainRatio/10){
                            featuresTrain.add(array);
                            labelsTrain.add(regLabel.get(key));
                            nbTrain++;
                            if(key.equals("HC")){
                                hctrain++;
                            }else if (key.equals("AD")){
                                adtrain++;
                            }
                        }else if(nbTrain >= trainRatio/10 && nbTrain < 10){
                            featuresTest.add(array);
                            labelsTest.add(regLabel.get(key));
                            nbTrain++;
                            if(key.equals("HC")){
                                hcTest++;
                            }else if (key.equals("AD")){
                                adTest++;
                            }
                            if(nbTrain == 10){
                                nbTrain = 0;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("HC train: " + hctrain);
        System.out.println("AD train: " + adtrain);
        System.out.println("HC Test: " + hcTest);
        System.out.println("Ad test: " + adTest);
        System.out.println("Taille du train set: " + featuresTrain.size());
        System.out.println("Taille du test set: " + featuresTest.size());

        System.out.println(labelsTest.get(1));

        iteratorTrain = createIterator(featuresTrain, labelsTrain, minibatchSize);
        iteratorTest = createIterator(featuresTest, labelsTest, 1);
    }

    private INDArrayDataSetIterator createIterator(List<INDArray> features, List<INDArray> labels, int batchSize){
        ArrayList<Pair> featureAndLabelTrain = new ArrayList<>();
        for(int i = 0; i < features.size(); i++){
            featureAndLabelTrain.add(new Pair(features.get(i), labels.get(i)));
        }
        Collections.shuffle(featureAndLabelTrain);
        Iterable featLabel = featureAndLabelTrain;
        return new INDArrayDataSetIterator(featLabel, batchSize);
    }

    private void normalize(INDArrayDataSetIterator iterator){
        System.out.println("***** Normalize data *****");
        DataNormalization scaler = new NormalizerMinMaxScaler();
        scaler.fit(iterator);
        iterator.setPreProcessor(scaler);
    }

    private List<DataSet> asList(INDArrayDataSetIterator iterator){
        List<DataSet> listData = new ArrayList<>();
        while(iterator.hasNext()){
            listData.add(iterator.next());
        }
        return listData;
    }
}
