package be.pdty.kafka.aml;

import be.pdty.kafka.common.TransferRequest;
import be.pdty.kafka.common.TransferRequestError;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

@SpringBootApplication
public class AMLApp {

	@Autowired
	public AMLApp(KafkaTemplate<String,Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	private final KafkaTemplate<String,Object> kafkaTemplate;

	@Value("${target-topic}")
	private String targetTopic;

	@Value("${error-topic}")
	private String errorTopic;

	@KafkaListener(topics = "${source-topic}")
	public void processMessage(ConsumerRecord<String,TransferRequest> record) {
		try(Producer<String,Object> producer = kafkaTemplate.getProducerFactory().createProducer()) {
			
			try {
				TransferRequest request=record.value();
				if(request.amount.compareTo(BigDecimal.valueOf(1_000_000))>=0) {
					throw new Exception("Amount too large");
				}
				kafkaTemplate.send(targetTopic,record.key(),request);
			} catch(Exception e) {
				kafkaTemplate.send(errorTopic,record.key(),new TransferRequestError(record.value(), e.getMessage()));
			}
			
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(AMLApp.class,args);
	}
}
