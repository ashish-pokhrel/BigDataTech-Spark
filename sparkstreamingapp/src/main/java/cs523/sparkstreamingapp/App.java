package cs523.sparkstreamingapp;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.flume.FlumeUtils;
import org.apache.spark.streaming.flume.SparkFlumeEvent;

public class App {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("KafkaToSparkStreaming").setMaster("local[*]");

        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(1));

        JavaReceiverInputDStream<SparkFlumeEvent> flumeStream = FlumeUtils.createStream(streamingContext, "localhost", 41414);

        flumeStream.print();

        streamingContext.start();
        try {
			streamingContext.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
