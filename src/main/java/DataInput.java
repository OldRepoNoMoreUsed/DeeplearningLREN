import niftijio.NiftiHeader;
import niftijio.NiftiVolume;
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nicolas on 01.06.17.
 */
public class DataInput {
    private String dataPath;
    private int x;
    private int y;
    private int z;

    public DataInput(String path){
        this.dataPath = path;
        try{
            NiftiVolume volume = NiftiVolume.read(dataPath);
            this.x = volume.header.dim[1];
            this.y = volume.header.dim[2];
            this.z = volume.header.dim[3];
        }catch(IOException e){
            System.out.println("Error reading NIFTI file");
            e.printStackTrace();
        }
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public INDArray getData(){
        try{
            NiftiVolume volume = NiftiVolume.read(dataPath);
            int nx = volume.header.dim[1];
            int ny = volume.header.dim[2];
            int nz = volume.header.dim[3];
            int dim = volume.header.dim[4];

            double[] tab = new double[nx * ny * nz * dim];
            int w = 0;
            if(dim == 0) dim = 1;
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

            INDArray arr = Nd4j.create(tab, new int []{dim, nx, ny, nz}, 'c');
            //System.out.println(arr.toString());
            System.out.println("Shape NIFTI: " + Arrays.toString(arr.shape()));
            return arr;
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public INDArray getData(String path){
        try{
            NiftiVolume volume = NiftiVolume.read(path);
            int nx = volume.header.dim[1];
            int ny = volume.header.dim[2];
            int nz = volume.header.dim[3];
            int dim = volume.header.dim[4];

            double[] tab = new double[nx * ny * nz * dim];
            int w = 0;
            if(dim == 0) dim = 1;
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

            INDArray arr = Nd4j.create(tab, new int []{dim, nx, ny, nz}, 'c');
            System.out.println("Shape NIFTI: " + Arrays.toString(arr.shape()));
            return arr;
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public INDArrayDataSetIterator getDataSetIterator(){
        INDArray negLabel = Nd4j.rand(2, 2);
        INDArray posLabel = Nd4j.rand(2, 2);

        List<INDArray> labels = new ArrayList<INDArray>();
        List<INDArray> datas = new ArrayList<INDArray>();

        for(int i = 1; i <= 15; i++){
            INDArray arr = getData("src/main/resources/3D/sub" + i + ".nii.gz");
            if(arr != null){
                datas.add(arr);
                if(i%2 == 0){
                    labels.add(negLabel);
                }else{
                    labels.add(posLabel);
                }
            }
        }
        ArrayList<Pair> featureAndLabel = new ArrayList<Pair>();
        for(int i = 0; i < datas.size(); i++){
            featureAndLabel.add(new Pair(datas.get(i), labels.get(i)));
        }
        System.out.println("Size dataset: " + featureAndLabel.size());
        Iterable featLab = featureAndLabel;
        INDArrayDataSetIterator ds = new INDArrayDataSetIterator(featLab, 1);
        return ds;
    }
}
