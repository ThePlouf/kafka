package be.pdty.kafka.client;

import java.math.BigDecimal;
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
		String reference=UUID.randomUUID().toString();
		Account mine=new Account("123456");
		Account theirs=new Account("654321");
		TransferRequest tr=new TransferRequest(reference,mine,theirs,BigDecimal.valueOf(17.0));
		
		try(Producer<String,TransferRequest> producer = kafkaTemplate.getProducerFactory().createProducer()) {
			kafkaTemplate.send(requestTopic,reference,tr);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApp.class,args);
	}
}
