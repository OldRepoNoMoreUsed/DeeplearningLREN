package wrapper.local;

import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import wrapper.WrapperDl4j;

/**
 * Created by nicolas on 13.07.17.
 */
public class LocalWrapperDl4j extends WrapperDl4j{

    public LocalWrapperDl4j(int seed){
        super(seed);
    }

    public void init(){
        System.out.println("Init local network");
        network = new MultiLayerNetwork(conf);
        //network.setListeners(new StatsListener(statsStorage));
        network.init();
    }

    public void localTrain(INDArrayDataSetIterator iteratorTrain){
        System.out.println("Training local...");
        network.fit(iteratorTrain);
        System.out.println("Done!");
    }

    public void localEvaluation(INDArrayDataSetIterator iteratorTest){
        System.out.println("Evaluation local...");
        Evaluation evaluation = new Evaluation();
        while(iteratorTest.hasNext()){
            DataSet next = iteratorTest.next();
            INDArray prediction = network.output(next.getFeatureMatrix());
            System.out.println("Valeur du label: " + next.getLabels());
            System.out.println("Valeur de la prediction: " + prediction);
            evaluation.eval(next.getLabels(), prediction);
        }
        System.out.println("Done!");
        System.out.println(evaluation.stats());
    }
}
