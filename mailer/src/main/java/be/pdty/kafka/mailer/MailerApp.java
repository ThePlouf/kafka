package be.pdty.kafka.mailer;

import java.math.BigDecimal;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

import be.pdty.kafka.common.AccountUpdate;
import be.pdty.kafka.common.TransferRequest;
import be.pdty.kafka.common.TransferRequestError;

@SpringBootApplication
public class MailerApp {

	@KafkaListener(topics = "${source-topic}")
	public void processUpdate(ConsumerRecord<String,AccountUpdate> record) {
		AccountUpdate update=record.value();
		if(update.updateAmount.compareTo(BigDecimal.ZERO)>0) {
			System.out.println("[TO: "+update.account.number+"] Dear customer, you have received "+update.updateAmount+" from "+update.source.number);
		} else {
			System.out.println("[TO: "+update.account.number+"] Dear customer, your transfer of "+update.updateAmount.negate()+" to "+update.source.number+" has been completed");
		}
	}
	
	@KafkaListener(topics = "${error-topic}")
	public void processFailure(ConsumerRecord<String,TransferRequestError> record) {
		TransferRequest request = record.value().request;
		System.out.println("[TO: "+request.issuer.number+"] Dear customer, your transfer request failed due to "+record.value().message);
	}
	

	public static void main(String[] args) {
		SpringApplication.run(MailerApp.class,args);
	}
}
