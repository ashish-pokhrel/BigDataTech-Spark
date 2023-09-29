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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
public class App {
    public static void main(String[] args) throws Exception {

        String zkQuorum = "localhost:2181";
        String groupId = "my-group";
        String topic = "logstwitter1";

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
        // Configuration
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "localhost"); // ZooKeeper quorum
        config.set("hbase.zookeeper.property.clientPort", "2182");
        Connection connection = ConnectionFactory.createConnection(config);
        TableName tableName = TableName.valueOf("records");
        Table table = connection.getTable(tableName);

        HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
        tableDescriptor.addFamily(new HColumnDescriptor("cf"));
        Admin admin = connection.getAdmin();
        if (!admin.tableExists(tableName)) {
            admin.createTable(tableDescriptor);
        }

        messages.foreachRDD(rdd -> {
            rdd.foreach(message -> {
                System.out.println("Received message: " + message);
                Put put = new Put(Bytes.toBytes("row1"));
                put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("column"), Bytes.toBytes(message));
                table.put(put);
                table.close();
                System.out.println("Row inserted successfully.");
            });
        });
        connection.close();
        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
