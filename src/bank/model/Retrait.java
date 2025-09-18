package bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Retrait extends Operation {
	private final String destination;
	
	public Retrait(BigDecimal montant, LocalDateTime date, String destination) {
		super(montant, date);
		this.destination = destination == null ? "" : destination;
	}
	
	public String getDestination() {
		return destination;
	}
}
