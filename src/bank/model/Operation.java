package bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class Operation {
	private final String numero;
	private final LocalDateTime date;
	private final BigDecimal montant;

	protected Operation(BigDecimal montant, LocalDateTime date) {
		this.numero = UUID.randomUUID().toString();
		this.date = date == null ? LocalDateTime.now() : date;
		this.montant = Objects.requireNonNull(montant, "montant");
	}

	public String getNumero() {
		return numero;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public BigDecimal getMontant() {
		return montant;
	}

	public static Versement versement(BigDecimal montant, LocalDateTime date, String source) {
		return new Versement(montant, date, source);
	}

	public static Retrait retrait(BigDecimal montant, LocalDateTime date, String destination) {
		return new Retrait(montant, date, destination);
	}
} 