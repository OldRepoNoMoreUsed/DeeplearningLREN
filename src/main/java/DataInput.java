import niftijio.NiftiHeader;
import niftijio.NiftiVolume;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.Arrays;

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

    public INDArray getget(){
        try{
            NiftiVolume volume = NiftiVolume.read(dataPath);
            int nx = volume.header.dim[1];
            int ny = volume.header.dim[2];
            int nz = volume.header.dim[3];
            int dim = volume.header.dim[4];

//            log.info("datatype: " + NiftiHeader.decodeDatatype(volume.header.datatype));
//            log.info("dimension: " + nx + " " + ny + " " + nz + " " + dim);

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

            INDArray arr = Nd4j.create(tab, new int []{nx, ny, nz}, 'c');
            System.out.println(arr.toString());
            System.out.println("Shape: " + Arrays.toString(arr.shape()));
            return arr;
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
