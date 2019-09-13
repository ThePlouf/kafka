package test;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class TestProducer {
	public static void main(String[] args)  throws Exception {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);

		Properties properties = new Properties();
		properties.put("bootstrap.servers", "127.0.0.1:9092");
		properties.put("client.id", "TestProducer");
		properties.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		
		try(KafkaProducer<Integer,String> producer = new KafkaProducer<Integer,String>(properties)) {
			producer.send(new ProducerRecord<Integer,String>("test",17,"hello world")).get();
		}
	}
}
