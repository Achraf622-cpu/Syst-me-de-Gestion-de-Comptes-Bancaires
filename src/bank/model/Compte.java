package bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Compte {
	protected final String code;
	protected BigDecimal solde;
	protected final List<Operation> listeOperations;

	protected Compte(String code, BigDecimal soldeInitiale) {
		this.code = Objects.requireNonNull(code, "code");
		this.solde = soldeInitiale == null ? BigDecimal.ZERO : soldeInitiale;
		this.listeOperations = new ArrayList<>();
	}

	public String getCode() {
		return code;
	}

	public BigDecimal getSolde() {
		return solde;
	}

	public List<Operation> getListeOperations() {
		return Collections.unmodifiableList(listeOperations);
	}

	public void ajouterOperation(Operation operation) {
		this.listeOperations.add(operation);
	}

	public void crediter(BigDecimal montant, String source) {
		this.solde = this.solde.add(montant);
		this.ajouterOperation(Operation.versement(montant, LocalDateTime.now(), source));
	}

	public abstract void retirer(BigDecimal montant, String destination);

	public abstract BigDecimal calculerInteret();

	public abstract void afficherDetails();
} 
