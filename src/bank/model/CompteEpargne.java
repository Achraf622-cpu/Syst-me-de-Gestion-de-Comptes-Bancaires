package bank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class CompteEpargne extends Compte {
	private final BigDecimal tauxInteret;

	public CompteEpargne(String code, BigDecimal soldeInitiale, BigDecimal tauxInteret) {
		super(code, soldeInitiale);
		this.tauxInteret = tauxInteret == null ? BigDecimal.ZERO : tauxInteret;
	}

	public BigDecimal getTauxInteret() {
		return tauxInteret;
	}

	@Override
	public void retirer(BigDecimal montant, String destination) {
		if (this.solde.compareTo(montant) < 0) {
			throw new IllegalArgumentException("Retrait refus 9: solde insuffisant");
		}
		this.solde = this.solde.subtract(montant);
		this.ajouterOperation(Operation.retrait(montant, LocalDateTime.now(), destination));
	}

	@Override
	public BigDecimal calculerInteret() {
		return this.solde.multiply(tauxInteret).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public void afficherDetails() {
		System.out.println("[Compte Epargne] " + code + " | Solde: " + solde + " | Taux: " + tauxInteret);
	}
} 