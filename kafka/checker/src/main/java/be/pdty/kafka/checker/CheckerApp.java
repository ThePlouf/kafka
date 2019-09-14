package be.pdty.kafka.checker;

import java.math.BigDecimal;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import be.pdty.kafka.common.Account;
import be.pdty.kafka.common.TransferRequest;
import be.pdty.kafka.common.TransferRequestError;

@SpringBootApplication
public class CheckerApp {
	@Autowired
	private KafkaTemplate<String,Object> kafkaTemplate;

	@Value("${target-topic}")
	private String targetTopic;

	@Value("${error-topic}")
	private String errorTopic;

	private void checkValidAccount(Account account) throws Exception {
		if(account.number==null) throw new Exception("Account must have number");
		if(!account.number.equals("123456") && !account.number.equals("654321")) throw new Exception("Invalid account number");
	}
	
	@KafkaListener(topics = "${source-topic}")
	public void processMessage(ConsumerRecord<String,TransferRequest> record) {
		try(Producer<String,Object> producer = kafkaTemplate.getProducerFactory().createProducer()) {
			
			try {
				TransferRequest request=record.value();
				if(request.amount==null || request.beneficiary==null || request.issuer==null || request.reference==null) throw new Exception("Mandatory field missing");
				if(request.amount.compareTo(BigDecimal.ZERO)<=0) throw new Exception("Only positive amount allowed");
				if(!request.reference.equals(record.key())) throw new Exception("Transaction reference mismatch");
				checkValidAccount(request.issuer);
				checkValidAccount(request.beneficiary);
				if(request.issuer.number.equals(request.beneficiary.number)) throw new Exception("Transaction issuer and beneficiary must be different");
				
				kafkaTemplate.send(targetTopic,record.key(),request);
			} catch(Exception e) {
				kafkaTemplate.send(errorTopic,record.key(),new TransferRequestError(record.value(), e.getMessage()));
			}
			
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(CheckerApp.class,args);
	}
}
