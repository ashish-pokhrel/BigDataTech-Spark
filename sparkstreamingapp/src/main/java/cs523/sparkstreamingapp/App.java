package cs523.sparkstreamingapp;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class App {
    public static void main(String[] args) throws InterruptedException {
        String zkQuorum = "localhost:2181";
        String groupId = "my-group";
        String topic = "logstwitter";

        SparkConf sparkConf = new SparkConf().setAppName("App").setMaster("local[*]");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkContext, new Duration(5000)); // Batch interval

        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put(topic, 1);

        Set<String> topics = new HashSet<>(Arrays.asList(topic));

        JavaPairReceiverInputDStream<String, String> kafkaStream = KafkaUtils.createStream(
                streamingContext,
                zkQuorum,
                groupId,
                topicMap
        );

        JavaDStream<String> messages = kafkaStream.map(Tuple2::_2);

        messages.foreachRDD(rdd -> {
            rdd.foreach(message -> {
                System.out.println("Received message: " + message);
            });
        });

        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
