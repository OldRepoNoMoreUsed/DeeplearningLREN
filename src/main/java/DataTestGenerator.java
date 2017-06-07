import org.apache.commons.lang.ArrayUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Created by nicolas on 07.06.17.
 */
public class DataTestGenerator {

    private static int k;
    private static int l;
    private static int m;
    private static int n;

    public DataTestGenerator(){
        this.k = 3;
        this.l = 3;
        this.m = 3;
        this.n = 3;
    }

    public DataTestGenerator(int k, int l, int m, int n){
        this.k = k;
        this.l = l;
        this.m = m;
        this.n = n;
    }

    public INDArray generateINDArray(){
        INDArray npArray = Nd4j.rand(l*m, n).reshape(l, m, n);
        System.out.println("******* Array Generated *******");
        System.out.println("Shape: " + ArrayUtils.toString(npArray.shape()));
        System.out.println("Array: " + npArray);
        return npArray;
    }

    public INDArray generateNegativeINDArray(){
        INDArray npArray = generateINDArray();
        npArray.mul(-1);
        System.out.println("******* Negative Array Generated *******");
        System.out.println("Shape: " + ArrayUtils.toString(npArray.shape()));
        System.out.println("Array: " + npArray);
        return npArray;
    }
}
