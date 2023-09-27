package cs523.kafkaproducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import twitter4j.*;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import java.util.Properties;

public class App {

    private static String BEARER_TOKEN = ""; // add bearer token here

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Authorization", "Bearer "+BEARER_TOKEN)
                .uri(new URI("https://api.twitter.com/2/users/2521950180?user.fields=created_at,entities,public_metrics"))
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readValue(response.body(), JsonNode.class);

        System.out.println(response.statusCode());
        System.out.println(jsonNode.toString());

        String followersCount = jsonNode
                .get("data")
                .get("public_metrics")
                .get("followers_count")
                .asText();

        System.out.println("Followers Count : "+ followersCount);
    }
}
//    public static void main(String[] args) {
////    	ConfigurationBuilder cb = new ConfigurationBuilder();
////        cb.setDebugEnabled(true)
////                .setOAuthConsumerKey("f98nFOHKHL2N70bRc0HFU0hNY")
////                .setOAuthConsumerSecret("epObeeKnVBDSmXdT8PQMyzNZumiIfNZRcYWyZPvvuI5YKUnx7l")
////                .setOAuthAccessToken("1706855062662701056-4vuY83QpxEFjwgAYh7nF08stcIpErp")
////                .setOAuthAccessTokenSecret("NUwgha8CBaQW1mkcDD9Bsda4t77HNuAbitFlzmPAm6j8G");
//
//    	  // Set your Twitter API Bearer Token
//        String bearerToken = "AAAAAAAAAAAAAAAAAAAAAJSxqAEAAAAAYimPjQ5eYmSrX0QBLE9Kp1L4NFc%3Dj6Qg0E3qTUptZi8XN0V1y4SIxLGbkZNLqCsmghEwOkWtEe9U5n";
////        Configuration configuration = new ConfigurationBuilder()
////                .setDebugEnabled(true)
////                .setOAuth2TokenType("bearer")
////                .setOAuth2AccessToken(bearerToken)
////                .setOAuthConsumerKey("f98nFOHKHL2N70bRc0HFU0hNY")
////                .setOAuthConsumerSecret("epObeeKnVBDSmXdT8PQMyzNZumiIfNZRcYWyZPvvuI5YKUnx7l")
// //               .build();
//
//        Configuration cb = new ConfigurationBuilder()
//        	  .setDebugEnabled(true)
//              .setOAuthConsumerKey("f98nFOHKHL2N70bRc0HFU0hNY")
//              .setOAuthConsumerSecret("epObeeKnVBDSmXdT8PQMyzNZumiIfNZRcYWyZPvvuI5YKUnx7l")
//              .setOAuthAccessToken("1706855062662701056-4vuY83QpxEFjwgAYh7nF08stcIpErp")
//              .setOAuthAccessTokenSecret("NUwgha8CBaQW1mkcDD9Bsda4t77HNuAbitFlzmPAm6j8G")
//              .build();
//        
//        TwitterFactory tf = new TwitterFactory(cb);
//        Twitter twitter = tf.getInstance();
//
//        try {
//            // Create a query to fetch tweets containing specific keywords
//            Query query = new Query("Java programming");
//            QueryResult result = twitter.search(query);
//
//            // Process and display the tweets
//            for (Status tweet : result.getTweets()) {
//                System.out.println("@" + tweet.getUser().getScreenName() + ": " + tweet.getText());
//            }
//        } catch (TwitterException te) {
//            te.printStackTrace();
//            System.exit(-1);
//        }
//    }
//}
