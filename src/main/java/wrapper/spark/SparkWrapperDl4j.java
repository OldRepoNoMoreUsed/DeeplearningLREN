package wrapper.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.spark.api.TrainingMaster;
import org.deeplearning4j.spark.impl.multilayer.SparkDl4jMultiLayer;
import org.deeplearning4j.spark.impl.paramavg.ParameterAveragingTrainingMaster;
import org.nd4j.linalg.dataset.DataSet;
import wrapper.WrapperDl4j;

import java.util.List;

/**
 * Created by nicolas on 13.07.17.
 */
public class SparkWrapperDl4j extends WrapperDl4j{
    private TrainingMaster trainingMaster;
    private JavaSparkContext jsc;
    private SparkDl4jMultiLayer sparkNet;

    public SparkWrapperDl4j(long seed, String sparkTimeout, String sparkHeartBeatInterval, String sparkMaster, String appName){
        super(seed);
        System.out.println("Get Spark configuration");
        SparkConf sparkConf = new SparkConf();
        sparkConf.set("wrapper.spark.network.timeout", sparkTimeout);
        sparkConf.set("wrapper.spark.executor.heartbeatInterval", sparkHeartBeatInterval);
        sparkConf.setMaster(sparkMaster);
        sparkConf.setAppName(appName);
        this.jsc = new JavaSparkContext(sparkConf);
        System.out.println("Done!");
    }

    public void initTrainingMaster(int rddDataSetNumExamples, int averagingFrequency, int workerNumBatch, int batchSizeByWorker){
        System.out.println("Init training master...");
        this.trainingMaster = new ParameterAveragingTrainingMaster.Builder(rddDataSetNumExamples)
                .averagingFrequency(averagingFrequency)
                .workerPrefetchNumBatches(workerNumBatch)
                .batchSizePerWorker(batchSizeByWorker)
                .build();
    }

    public void initSparkNet(){
        System.out.println("Init spark network...");
        sparkNet = new SparkDl4jMultiLayer(jsc, conf, trainingMaster);
        System.out.println("Done!");
    }

    public void sparkTrain(List<DataSet> listTrainData){
        System.out.println("Training with Spark");
        JavaRDD<DataSet> trainData = jsc.parallelize(listTrainData);
        trainData.persist(StorageLevel.DISK_ONLY());
        for(int i = 0; i < iteration; i++){
            sparkNet.fit(trainData);
        }
    }

    public void sparkEval(List<DataSet> listTestData){
        System.out.println("Evaluation with spark");
        JavaRDD<DataSet> testData = jsc.parallelize(listTestData);
        testData.persist(StorageLevel.DISK_ONLY());
        Evaluation eval = sparkNet.evaluate(testData);
        System.out.println(eval.stats());
        trainingMaster.deleteTempFiles(jsc);
    }
}
