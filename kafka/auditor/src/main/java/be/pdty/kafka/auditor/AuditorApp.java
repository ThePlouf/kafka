package be.pdty.kafka.auditor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

import be.pdty.kafka.common.AccountUpdate;
import be.pdty.kafka.common.TransferRequestError;

@SpringBootApplication
public class AuditorApp {

	@KafkaListener(topics = "${request-topic}")
	public void processRequest(ConsumerRecord<String,AccountUpdate> record) {
		System.out.println(Thread.currentThread().getName()+" Request "+record.key()+" received and pending validation");
	}

	@KafkaListener(topics = "${after-check-topic}")
	public void processAfterCheck(ConsumerRecord<String,AccountUpdate> record) {
		System.out.println(Thread.currentThread().getName()+" Request "+record.key()+" validated and pending AML");
	}
	
	@KafkaListener(topics = "${after-aml-topic}")
	public void processAfterAML(ConsumerRecord<String,AccountUpdate> record) {
		System.out.println(Thread.currentThread().getName()+" Request "+record.key()+" passed AML and pending processing");
	}

	@KafkaListener(topics = "${completed-topic}")
	public void processCompleted(ConsumerRecord<String,AccountUpdate> record) {
		System.out.println(Thread.currentThread().getName()+" Request "+record.key()+" completed");
	}

	@KafkaListener(topics = "${error-topic}")
	public void processFailure(ConsumerRecord<String,TransferRequestError> record) {
		System.out.println(Thread.currentThread().getName()+" Request "+record.key()+" failed: "+record.value().message);
	}
	
	@KafkaListener(topics = "account-aggregated",properties={"value.deserializer=org.apache.kafka.common.serialization.IntegerDeserializer"})
	public void processAggregatedUpdate(ConsumerRecord<String,Integer> record) {
		System.out.println(Thread.currentThread().getName()+" Aggregated "+record.key()+": "+record.value());
	}

	public static void main(String[] args) {
		SpringApplication.run(AuditorApp.class,args);
	}
}
