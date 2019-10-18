package be.pdty.kafka.processor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Vostro {
	@Id
	public String account;
	
	public BigDecimal credit;
}
