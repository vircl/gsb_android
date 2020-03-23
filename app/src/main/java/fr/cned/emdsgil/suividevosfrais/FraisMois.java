/**
 * Fichier décrivant la classe FraisMois
 * Cette classe contient les éléments forfaitisés
 * et les frais hors forfait
 *
 * @author CNED
 * @author Virginie CLAUDE
 */
package fr.cned.emdsgil.suividevosfrais;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe métier contenant les informations des frais d'un mois
 */
class FraisMois implements Serializable {

    private Integer mois;        // mois concerné
    private Integer annee;       // année concernée
    private Integer etape;       // nombre d'étapes du mois
    private Integer km;          // nombre de km du mois
    private Integer nuitee;      // nombre de nuitées du mois
    private Integer repas;       // nombre de repas du mois
    private String  etapeModif;  // date et heure modification nb etape
    private String  kmModif;     // date et heure modification nb km
    private String  nuiteeModif; // date et heure modification nb nuitées
    private String  repasModif;  // date et heure modification nb repas

    private final ArrayList<FraisHf> lesFraisHf; // liste des frais hors forfait du mois

    public FraisMois(Integer annee, Integer mois) {
        this.annee       = annee;
        this.mois        = mois;
        this.etape       = 0;
        this.km          = 0;
        this.nuitee      = 0;
        this.repas       = 0;
        this.etapeModif  = "";
        this.repasModif  = "";
        this.nuiteeModif = "";
        this.kmModif     = "";
        lesFraisHf       = new ArrayList<>();
    }

    /**
     * Ajout d'un frais hors forfait
     *
     * @param montant Montant en euros du frais hors forfait
     * @param motif   Justification du frais hors forfait
     */
    public void addFraisHf(Float montant, String motif, Integer jour, Integer id, String dateModif) {
        lesFraisHf.add(new FraisHf(montant, motif, jour, id, dateModif, null));
    }

    /**
     * Ajout d'un frais hors forfait
     * @param montant   Montant en euros du frais hors forfait
     * @param motif     Justification du frais hors forfait
     * @param jour      Jour correspondant au frais hors forfait
     * @param id        Identifiant bdd serveur distant
     * @param datemodif Date de modification
     */
    public void addFraisHf(Float montant, String motif, Integer jour, Integer id, String datemodif, Integer idMySql) {
        lesFraisHf.add(new FraisHf(montant, motif, jour, id, datemodif, idMySql));
    }

    /**
     * Suppression d'un frais hors forfait
     *
     * @param index Indice du frais hors forfait à supprimer
     */
    public void supprFraisHf(Integer index) {
        FraisHf leFraisHf = lesFraisHf.get(index);
        leFraisHf.setActif(false);
    }

    /**
     * Retourne le mois concerné par la fiche de frais
     * @return Integer
     */
    public Integer getMois() {
        return mois;
    }

    /**
     * Défini le mois concerné par la fiche de frais
     * @param mois INteger
     */
    public void setMois(Integer mois) {
        this.mois = mois;
    }

    /**
     * Retourne l'année concernée par la fiche de frais
     * @return Integer
     */
    public Integer getAnnee() {
        return annee;
    }

    /**
     * Défini l'année de la fiche de frais
     * @param annee Integer
     */
    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    /**
     * Retourne le nombre d'étapes
     * @return Integer
     */
    public Integer getEtape() {
        return etape;
    }

    /**
     * Défini le nombre d'étapes
     * @param etape Integer
     */
    public void setEtape(Integer etape) {
        this.etape      = etape;
        this.etapeModif = Global.getNow();
    }

    /**
     * Retourne la date et l'heure de modification du nombre d'étapes
     * @return String
     */
    public String getEtapeModif() {
        return etapeModif;
    }

    /**
     * Défini la date de modification du nombre d'étapes
     * @param datemodif
     */
    public void setEtapeModif(String datemodif) {
        this.etapeModif = datemodif;
    }

    /**
     * Retourne un objet JSON concernant les infos du forfait étape
     * @return JSONObject
     */
    public JSONObject getEtapeJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("quantite", this.getEtape());
        jsonObject.put("datemodif", this.getEtapeModif());
        return jsonObject;
    }

    /**
     * Retourne le nombre de km
     * @return Integer
     */
    public Integer getKm() {
        return km;
    }

    /**
     * Défini le nombre de km
     * @param km Integer
     */
    public void setKm(Integer km) {
        this.km      = km;
        this.kmModif = Global.getNow();
    }

    /**
     * Retourne la date et heure de modification du nombre de km
     * @return String
     */
    public String getKmModif() {
        return kmModif;
    }

    /**
     * Défini la dae de modification du nombre de km
     * @param datemodif
     */
    public void setKmModif(String datemodif) {
        this.kmModif = datemodif;
    }

    /**
     * Retourne un objet JSON concernant les infos du forfait km
     * @return JSONObject
     * @throws JSONException
     */
    public JSONObject getKmJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("quantite", this.getKm());
        jsonObject.put("datemodif", this.getKmModif());
        return jsonObject;
    }

    /**
     * Retourne le nombre de nuitées
     * @return Integer
     */
    public Integer getNuitee() {
        return nuitee;
    }

    /**
     * Défini le nombre de nuitées
     * @param nuitee Integer
     */
    public void setNuitee(Integer nuitee) {
        this.nuitee      = nuitee;
        this.nuiteeModif = Global.getNow();
    }

    /**
     * Retourne la date et heure de modification du nombre de nuitées
     * @return String
     */
    public String getNuiteeModif() {
        return nuiteeModif;
    }

    /**
     * Défini la date de modification du nombre de nuitées
     * @param datemodif
     */
    public void setNuiteeModif(String datemodif) {
        this.nuiteeModif = datemodif;
    }

    /**
     * Retourne un objet JSON concernant les infos du forfait nuit
     * @return JSONObject
     */
    public JSONObject getNuiteeJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("quantite", this.getNuitee());
        jsonObject.put("datemodif", this.getNuiteeModif());
        return jsonObject;
    }

    /**
     * Retourne le nombre de repas
     * @return Integer
     */
    public Integer getRepas() {
        return repas;
    }

    /**
     * Défini le nombre de repas
     * @param repas
     */
    public void setRepas(Integer repas) {
        this.repas      = repas;
        this.repasModif = Global.getNow();
    }

    /**
     * Retourne la date et heure de modification du nombre de repas
     * @return String
     */
    public String getRepasModif() {
        return repasModif;
    }

    /**
     * Défini la date de modification du nombre de repas
     * @param datemodif
     */
    public void setRepasModif(String datemodif) {
        this.repasModif = datemodif;
    }

    /**
     * Retourne un objet JSON concernant les infos du forfait repas
     * @return JSONObject
     * @throws JSONException
     */
    public JSONObject getRepasJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("quantite", this.getRepas());
        jsonObject.put("datemodif", this.getRepasModif());
        return jsonObject;
    }

    /**
     * Retourne un tableau d'instances de la classe FraisHf
     * @return ArrayList<FraisHf> Tableau d'instances d'éléments hors forfait
     */
    public ArrayList<FraisHf> getLesFraisHf() {
        return lesFraisHf;
    }

}
