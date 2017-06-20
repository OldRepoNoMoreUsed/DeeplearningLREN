import niftijio.NiftiVolume;
import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
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

    public void generateNIFTISphere(int niftiSize, int sphereSize, int offsetX, int offsetY, int offsetZ, String name) throws IOException{
        int nx = niftiSize;
        int ny = niftiSize;
        int nz = niftiSize;
        int dim = 1;

        int centerX = offsetX + sphereSize/2;
        int centerY = offsetY + sphereSize/2;
        int centerZ = offsetZ + sphereSize/2;

        NiftiVolume sphere = new NiftiVolume(nx, ny, nz, dim);
        for(int d = 0; d < dim; d++){
            for(int z = 0; z < nz; z++){
                for(int y = 0; y < ny; y++){
                    for(int x = 0; x < nx; x++){
                        int distanceToCentre = (int) Math.abs(Math.sqrt(Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2) + Math.pow(centerZ - z, 2)));
                        if(distanceToCentre <= sphereSize){
                            sphere.data.set(x, y, z, d, 255.0);
                        }else{
                            sphere.data.set(x, y, z, d, 0.0);
                        }
                    }
                }
            }
        }
        sphere.write("generate/" + name + ".nii.gz");
        System.out.println("Sphere NIFTI generate: " + name);
    }

    public void generateNIFTICube(int niftiSize, int cubeSize, int offsetX, int offsetY, int offsetZ, String name) throws IOException{
        int nx = niftiSize;
        int ny = niftiSize;
        int nz = niftiSize;
        int dim = 1;

        NiftiVolume volume = new NiftiVolume(nx, ny ,nz, dim);
        //Cube generation
        for(int d = 0; d < dim; d++){
            for(int z = 0; z < nz; z++){
                for(int y = 0; y < ny; y++){
                    for(int x = 0; x < nx; x++){
                        if(z == offsetZ || z == offsetZ + cubeSize){
                            if(y == offsetY || y == offsetY + cubeSize){
                                if(x >= offsetX && x <= offsetX + cubeSize){
                                    volume.data.set(x, y, z, d, 255.0);
                                }else{
                                    volume.data.set(x, y, z, d, 0.0);
                                }
                            }
                            if(y > offsetY && y <= offsetY + cubeSize-1){
                                if(x == offsetX || x == offsetX + cubeSize){
                                    volume.data.set(x, y, z, d, 255.0);
                                }else{
                                    volume.data.set(x, y, z, d, 0.0);
                                }
                            }
                        }else if(z > offsetZ && z <= offsetZ + cubeSize-1){
                            if(y > offsetY && y <= offsetY + cubeSize -1){
                                if(x == offsetX || x == offsetX + cubeSize ){
                                    volume.data.set(x, y, z, d, 255.0);
                                }else{
                                    volume.data.set(x, y, z, d, 0.0);
                                }
                            }
                        }else{
                            volume.data.set(x, y, z, d, 0.0);
                        }
                    }
                }
            }
        }
        volume.write("generate/" + name + ".nii.gz");
        System.out.println("Cube NIFTI generated: " + name);
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
