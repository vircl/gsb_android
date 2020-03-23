/**
 * Fichier Classe AccesLocal
 * Cette classe regroupe les fonctions permettant l'accès à la base locale SQLite
 * de l'application
 *
 * @version 1.0
 * @author  Virginie CLAUDE
 */

package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Cette classe permet l'accès à la base locale SQLite de l'application
 */
public class AccesLocal {

    private MySQLiteOpenHelper accesBD;
    private SQLiteDatabase bd;

    /**
     * Constructeur
     */
    public AccesLocal(Context context) {
        String nomBase = "bdAppliFrais.sqlite";
        int version = 1;
        this.accesBD = new MySQLiteOpenHelper(context, nomBase, version);
    }

    /**
     * Ajoute un élément hors forfait
     * @param mois    mois du frais hors forfait (string)
     * @param jour    jour du frais hors forfait (string)
     * @param libelle libelle du frais hors forfait (string)
     * @param montant montant du frais hors forfait (float)
     * @param idMySQL identifiant mysql de ce frais hors forfait
     */
    public void ajouterFraisHorsForfait(String mois, String jour, String libelle, Float montant, Integer idMySQL) {
        bd             = accesBD.getWritableDatabase();
        String requete = "INSERT INTO lignefraishorsforfait (mois, jour, libelle, montant, idmysql, datemodif, idvisiteur)"
                + " VALUES ( " +
                "\"" + mois + "\", " +
                "\"" + jour + " \", " +
                "\"" + libelle + "\", " +
                "\"" + montant + "\", " +
                "\"" + idMySQL + "\", " +
                "\"" + Global.getNow() + "\", " +
                "\"" + Global.idVisiteur + "\"" +
                ")";
        bd.execSQL(requete);
    }

    /**
     * Ajoute une fiche de frais dans la base de données
     *
     * @param fraisMois Fiche de frais du mois courant conentant les éléments forfaitisés
     */
    public void ajouterFraisMois(FraisMois fraisMois) {

        String mois = Global.getCleMois(fraisMois.getAnnee(), fraisMois.getMois());

        // Crée la fiche de frais si elle n'existe pas
        if (estPremierFraisMois(mois)) {
            creerFicheFrais(mois);
        }

        // Renseigne les éléments forfaitisés
        this.setFraisForfait(mois, "KM",  fraisMois.getKm(), Global.getNow());
        this.setFraisForfait(mois, "ETP", fraisMois.getEtape(), Global.getNow());
        this.setFraisForfait(mois, "NUI", fraisMois.getNuitee(), Global.getNow());
        this.setFraisForfait(mois, "REP", fraisMois.getRepas(), Global.getNow());

    }

    /**
     * Génère les lignes pour les éléments forfaitisés
     *
     * @param mois mois de la fiche de frais (string)
     */
    public void creerFicheFrais(String mois) {
        bd             = accesBD.getWritableDatabase();
        String requete = "INSERT INTO lignefraisforfait (mois, idfrais, quantite, idvisiteur) "
                + "VALUES " +
                "( \"" + mois + "\", \"KM\", 0, \"" + Global.idVisiteur + "\" )," +
                "( \"" + mois + "\", \"ETP\", 0, \"" + Global.idVisiteur + "\" )," +
                "( \"" + mois + "\", \"NUI\", 0, \"" + Global.idVisiteur + "\" )," +
                "( \"" + mois + "\", \"REP\", 0, \"" + Global.idVisiteur + "\" )";
        bd.execSQL(requete);
    }

    /**
     * Vérifie que la chaîne json reçue par l'API est correcte
     *
     * @param infos chaîne json contenant les informations de l'utilisateur
     *
     * @throws ApiException
     */
    public void controleJSONUser(JSONObject infos) throws ApiException {
        if (!infos.has("token")) {
            throw new ApiException("Connexion refusée, login ou mdp incorrect");
        }
        if (!infos.has("id")) {
            throw new ApiException("Identifiant manquant");
        }
        if (!infos.has("login")) {
            throw new ApiException("Login manquant");
        }
    }

    /**
     * Déconnecte l'utilisateur
     */
    public void deconnexion() {
        bd             = accesBD.getWritableDatabase();
        String requete = "DELETE FROM utilisateur"; // un seul utilisateur renseigné par appli

        bd.execSQL(requete);

        Global.token      = "";
        Global.idVisiteur = "";
    }

    /**
     * Teste si la fiche de frais passée en paramètres existe
     *
     * @param mois      Mois concerné par la fiche de frais (string)
     *
     * @return boolean  True si la fiche de frais n'existe pas
     */
    private boolean estPremierFraisMois(String mois) {
        Boolean retour;

        bd             = accesBD.getReadableDatabase();
        String requete = "SELECT * FROM lignefraisforfait WHERE mois = \""
                + mois + "\" AND idvisiteur = \"" + Global.idVisiteur + "\"";
        Cursor cursor  = bd.rawQuery(requete, null);
        retour         = cursor.getCount() == 0 ;

        cursor.close();

        return retour;
    }

    /**
     * Teste si le frais hors forfait existe dans la base sqlite
     *
     * @param id Identifiant du frais hors forfait
     *
     * @return boolean true si le frais existe
     */
    public boolean fraisExiste(Integer id) {
        bd             = accesBD.getReadableDatabase();
        String requete = "SELECT id FROM lignefraishorsforfait WHERE id = "
                + id + " AND idvisiteur = \"" + Global.idVisiteur + "\"";
        Cursor cursor  = bd.rawQuery(requete, null);
        boolean retour = cursor.getCount() > 0;

        cursor.close();

        return retour;
    }

    /**
     * Retourne la date de la dernière synchronisation
     *
     * @return String date de synchronisation
     */
    public String getDateSynchro() {
        bd             = accesBD.getReadableDatabase();
        String requete = "SELECT valeur FROM config WHERE parametre = \"synchronisation\"";
        Cursor cursor  = bd.rawQuery(requete, null);
        String date    = null;
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            date = cursor.getString(0);
        }
        cursor.close();
        return date;
    }

    /**
     * Retourne un hashtable contenant l'ensemble des éléments forfaitisés et hors forfait
     *
     * @return Hashtable contenant la liste des frais
     */
    private Hashtable<Integer, FraisMois> getHashtableFrais() {

        Hashtable<Integer , FraisMois> hashtable = new Hashtable<>();
        ArrayList<String> lesMois                = getLesMoisDisponibles();

        bd = accesBD.getReadableDatabase();
        String  requete;


        // Pour chaque mois enregistré
        for(String unMois: lesMois) {

            Cursor cursor;

            // Création d'un objet FraisMois
            Integer annee   = Integer.parseInt(unMois.substring(0,4));
            Integer mois    = Integer.parseInt(unMois.substring(4,6));
            Integer key     = annee*100+mois;

            FraisMois fraisMois = new FraisMois(annee, mois);

            Hashtable<String, Integer>  hashMois = new Hashtable<>();

            // Récupère les éléments forfaitisés
            requete = "SELECT * FROM lignefraisforfait WHERE mois = \"" + unMois + "\" AND idvisiteur = \"" + Global.idVisiteur + "\"";
            cursor  = bd.rawQuery(requete, null);

            cursor.moveToFirst();
            if(!cursor.isBeforeFirst()) {
                int i = cursor.getCount();
                while (i > 0) {
                    hashMois.put(cursor.getString(1), cursor.getInt(2));
                    cursor.moveToNext();
                    i--;
                }
            }

            cursor.close();

            fraisMois.setKm(hashMois.get("KM"));
            fraisMois.setEtape(hashMois.get("ETP"));
            fraisMois.setNuitee(hashMois.get("NUI"));
            fraisMois.setRepas(hashMois.get("REP"));

            // Récupère les éléments hors forfait
            requete = "SELECT * FROM lignefraishorsforfait WHERE mois = \"" + unMois + "\" AND idvisiteur = \"" + Global.idVisiteur + "\"";
            cursor  = bd.rawQuery(requete, null);
            cursor.moveToFirst();

            if(!cursor.isBeforeFirst()) {
                int i = cursor.getCount();
                while (i > 0) {
                    Float montant    = cursor.getFloat(4);
                    String motif     = cursor.getString(3);
                    Integer jour     = Integer.parseInt(cursor.getString(2));
                    Integer id       = cursor.getInt(0);
                    Integer idMySql  = cursor.getInt(6);
                    String dateModif = cursor.getString(5);
                    fraisMois.addFraisHf(montant, motif, jour, id, dateModif, idMySql);
                    cursor.moveToNext();
                    i--;
                }
            }

            cursor.close();

            // Enregistre le FraisMois dans le hashtable
            hashtable.put(key, fraisMois);
        }
        return hashtable;
    }


    /**
     * Retourne la liste des éléments à mettre à jour
     * sous la forme d'une chaîne json
     *
     * @return String
     *
     * @throws JSONException
     */
    public String getJsonSynchro(String session_id) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("session_id", session_id);
        jsonObject.put("dateSynchro", this.getDateSynchro());
        jsonObject.put("delete", this.getLesFraisSupprimes());
        jsonObject.put("fraisForfait", this.getLesFraisForfait());
        jsonObject.put("fraisHf", this.getLesFraisHorsForfait());

        return jsonObject.toString();
    }

    /**
     * Récupère l'id du dernier élément intégré dans la base de données
     *
     * @return int dernier identifiant enregistré dans la base SQLite
     */
    public int getLastId() {
        bd             = accesBD.getReadableDatabase();
        String requete = "SELECT last_insert_rowid()";
        Cursor cursor  = bd.rawQuery(requete, null);

        cursor.moveToLast();
        if( !cursor.isAfterLast()) {
            return cursor.getInt(0);
        } else {
            return -1;
        }
    }

    /**
     * Retourne le contenu de la table des éléments forfaitisés
     * sous la forme d'une chaîne json
     *
     * @return JSONArray liste des éléments forfaitisés
     */
    public JSONArray getLesFraisForfait() throws JSONException {
        bd                  = accesBD.getReadableDatabase();
        String requete      = "SELECT * FROM lignefraisforfait WHERE idvisiteur = \"" + Global.idVisiteur + "\"";
        Cursor    cursor    = bd.rawQuery(requete, null);
        JSONArray jsonArray = new JSONArray();

        cursor.moveToFirst();
        if( !cursor.isBeforeFirst()) {
            int i = cursor.getCount();
            while (i > 0) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("mois", cursor.getString(0));
                jsonObject.put("idFrais", cursor.getString(1));
                jsonObject.put("quantite", cursor.getString(2));
                jsonObject.put("datemodif", cursor.getString(3));
                jsonArray.put(jsonObject);
                cursor.moveToNext();
                i--;
            }
        }
        cursor.close();
        return jsonArray;
    }


    /**
     * Retourne le contenu de la table des éléments hors forfait
     * sous la forme d'une chaîne json
     *
     * @return JSONArray liste des éléments hors forfait
     */
    public JSONArray getLesFraisHorsForfait() throws JSONException {
        bd                  = accesBD.getReadableDatabase();
        String requete      = "SELECT * FROM lignefraishorsforfait WHERE idvisiteur = \"" + Global.idVisiteur + "\"";
        Cursor cursor       = bd.rawQuery(requete, null);
        JSONArray jsonArray = new JSONArray();

        cursor.moveToFirst();

        int i = cursor.getCount();

        while (i > 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cursor.getInt(0));
            jsonObject.put("mois", cursor.getString(1));
            jsonObject.put("jour", cursor.getInt(2));
            jsonObject.put("libelle", cursor.getString(3));
            jsonObject.put("montant", cursor.getFloat(4));
            jsonObject.put("datemodif", cursor.getString(5));
            jsonObject.put("idMySQL", cursor.getInt(6));
            jsonArray.put(jsonObject);
            cursor.moveToNext();
            i--;
        }

        cursor.close();
        return jsonArray;
    }

    /**
     * Retourne le contenu de la table des éléments supprimés
     * sous la forme d'un châine json
     *
     * @return JSONArray liste des éléments supprimés
     */
    public JSONArray getLesFraisSupprimes() throws JSONException {

        bd                  = accesBD.getReadableDatabase();
        String requete      = "SELECT * FROM lignefraissupprimes WHERE idvisiteur = \"" + Global.idVisiteur + "\"";
        Cursor cursor       = bd.rawQuery(requete, null);
        JSONArray jsonArray = new JSONArray();

        cursor.moveToFirst();

        int i = cursor.getCount();
        while (i > 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cursor.getInt(0));
            jsonObject.put("datemodif", cursor.getString(1));
            jsonArray.put(jsonObject);
            cursor.moveToNext();
            i--;
        }

        cursor.close();
        return jsonArray;
    }


    /**
     * Retourne la liste des frais disponibles
     * @return liste des mois disponibles
     */
    public ArrayList<String> getLesMoisDisponibles() {

        ArrayList<String> lesMois = new ArrayList<>();
        bd             = accesBD.getReadableDatabase();
        String requete = "SELECT DISTINCT mois FROM lignefraisforfait WHERE idvisiteur = \"" + Global.idVisiteur + "\"" ;
        Cursor cursor  = bd.rawQuery(requete, null);

        cursor.moveToFirst();
        int i = cursor.getCount();
        while (i > 0) {
            lesMois.add(cursor.getString(0));
            cursor.moveToNext();
            i--;
        }
        cursor.close();
        return lesMois;
    }

    /**
     * Met à jour les infos de l'utilisateur dans la base de données SQLite
     * @param infos les infos de l'utilisateur sous la forme d'un tableau json
     */
    public void majInfosUtilisateur(JSONObject infos, String mdp) throws JSONException {

        // Teste si la chaîne infos est au bon format
        try {
            controleJSONUser(infos);
        } catch (ApiException e) {
            e.printStackTrace();
        }

        bd        = accesBD.getWritableDatabase();
        String id = infos.getString("id");
        String requete;


        // Ajout des infos dans la base de données
        if (!utilisateurExiste(id)) {
            requete = "INSERT INTO utilisateur(id, login, mdp)" +
                    " VALUES (" +
                    "\"" + id + "\", " +
                    "\"" + infos.getString("login") + "\", " +
                    "\"" + mdp + "\"" +
                    ")";
            bd.execSQL(requete);
        }

        // Ajout des variables globales
        Global.token      = infos.getString("token");
        Global.idVisiteur = id;
    }





    /**
     * Met à jour un élément hors forfait
     * @param id      ID du frais HF à mettre à jour (integer)
     * @param mois    mois du frais HF (string)
     * @param jour    jour du frais HF (string)
     * @param libelle libelle du frais HF (string)
     * @param montant montant du frais HF (float)
     */
    public void majFraisHorsForfait(Integer id, String mois, String jour, String libelle, Float montant, Integer idMySQL, String dateModif) {
        bd             = accesBD.getWritableDatabase();
        String requete = "UPDATE lignefraishorsforfait SET " +
                "mois = \"" + mois + "\", " +
                "jour = \"" + jour + "\", " +
                "libelle = \"" + libelle + "\", " +
                "montant =  \"" + montant + "\", " +
                "datemodif = \"" + dateModif + "\", " +
                "idmysql = \"" + idMySQL + "\" " +
                "WHERE id =  \"" + id + "\" ";
        bd.execSQL(requete);
    }


    /**
     * Recupère les fiches de frais
     * et les enregistre dans la variable globale listFraisMois
     */
    public void recupFrais() {
        Global.listFraisMois = this.getHashtableFrais();
    }

    /**
     * Récupère l'utilisateur connecté
     *
     * @param context Activity
     * @param connexionInternet vrai si le terminal a accès à internet
     */
    public void recupUser(Context context, Boolean connexionInternet) {

        API    api     = new API(context);
        bd             = accesBD.getReadableDatabase();
        String requete = "SELECT * FROM utilisateur";
        Cursor cursor  = bd.rawQuery(requete, null);

        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            Global.idVisiteur = cursor.getString(0);
            if(connexionInternet) {
                // Connecte l'utilisateur à l'api
                api.connexion(cursor.getString(1), cursor.getString(2));
            }
        } else {
            Global.idVisiteur = "";
        }
        cursor.close();
    }

    /**
     * Enregistre la date de synchronisation
     *
     */
    public void setDateSynchro() {
        bd             = accesBD.getWritableDatabase();
        String requete = "UPDATE config SET valeur = \"" + Global.getNow() + "\" WHERE parametre = \"synchronisation\" AND idVisiteur = \"" + Global.idVisiteur + "\"";
        bd.execSQL(requete);
    }


    /**
     * Met à jour un élément forfaitisé
     *
     * @param mois     Mois concerné par ce frais
     * @param idFrais  Identifiant de l'élément forfaitisé (ETP|REP|NUI|KM)
     * @param quantite Nombre d'éléments à valoriser
     */
    public void setFraisForfait(String mois, String idFrais, Integer quantite, String datemodif) {
        if (estPremierFraisMois(mois)) {
            creerFicheFrais(mois);
        }
        bd = accesBD.getWritableDatabase();

        String requete = "UPDATE lignefraisforfait SET " +
                "quantite = \"" + quantite + "\", " +
                "datemodif = \"" + datemodif + "\"" +
                " WHERE mois = \"" + mois + "\" " +
                "AND idfrais = \"" + idFrais + "\" " +
                "AND idvisiteur = \"" + Global.idVisiteur + "\"";
        bd.execSQL(requete);
    }

    /**
     * Supprime un élément hors forfait
     *
     * @param idFrais ID du frais à supprimer
     */
    public void supprimerFraisHorsForfait(Integer idFrais) {

        bd = accesBD.getWritableDatabase();
        String requete;

        // Insertion du frais sur la table des frais supprimés
        requete = "INSERT INTO lignefraissupprimes (id, datemodif, idvisiteur) VALUES (" + idFrais + ", \"" + Global.getNow() + "\", " + Global.idVisiteur + ")";
        bd.execSQL(requete);

        // Retrait du frais de la liste des frais hors forfait
        requete = "DELETE FROM lignefraishorsforfait WHERE id=" + idFrais;
        bd.execSQL(requete);
    }


    /**
     * Met à jour la base locale avec les informations retournées par l'API
     *
     * @param json Chaine json à intégrer
     *
     * @return boolean True si l'exécution s'est déroulée sans erreur
     */
    public boolean updateBaseLocale(String json) throws JSONException {

        JSONObject jsonObject = new JSONObject(json);

        // Le traitement ne s'effectue que s'il n'y a pas d'erreur HTTP
        if (jsonObject.has("erreurHTTP")) {
            return false;
        }

        JSONArray fraisHf      = jsonObject.getJSONArray("fraisHf");
        JSONArray fraisForfait = jsonObject.getJSONArray("fraisForfait");

        // Mise à jour des éléments forfaitisés
        for (int i = 0; i < fraisForfait.length(); i++ ) {

            JSONObject item = fraisForfait.getJSONObject(i);
            String     mois = item.getString("mois");

            if (estPremierFraisMois(mois)) {
                creerFicheFrais(mois);
            }

            bd = accesBD.getWritableDatabase();

            String requete = "UPDATE lignefraisforfait "
                    + " SET quantite = " + item.getInt("quantite")  + ","
                    + "datemodif = \"" + Global.getNow() + "\""
                    + " WHERE idfrais = \"" + item.getString("idfrais")
                    + "\" AND mois = \"" + item.getString("mois") + "\""
                    + " AND idvisiteur = \"" + Global.idVisiteur + "\"";
            bd.execSQL(requete);
        }

        // Mise à jour des éléments hors forfait
        for (int i = 0; i < fraisHf.length(); i++) {
            JSONObject item   = fraisHf.getJSONObject(i);
            String  id        = item.getString("idandroid");
            Integer idAndroid = 0;
            String idSQL      = item.getString("id"); // id mysQL
            Integer idMySQL   = 0;

            if (!id.equals("null")) {
                idAndroid = Integer.parseInt(id);
            }
            if (!idSQL.equals("null")) {
                idMySQL = Integer.parseInt(idSQL);
            }
            if (fraisExiste(idAndroid)) {
                this.majFraisHorsForfait(
                    idAndroid,
                    item.getString("mois"),
                    item.getString("jour"),
                    item.getString("libelle"),
                    Float.parseFloat(item.getString("montant")),
                    idMySQL,
                    item.getString("datemodif")
                );
            } else {
                this.ajouterFraisHorsForfait(
                    item.getString("mois"),
                    item.getString("jour"),
                    item.getString("libelle"),
                    Float.parseFloat(item.getString("montant")),
                    idMySQL
                );
            }
        }

        // Elements supprimés
        bd             = accesBD.getWritableDatabase();
        String requete = "DELETE FROM lignefraissupprimes WHERE idvisiteur = \"" + Global.idVisiteur + "\"";
        bd.execSQL(requete);

        // Actualise les fiches de frais
        recupFrais();

        // Met à jour la date de synchronisation
        setDateSynchro();

        // Retourne true si aucune erreur en amont
        return true;

    }
    /**
     * Teste si l'id de l'utilisateur est référencé dans la base de données sqlite
     *
     * @param id Identifiant de l'utilisateur
     */
    public boolean utilisateurExiste(String id) {
        bd             = accesBD.getReadableDatabase();
        String requete = "SELECT * FROM utilisateur WHERE id = \"" + id + "\"";
        Cursor cursor  = bd.rawQuery(requete, null);
        Boolean retour = (cursor.getCount() > 0);
        cursor.close();
        return retour;
    }


}
