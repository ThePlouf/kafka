package be.pdty.kafka.processor;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import be.pdty.kafka.common.AccountUpdate;
import be.pdty.kafka.common.TransferRequest;
import be.pdty.kafka.common.TransferRequestError;
import be.pdty.kafka.processor.Processor.CreditPair;

@SpringBootApplication
public class ProcessorApp {
	@Autowired
	private KafkaTemplate<String,Object> kafkaTemplate;

	@Value("${target-topic}")
	private String targetTopic;

	@Value("${update-topic}")
	private String updateTopic;

	@Value("${error-topic}")
	private String errorTopic;
	
	@Autowired
	private Processor processor;
	
	@KafkaListener(topics = "${source-topic}")
	public void processMessage(ConsumerRecord<String,TransferRequest> record) {
		try(Producer<String,Object> producer = kafkaTemplate.getProducerFactory().createProducer()) {
			
			try {
				TransferRequest request=record.value();
				
				CreditPair pair = processor.executeRequest(request);
				
				BigDecimal issuerCredit=pair.a;
				BigDecimal beneficiaryCredit=pair.b;
				
				AccountUpdate issuerUpdate=new AccountUpdate(request.reference+"/I", request.issuer, request.beneficiary, request.reference, request.amount.negate(), issuerCredit);
				AccountUpdate beneficiaryUpdate=new AccountUpdate(request.reference+"/B", request.beneficiary, request.issuer, request.reference, request.amount, beneficiaryCredit);
				
				
				//Note: we should be able to use a transaction here
				kafkaTemplate.send(targetTopic,record.key(),request);
				kafkaTemplate.send(updateTopic,issuerUpdate.reference,issuerUpdate);
				kafkaTemplate.send(updateTopic,beneficiaryUpdate.reference,beneficiaryUpdate);
				
				
				
			} catch(Exception e) {
				kafkaTemplate.send(errorTopic,record.key(),new TransferRequestError(record.value(), e.getMessage()));
			}
			
		}

	}
	
	@PostConstruct
	public void test() {
		processor.test();
	}

	public static void main(String[] args) {
		SpringApplication.run(ProcessorApp.class,args);
	}
}
