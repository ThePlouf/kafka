package test;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
public class ConsumerApp {

	@KafkaListener(topics = "test")
	public void processMessage(ConsumerRecord<Integer,String> record) {
		System.err.println("received message "+record.key()+":"+record.value());
	}

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApp.class,args);
	}
}
