package cs523.kafkaproducer;
import org.apache.kafka.clients.producer.*;
import java.util.Properties;
import java.util.Random;

public class App {
    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092";
        String topic = "logstwitter";

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(properties);
        Random random = new Random();

        try {
            while (true) {
                String logMessage = "This is a re occurring log entry: " + random.nextInt(1000);
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, logMessage);
                producer.send(record);
                Thread.sleep(5000); // Sleep for 1 second
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}