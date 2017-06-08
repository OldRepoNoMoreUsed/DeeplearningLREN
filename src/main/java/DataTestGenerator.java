import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicolas on 07.06.17.
 */
public class DataTestGenerator {

    private static int l;
    private static int m;
    private static int n;

    public DataTestGenerator(){
        this.l = 3;
        this.m = 3;
        this.n = 3;
    }

    public DataTestGenerator(int l, int m, int n){
        this.l = l;
        this.m = m;
        this.n = n;
    }

    public INDArray generateINDArray(){
        INDArray npArray = Nd4j.rand(l*m, n).reshape(l, m, n);
        System.out.println("******* Array Generated *******");
        System.out.println("Shape: " + ArrayUtils.toString(npArray.shape()));
        //System.out.println("Array: " + npArray);
        return npArray;
    }

    public INDArray generateNegativeINDArray(){
        INDArray npArray = generateINDArray();
        npArray.mul(-1);
        System.out.println("******* Negative Array Generated *******");
        System.out.println("Shape: " + ArrayUtils.toString(npArray.shape()));
        //System.out.println("Array: " + npArray);
        return npArray;
    }

    public INDArrayDataSetIterator generateDataSet(int nbData){
        INDArray negLabel = generateINDArray().reshape(9,3);
        INDArray posLabel = generateINDArray().reshape(9,3);
        List<INDArray> labels = new ArrayList<INDArray>();
        List<INDArray> features = new ArrayList<INDArray>();

        for(int i = 0; i < nbData; i++){
            if(i%2 == 0){
                labels.add(negLabel);
                features.add(generateNegativeINDArray().reshape(9, 3));
            }else{
                labels.add(posLabel);
                features.add(generateINDArray().reshape(9, 3));
            }
        }
        ArrayList<Pair> featuresAndLabels = new ArrayList<Pair>();
        for(int i = 0; i < features.size(); i++){
            featuresAndLabels.add(new Pair(features.get(i), labels.get(i)));
        }
        System.out.println("size dataset: " + featuresAndLabels.size());

        Iterable featLab = featuresAndLabels;
        INDArrayDataSetIterator ds = new INDArrayDataSetIterator(featLab, 1);
        return ds;
    }

}
