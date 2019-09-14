package be.pdty.kafka.common;

import java.math.BigDecimal;

public class TransferRequest {
	public String reference;
	public Account issuer;
	public Account beneficiary;
	public BigDecimal amount;
	
	public TransferRequest() {
	}
	
	public TransferRequest(String reference,Account issuer,Account beneficiary,BigDecimal amount) {
		this.reference=reference;
		this.issuer=issuer;
		this.beneficiary=beneficiary;
		this.amount=amount;
	}

}
