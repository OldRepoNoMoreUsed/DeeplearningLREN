package wrapper.local;

import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.LocalFileModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import wrapper.WrapperDl4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicolas on 13.07.17.
 */
public class LocalWrapperDl4j extends WrapperDl4j{
    private UIServer uiServer;
    private StatsStorage statsStorage;

    public LocalWrapperDl4j(int seed, boolean isUseUI){
        super(seed);
        /*if(isUseUI){
            this.uiServer = UIServer.getInstance();
            this.statsStorage = new InMemoryStatsStorage();
            uiServer.attach(statsStorage);
            System.out.println("UI setting up...");
        }*/
    }

    public void init(StatsStorage ss){
        System.out.println("Init local network");
        network = new MultiLayerNetwork(conf);
        network.setListeners(new StatsListener(ss, 1));
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

    public void earlyStopTraining(INDArrayDataSetIterator dataTrain, INDArrayDataSetIterator dataTest, int maxEpoch, int maxMinutes){
        String directory = "modelSaved/";
        LocalFileModelSaver saver = new LocalFileModelSaver(directory);

        EarlyStoppingConfiguration esConf = new EarlyStoppingConfiguration.Builder()
                .epochTerminationConditions(new MaxEpochsTerminationCondition(maxEpoch))
                .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(maxMinutes, TimeUnit.MINUTES))
                .scoreCalculator(new DataSetLossCalculator(dataTest, true))
                .evaluateEveryNEpochs(1)
                .modelSaver(saver)
                .build();

        EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, conf, dataTrain);

        System.out.println("Early stop training...");
        EarlyStoppingResult result = trainer.fit();

        System.out.println("Termination reason: " + result.getTerminationReason());
        System.out.println("Termination details: " + result.getTerminationDetails());
        System.out.println("Total epochs: " + result.getTotalEpochs());
        System.out.println("Best epoch number: " + result.getBestModelEpoch());
        System.out.println("Score at the best epoch: " + result.getBestModelScore());

        File locationToSave = new File("modelSaved/earlyStopBestModel.zip");
        try{
            ModelSerializer.writeModel(result.getBestModel(), locationToSave, true);
        }catch(IOException e){
            e.printStackTrace();
        }

        Map<Integer, Double> scoreVsEpoch = result.getScoreVsEpoch();
        List<Integer> list = new ArrayList<>(scoreVsEpoch.keySet());
        Collections.sort(list);
        System.out.println("Score vs Epoch: ");
        for(Integer i : list){
            System.out.println(i + "\t" + scoreVsEpoch.get(i));
        }
    }

    public void earlyStopEvaluation(INDArrayDataSetIterator dataTest){
        try{
            this.network = ModelSerializer.restoreMultiLayerNetwork("modelSaved/earlyStopBestModel.zip");
        }catch(IOException e){
            e.printStackTrace();
        }

        this.localEvaluation(dataTest);
    }
}
