package be.pdty.kafka.accountant;

import be.pdty.kafka.common.AccountUpdate;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@SpringBootApplication
public class AccountantApp {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;
	
	@Value("${source-topic}")
	private String sourceTopic;

	@Value("${target-topic}")
	private String targetTopic;
	
	@Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}")
	private String trustedPackages;

	private Properties getStreamsConfiguration() {
		
		Properties streamsConfiguration = new Properties();
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-accountant");
		streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "bank-accountant-client");
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
		return streamsConfiguration;
	}	
	
	private void run() {
		Map<String, Object> serdeProps = new HashMap<>();
		   
		Serializer<AccountUpdate> accountUpdateSerializer = new JsonSerializer<>();
		serdeProps.put("JsonPOJOClass", AccountUpdate.class);
		accountUpdateSerializer.configure(serdeProps, false);
		
		Deserializer<AccountUpdate> accountUpdateDeserializer = new JsonDeserializer<>();
		serdeProps.put("JsonPOJOClass", AccountUpdate.class);
		serdeProps.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
		accountUpdateDeserializer.configure(serdeProps, false);

		Serde<AccountUpdate> accountSerdes = Serdes.serdeFrom(accountUpdateSerializer,accountUpdateDeserializer);
		
		StreamsBuilder builder = new StreamsBuilder();
	    KStream<String,AccountUpdate> stream = builder.stream(sourceTopic,Consumed.with(Serdes.String(),accountSerdes));
	    stream.foreach((k,u)->System.out.println(k+":"+u));
	    KGroupedStream<String,AccountUpdate> group = stream.groupBy((k,v)->v.account.number,Grouped.with(Serdes.String(),accountSerdes));
	    KTable<String,Integer> table = group.aggregate(()->0,(k,v1,v2)->v1.updateAmount.intValue()+v2,Materialized.with(Serdes.String(), Serdes.Integer()));
	    table.toStream().foreach((k,u)->System.out.println(k+":"+u));
	    table.toStream().to(targetTopic,Produced.with(Serdes.String(), Serdes.Integer()));
	    
	    @SuppressWarnings("resource")
		KafkaStreams streams = new KafkaStreams(builder.build(), getStreamsConfiguration());
	    streams.cleanUp();
	    streams.start();
	    
	    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));	    
	}
	
	public static void main(String[] args) {
		AccountantApp app = SpringApplication.run(AccountantApp.class,args).getBean(AccountantApp.class);
		app.run();
	}

}
