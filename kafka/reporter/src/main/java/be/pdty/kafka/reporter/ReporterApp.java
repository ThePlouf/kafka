package be.pdty.kafka.reporter;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Properties;


@SpringBootApplication
@EnableScheduling
public class ReporterApp {
	@Autowired
	public ReporterApp(KafkaStreams streams) {
		this.streams = streams;
	}

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;
	
	@Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}")
	private String trustedPackages;
	
	private final KafkaStreams streams;
	

	private Properties getStreamsConfiguration() {
		
		Properties streamsConfiguration = new Properties();
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-reporter");
		streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "bank-reporter-client");
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		return streamsConfiguration;
	}	
	
	
	@Bean
	public KafkaStreams getStreams() {
		StreamsBuilder builder = new StreamsBuilder();
		
		KeyValueBytesStoreSupplier storeSupplier = Stores.inMemoryKeyValueStore("account-aggregated-store");
		
		builder.globalTable("account-aggregated",Materialized.<String,Integer>as(storeSupplier).withKeySerde(Serdes.String()).withValueSerde(Serdes.Integer()));
		
		KafkaStreams streams = new KafkaStreams(builder.build(), getStreamsConfiguration());
	    streams.cleanUp();
	    streams.start();
	   	    
	    
	    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
	    
	    
	    return streams;
	}
	
	@Scheduled(fixedRate = 1000)
	public void report() {
		if(streams.state() == State.RUNNING) {
		    ReadOnlyKeyValueStore<String,Integer> view = streams.store("account-aggregated-store", QueryableStoreTypes.keyValueStore());
		    
		    System.out.println("--");
		    try(KeyValueIterator<String,Integer> iterator=view.all()) {
		    	iterator.forEachRemaining(x->System.out.println("Credit for "+x.key+" is "+x.value));
		    }
		} else {
			System.out.println("Streams are not running ("+streams.state()+")");
		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ReporterApp.class,args).getBean(ReporterApp.class);
	}

}
