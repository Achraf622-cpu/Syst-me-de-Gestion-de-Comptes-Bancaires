package bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CompteCourant extends Compte {
	private final BigDecimal decouvert; // overdraft limit (positive value)

	public CompteCourant(String code, BigDecimal soldeInitiale, BigDecimal decouvert) {
		super(code, soldeInitiale);
		this.decouvert = decouvert == null ? BigDecimal.ZERO : decouvert;
	}

	public BigDecimal getDecouvert() {
		return decouvert;
	}

	@Override
	public void retirer(BigDecimal montant, String destination) {
		BigDecimal nouveauSolde = this.solde.subtract(montant);
		BigDecimal limite = this.decouvert.negate();
		if (nouveauSolde.compareTo(limite) < 0) {
			throw new IllegalArgumentException("Retrait refusé: dépassement du découvert autorisé");
		}
		this.solde = nouveauSolde;
		this.ajouterOperation(Operation.retrait(montant, LocalDateTime.now(), destination));
	}

	@Override
	public BigDecimal calculerInteret() {
		return BigDecimal.ZERO; // no interest for checking account
	}

	@Override
	public void afficherDetails() {
		System.out.println("[Compte Courant] " + code + " | Solde: " + solde + " | Découvert: " + decouvert);
	}
} 