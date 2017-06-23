import niftijio.NiftiVolume;
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicolas on 21.06.17.
 */
public class DataReader {
    private ArrayList<INDArray> labels = new ArrayList<>();
    private List<INDArray> labelsTrain = new ArrayList<>();
    private List<INDArray> labelsTest = new ArrayList<>();
    private List<INDArray> featuresTrain = new ArrayList<>();
    private List<INDArray> featuresTest = new ArrayList<>();


    public int[] getMetaData(String path){
        try{
            NiftiVolume volume = NiftiVolume.read(path);
            return new int[]{volume.header.dim[1], volume.header.dim[2], volume.header.dim[3], volume.header.dim[4]};
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void load(String path, float[] label, int[] labelShape, boolean train){
        double[] tab = readData(path);
        INDArray array = Nd4j.create(tab, new int[]{1, 1, 1000, 1000});
        if(train){
            featuresTrain.add(array);
            labelsTrain.add(Nd4j.create(label, labelShape));
        }else{
            featuresTest.add(array);
            labelsTest.add(Nd4j.create(tab, labelShape));
        }
    }

    public INDArrayDataSetIterator getTrainIterator(){
        ArrayList<Pair> featureAndLabelTrain = new ArrayList<>();
        for(int i = 0; i < featuresTrain.size(); i++){
            featureAndLabelTrain.add(new Pair(featuresTrain.get(i), labelsTest.get(i)));
        }
        Iterable featureAndLabel = featureAndLabelTrain;
        return new INDArrayDataSetIterator(featureAndLabel, featureAndLabelTrain.size());
    }

    public INDArrayDataSetIterator getTestiterator(){
        ArrayList<Pair> featureAndLabelTest = new ArrayList<>();
        for(int i = 0; i < featuresTest.size(); i++){
            featureAndLabelTest.add(new Pair(featuresTest.get(i), labelsTest.get(i)));
        }
        Iterable featureAndLabel = featureAndLabelTest;
        return new INDArrayDataSetIterator(featureAndLabel, featureAndLabelTest.size());
    }

    private void createLabel(float[] tableau){
        INDArray label = Nd4j.create(tableau, new int[]{1,2});
        labels.add(label);
        System.out.println("Label added: " + label);
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
}
