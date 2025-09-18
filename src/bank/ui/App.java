package bank.ui;

import bank.model.Compte;
import bank.model.CompteCourant;
import bank.model.CompteEpargne;
import bank.model.Operation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class App {

    private final Map<String, Compte> storage = new HashMap<>();
    private int sequence = 0; 
    private static final Pattern CODE_PATTERN = Pattern.compile("^CPT-\\d{5}$");

    public App() {}

   
    private String readLine(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private BigDecimal readPositiveBigDecimal(Scanner sc, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String s = sc.nextLine().trim().replace(",", ".");
                BigDecimal v = new BigDecimal(s);
                if (v.signum() <= 0) {
                    System.out.println("Erreur: entrer un montant strictement positif.");
                    continue;
                }
                return v;
            } catch (Exception e) {
                System.out.println("Entree invalide. Ressayez.");
            }
        }
    }

    private String nextCode() {
        sequence++;
        return String.format("CPT-%05d", sequence);
    }

    private void requireValidCode(String code) {
        if (code == null || !CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("Code compte invalide (format attendu: CPT-12345)");
        }
    }

    private BigDecimal requirePositive(BigDecimal montant) {
        if (montant == null || montant.signum() <= 0) {
            throw new IllegalArgumentException("Le montant doit etre strictement positif");
        }
        return montant;
    }

    private Compte getOrThrow(String code) {
        requireValidCode(code);
        Compte c = storage.get(code);
        if (c == null) throw new IllegalArgumentException("Compte introuvable");
        return c;
    }

	public static void main(String[] args) {
		new App().run();
	}

	private void run() {
		try (Scanner sc = new Scanner(System.in)) {
			boolean running = true;
			while (running) {
				printMenu();
                String choice = readLine(sc, "Choix: ");
				switch (choice) {
					case "1":
						creerCompte(sc);
						break;
					case "2":
						versement(sc);
						break;
					case "3":
						retrait(sc);
						break;
					case "4":
						virement(sc);
						break;
					case "5":
						consulterSolde(sc);
						break;
					case "6":
						listerOperations(sc);
						break;
					case "0":
						running = false;
						break;
					default:
						System.out.println("Choix invalide.");
				}
				System.out.println();
			}
			System.out.println("Au revoir.");
		}
	}

	private void printMenu() {
		System.out.println("===== Menu Banque =====");
		System.out.println("1. Creer un compte");
		System.out.println("2. Effectuer un versement");
		System.out.println("3. Effectuer un retrait");
		System.out.println("4. Effectuer un virement");
		System.out.println("5. Consulter le solde");
		System.out.println("6. Lister les operations");
		System.out.println("0. Quitter");
	}

    private void creerCompte(Scanner sc) {
		System.out.println("-- Creation de compte --");
		String type;
		while (true) {
            type = readLine(sc, "Type (C=courant, E= epargne): ").toUpperCase();
			if ("C".equals(type) || "E".equals(type)) {
				break;
			}
			System.out.println("Erreur: type invalide. Entrez C ou E.");
		}
        try {
            if ("C".equals(type)) {
                String code = nextCode();
                while (storage.containsKey(code)) code = nextCode();
                System.out.println("Votre code est : " + code);
                BigDecimal solde = readPositiveBigDecimal(sc, "Solde initial: ");
                BigDecimal decouvert = readPositiveBigDecimal(sc, "Decouvert autorise: ");
                Compte c = new CompteCourant(code, solde, decouvert);
                storage.put(code, c);
                c.afficherDetails();
            } else {
                String code = nextCode();
                while (storage.containsKey(code)) code = nextCode();
                System.out.println("Votre code est : " + code);
                BigDecimal solde = readPositiveBigDecimal(sc, "Solde initial: ");
                BigDecimal taux = readPositiveBigDecimal(sc, "Taux d'interet (ex 0.03): ");
                Compte c = new CompteEpargne(code, solde, taux);
                storage.put(code, c);
                c.afficherDetails();
            }
        } catch (Exception e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}

	private void versement(Scanner sc) {
		System.out.println("-- Versement --");
        String code;
        while (true) {
            code = readLine(sc, "Code compte: ");
            try {
                getOrThrow(code);
                break;
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
        BigDecimal montant = readPositiveBigDecimal(sc, "Montant: ");
        String source = readLine(sc, "Source: ");
		try {
            requirePositive(montant);
            Compte c = getOrThrow(code);
            c.crediter(montant, source);
			System.out.println("Versement réussi.");
		} catch (Exception e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}

	private void retrait(Scanner sc) {
		System.out.println("-- Retrait --");
        String code;
        while (true) {
            code = readLine(sc, "Code compte: ");
            try {
                getOrThrow(code);
                break;
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
        BigDecimal montant = readPositiveBigDecimal(sc, "Montant: ");
        String dest = readLine(sc, "Destination: ");
		try {
            requirePositive(montant);
            Compte c = getOrThrow(code);
            c.retirer(montant, dest);
			System.out.println("Retrait r 9ussi.");
		} catch (Exception e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}

	private void virement(Scanner sc) {
		System.out.println("-- Virement --");
        String c1;
        String c2;
        while (true) {
            c1 = readLine(sc, "Code source: ");
            c2 = readLine(sc, "Code destination: ");
            if (c1.equals(c2)) {
                System.out.println("Erreur: les comptes doivent \tatre diff\terents.");
                continue;
            }
            try {
                getOrThrow(c1);
                getOrThrow(c2);
                break;
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
        BigDecimal montant = readPositiveBigDecimal(sc, "Montant: ");
        String motif = readLine(sc, "Motif: ");
		try {
            requirePositive(montant);
            Compte source = getOrThrow(c1);
            Compte dest = getOrThrow(c2);
            source.retirer(montant, motif == null ? "Virement sortant" : motif);
            dest.crediter(montant, motif == null ? "Virement entrant" : motif);
			System.out.println("Virement réussi.");
		} catch (Exception e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}

	private void consulterSolde(Scanner sc) {
		System.out.println("-- Solde --");
        String code = readLine(sc, "Code compte: ");
        try {
            BigDecimal s = getOrThrow(code).getSolde();
            System.out.println("Solde: " + s);
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
	}

	private void listerOperations(Scanner sc) {
		System.out.println("-- Operations --");
        String code;
        while (true) {
            code = readLine(sc, "Code compte: ");
            try {
                getOrThrow(code);
                break;
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
        try {
            List<Operation> ops = new ArrayList<>(getOrThrow(code).getListeOperations());
			if (ops.isEmpty()) {
				System.out.println("Aucune opération.");
				return;
			}
			for (Operation op : ops) {
				System.out.println(op.getDate() + " | " + op.getClass().getSimpleName() + " | Montant: " + op.getMontant());
			}
		} catch (Exception e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}
} 