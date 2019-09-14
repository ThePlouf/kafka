package be.pdty.kafka.common;

public class TransferRequestError {
	public TransferRequest request;
	public String message;
	
	public TransferRequestError() {
	}
	
	public TransferRequestError(TransferRequest request,String message) {
		this.request=request;
		this.message=message;
	}
	
}
