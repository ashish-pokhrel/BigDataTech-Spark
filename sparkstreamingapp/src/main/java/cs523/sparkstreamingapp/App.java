package cs523.sparkstreamingapp;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
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
        String zkQuorum = "localhost:2181"; // Zookeeper quorum for Kafka
        String groupId = "my-group"; // Replace with your consumer group ID
        String topic = "logstwitter"; // Replace with the Kafka topic you want to consume from

        SparkConf sparkConf = new SparkConf().setAppName("App").setMaster("local[*]");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkContext, new Duration(5000)); // Batch interval

        // Set up Kafka parameters
        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put(topic, 1);

        // Create a set of Kafka topics to subscribe to
        Set<String> topics = new HashSet<>(Arrays.asList(topic));

        // Create the Kafka stream using KafkaUtils.createStream
        JavaPairReceiverInputDStream<String, String> kafkaStream = KafkaUtils.createStream(
                streamingContext,
                zkQuorum, // Zookeeper quorum for Kafka
                groupId, // Consumer group ID
                topicMap // Map of (topic -> numThreads) to consume from
        );

        // Extract the values (messages) from the Kafka stream
        JavaDStream<String> messages = kafkaStream.map(Tuple2::_2);

        // Process the messages (you can replace this with your own processing logic)
        messages.foreachRDD(rdd -> {
            rdd.foreach(message -> {
                System.out.println("Received message: " + message);
                // Add your custom processing logic here
            });
        });

        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
