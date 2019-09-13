package test;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class ProducerApp {
	@Autowired
	private KafkaTemplate<Integer,String> kafkaTemplate;
	

	@PostConstruct
	private void sendOneMessage() throws Exception {
		try(Producer<Integer,String> producer = kafkaTemplate.getProducerFactory().createProducer()) {
			producer.send(new ProducerRecord<Integer,String>("test",17,"hello world")).get();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(ProducerApp.class,args);
	}
}
