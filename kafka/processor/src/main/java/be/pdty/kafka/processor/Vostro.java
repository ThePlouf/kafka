package be.pdty.kafka.processor;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Vostro {
	@Id
	public String account;
	
	public BigDecimal credit;
}
