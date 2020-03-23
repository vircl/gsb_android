/**
 * Fichier classe Hors Forfait
 * Classe métier décrivant un frais hors forfait
 *
 * @author CNED
 * @author Virignie CLAUDE
 */
package fr.cned.emdsgil.suividevosfrais;

import java.io.Serializable;

/**
 * Classe métier contenant la description d'un frais hors forfait
 */
class FraisHf  implements Serializable {

	private final Float montant ;
	private final String motif ;
	private final Integer jour ;
	private final String datemodif;
	private Integer id;
	private Integer idMySQL;
	private Boolean actif;

	/**
	 * Constructeur
	 * @param montant   montant de l'élément hors forfait
	 * @param motif		libellé du frais hors forfait
	 * @param jour      date frais (jour)
	 * @param id		identifiant du frais hf
	 * @param datemodif date de modification du frais
	 * @param idMySQL   identifiant base distante
	 */
	public FraisHf(Float montant, String motif, Integer jour, Integer id, String datemodif, Integer idMySQL) {
		this.montant   = montant ;
		this.motif     = motif ;
		this.jour      = jour ;
		this.datemodif = datemodif;
		this.id        = id;
		this.idMySQL   = idMySQL;
		this.actif     = true;
	}

	/**
	 * Getter montant
	 * @return float
	 */
	public Float getMontant() {
		return montant;
	}

	/**
	 * Getter motif (libellé)
	 * @return string
	 */
	public String getMotif() {
		return motif;
	}

	/**
	 * Getter date (jour)
	 * @return string
	 */
	public Integer getJour() {
		return jour;
	}

	/**
	 * Getter date modification
	 * @return string
	 */
	public String getDatemodif() { return datemodif; }

	/**
	 * Getter id (identifiant du frais)
	 * @return integer
	 */
	public Integer getId() { return id; }

	/**
	 * Getter Actif
	 * @return true si le frais est actif (non supprimé)
	 */
	public Boolean getActif() { return this.actif; }

	/**
	 * Setter actif
	 * @param bool true si le frais est actif | false si le frais est à supprimer
	 */
	public void setActif(Boolean bool) {
		this.actif = bool;
	}

}
