package be.pdty.kafka.processor;

import be.pdty.kafka.common.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class Processor {
	
	public static class CreditPair {
		public CreditPair(BigDecimal a,BigDecimal b) {
			this.a=a;
			this.b=b;
		}
		
		public final BigDecimal a;
		public final BigDecimal b;
	}

	@Autowired
	public Processor(VostroRepository vostros) {
		this.vostros = vostros;
	}

	private final VostroRepository vostros;
	
	@Transactional
	public CreditPair executeRequest(TransferRequest request)  throws Exception {
		Optional<Vostro> issuer=vostros.findByIdForTransaction(request.issuer.number);
		Optional<Vostro> beneficiary=vostros.findByIdForTransaction(request.beneficiary.number);
		
		if(!issuer.isPresent()) throw new Exception("Invalid issuer account");
		if(!beneficiary.isPresent()) throw new Exception("Invalid beneficiary account");
		
		Vostro issuerg=issuer.get();
		Vostro beneficiaryg=beneficiary.get();
		
		issuerg.credit=issuerg.credit.subtract(request.amount);
		beneficiaryg.credit=beneficiaryg.credit.add(request.amount);
		
		vostros.save(issuerg);
		vostros.save(beneficiaryg);
		
		
		return new CreditPair(issuer.get().credit,beneficiary.get().credit);
	}
	
	public void test() {
		System.err.println("---");
		for(Vostro v:vostros.findAll()) {
			System.err.println("Vostro "+v.account+":"+v.credit);
		}
		System.err.println("---");
	}
	
}
