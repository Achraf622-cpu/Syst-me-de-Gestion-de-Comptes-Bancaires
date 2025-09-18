package bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Versement extends Operation {
	private final String source;
	
	public Versement(BigDecimal montant, LocalDateTime date, String source) {
		super(montant, date);
		this.source = source == null ? "" : source;
	}
	
	public String getSource() {
		return source;
	}
}
