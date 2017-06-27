import niftijio.NiftiVolume;
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicolas on 21.06.17.
 */
public class DataReader {
    private INDArrayDataSetIterator trainDataSetIterator;
    private INDArrayDataSetIterator testDataSetIterator;

    private INDArray cubeLabel = Nd4j.create(new float[]{1, 0, 1, 0}, new int[]{2, 2});
    private INDArray sphereLabel = Nd4j.create(new float[]{0, 1, 0, 1}, new int[]{2, 2});

    public INDArrayDataSetIterator getTrainDataSetIterator(){
        return trainDataSetIterator;
    }

    public INDArrayDataSetIterator getTestDataSetIterator(){
        return testDataSetIterator;
    }

    public int[] getMetaData(String path){
        try{
            NiftiVolume volume = NiftiVolume.read(path);
            return new int[]{volume.header.dim[1], volume.header.dim[2], volume.header.dim[3], volume.header.dim[4]};
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private double[] readData(String path){
        try{
            NiftiVolume volume = NiftiVolume.read(path);
            int nx = volume.header.dim[1];
            int ny = volume.header.dim[2];
            int nz = volume.header.dim[3];
            int dim = volume.header.dim[4];

            if(dim == 0){
                dim = 1;
            }

            int w = 0;
            double[] tab = new double[nx * ny * nz * dim];

            for(int d = 0; d < dim; d++){
                for(int k = 0; k < nz; k++){
                    for(int j = 0; j < ny; j++){
                        for(int i = 0; i < nx; i++){
                            tab[w] = volume.data.get(i, j, k, d);
                            w++;
                        }
                    }
                }
            }
            return tab;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void load(ArrayList<String> paths){
        ArrayList<String> pathTrain = new ArrayList<>();
        ArrayList<String> pathTest = new ArrayList<>();

        for(int i = 0; i <= 582; i++){
            pathTrain.add(paths.get(i));
        }
        loadDataSetIterator(pathTrain, trainDataSetIterator);

        for(int i = 583; i < paths.size(); i++){
            pathTest.add(paths.get(i));
        }
        loadDataSetIterator(pathTest, testDataSetIterator);
    }

    private void loadDataSetIterator(ArrayList<String> paths, INDArrayDataSetIterator dataSetIterator){
        System.out.println("************************************************************");
        ArrayList<INDArray> features = new ArrayList<>();
        ArrayList<INDArray> labels = new ArrayList<>();

        for(int i = 0; i < paths.size(); i++){
            INDArray array = Nd4j.create(readData(paths.get(i)), new int[]{1, 1, 1000, 1000});
            features.add(array);
            if(paths.get(i).contains("cube")){
                labels.add(cubeLabel);
            }
            else{
                labels.add(sphereLabel);
            }
        }
        ArrayList<Pair> featureAndLabelTrain = new ArrayList<>();
        for(int i = 0; i < features.size(); i++){
            featureAndLabelTrain.add(new Pair(features.get(i), labels.get(i)));
        }
        Iterable featLabel = featureAndLabelTrain;
        trainDataSetIterator = new INDArrayDataSetIterator(featLabel, features.size());
        System.out.println("Paths size: " + paths.size());
        System.out.println("Features size: " + features.size());
        System.out.println("Labels size: " + labels.size());

    }
}
