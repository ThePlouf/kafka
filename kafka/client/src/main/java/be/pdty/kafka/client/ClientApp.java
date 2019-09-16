package be.pdty.kafka.client;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

import be.pdty.kafka.common.Account;
import be.pdty.kafka.common.TransferRequest;

@SpringBootApplication
public class ClientApp {
	@Autowired
	private KafkaTemplate<String,TransferRequest> kafkaTemplate;
	
	@Value("${target-topic}")
	private String requestTopic;

	@PostConstruct
	private void issueTransfer() throws Exception {
		Account mine=new Account("123456");
		Account theirs=new Account("654321");
		
		Random random=new SecureRandom();
		
		
		try(Producer<String,TransferRequest> producer = kafkaTemplate.getProducerFactory().createProducer()) {
			for(int i=0;i<100_000;i++) {
				String reference=UUID.randomUUID().toString();
				TransferRequest tr=new TransferRequest(reference,random.nextBoolean()?mine:theirs,random.nextBoolean()?theirs:mine,BigDecimal.valueOf(random.nextInt(1000)-10));
				kafkaTemplate.send(requestTopic,reference,tr);
			}
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApp.class,args);
	}
}
