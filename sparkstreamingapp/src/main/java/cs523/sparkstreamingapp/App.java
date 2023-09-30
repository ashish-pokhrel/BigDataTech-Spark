package cs523.sparkstreamingapp;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
public class App {
    static int c = 0;
    public static void main(String[] args) throws Exception {
        List<String> kafkaMessages = new ArrayList<>();
        String zkQuorum = "localhost:2181";
        String groupId = "my-group";
        String topic = "logstwit";

        SparkConf sparkConf = new SparkConf().setAppName("App").setMaster("local[*]");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkContext, new Duration(5000)); // Batch interval
        // Set up Kafka parameters
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
                System.out.println(" ");
                System.out.println(" ");
                System.out.println("Received message: " + message);
                System.out.println(" ");
                System.out.println(" ");
//                kafkaMessages.add(message);
                saveToHBase(message);
            });
        });

        streamingContext.start();
        streamingContext.awaitTermination();

    }

    private static void saveToHBase(String message) {
        try {
            Configuration conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.quorum", "localhost");
            conf.set("hbase.zookeeper.property.clientPort", "2183");
            conf.set("hbase.master", "localhost:16010");

            Connection connection = ConnectionFactory.createConnection(conf);
            Admin admin = connection.getAdmin();
            System.out.println("Table cra.");
            // Define the table name and column family
            TableName tableName = TableName.valueOf("STUDENTSTABLE");

            Table table = connection.getTable(tableName);

            // Create a Put object to specify the row key
            Put put = new Put(Bytes.toBytes("row1")); // Change "row1" to your desired row key

            // Add data to the Put object
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("column"+c++), Bytes.toBytes(message));
            table.put(put);
            // Close the table when done
            table.close();
            System.out.println("Table created successfully.");

            admin.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
