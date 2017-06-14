import niftijio.NiftiHeader;
import niftijio.NiftiVolume;
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
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
    private int nbDim;
    private int x;
    private int y;
    private int z;
    private float[] voxelDim;
    private NiftiVolume volume;

    public DataInput(String path){
        dataPath = path;
        try{
            volume = NiftiVolume.read(dataPath + "1.nii.gz");
            nbDim = volume.header.dim[0];
            x = volume.header.dim[1];
            y = volume.header.dim[2];
            z = volume.header.dim[3];
            voxelDim = volume.header.pixdim;
        }catch(IOException e){
            System.out.println("Error reading NIFTI file");
            e.printStackTrace();
        }
    }

    public void printInfo(){
        System.out.println("***** Datas Input Info *****");
        System.out.println("Chemin d'acces aux fichiers: " + dataPath);
        System.out.println("Nombre de dimension des fichiers: " + nbDim);
        System.out.println("Shape du fichier: x = " + volume.header.dim[1] + " y = " + volume.header.dim[2] + " z = " + volume.header.dim[2]);
        System.out.println("Pixdim: " + voxelDim[0] + " " + voxelDim[1] + " " + voxelDim[2] + " " + voxelDim[3] + " " + voxelDim[4]+ " " + voxelDim[5] + " " + voxelDim[6] + " " + voxelDim[7]);
        System.out.println("****************************");
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
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

            return Nd4j.create(tab, new int []{1, 16, 160, 36864}, 'c');

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
            System.out.println("label du nifti " + i + ": " + labels.get(i));
        }
        System.out.println("Size dataset: " + featureAndLabel.size());
        Iterable featLab = featureAndLabel;
        INDArrayDataSetIterator ds = new INDArrayDataSetIterator(featLab, 1);
        return ds;
    }
}
