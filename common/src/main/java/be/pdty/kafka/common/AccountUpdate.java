package be.pdty.kafka.common;

import java.math.BigDecimal;

public class AccountUpdate {
	public String reference;
	public Account account;
	public Account source;
	public String transferReference;
	public BigDecimal updateAmount;
	public BigDecimal newCredit;

	public AccountUpdate() {
	}
	
	public AccountUpdate(String reference,Account account,Account source,String transferReference,BigDecimal updateAmount,BigDecimal newCredit) {
		this.reference=reference;
		this.account=account;
		this.source=source;
		this.transferReference=transferReference;
		this.updateAmount=updateAmount;
		this.newCredit=newCredit;
	}
	
}
