/**
 * MySQLiteOpenHelper
 * Fichier décrivant la classe MySQLiteOpenHelper
 * permettant l'initialisation de la base de données SQLite
 *
 * @version 1.0
 * @author  Virginie CLAUDE
 */
package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classe d'initialisation de la base de données SQLite
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    /**
     * Construction de la table lignefraisforfait
     */
    private String lignefraisforfait ="CREATE TABLE lignefraisforfait("
            + "mois TEXT NOT NULL, "
            + "idfrais TEXT NOT NULL, "
            + "quantite INTEGER NOT NULL, "
            + "datemodif TEXT DEFAULT NULL,"
            + "idvisiteur TEXT NOT NULL"
            + ");";
    /**
     * Construction de la table lignefraishorsforfait
     */
    private String lignefraishorsforfait = "CREATE TABLE lignefraishorsforfait("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "mois TEXT NOT NULL, "
            + "jour INTEGER NOT NULL, "
            + "libelle TEXT NOT NULL, "
            + "montant REAL NOT NULL, "
            + "datemodif TEXT NOT NULL, "
            + "idmysql INTEGER DEFAULT NULL, "
            + "idvisiteur TEXT NOT NULL"
            + ");";

    /**
     * Construction de la table lignefraissupprimes
     */
    private String lignefraissupprimes = "CREATE TABLE lignefraissupprimes("
            + "id INTEGER PRIMARY KEY, "
            + "datemodif TEXT NOT NULL,"
            + "idvisiteur TEXT NOT NULL"
            + ");";

    /**
     * Construction de la table config
     * Cette table permet de stocker les paramètres de l'application
     * dont la date de dernière synchronisation des données
     */
    private String config = "CREATE TABLE config(parametre TEXT PRIMARY KEY, valeur TEXT DEFAULT NULL, idvisiteur TEXT DEFAULT NULL);";

    /**
     * Construction de la table utilisateur
     * Cette table stocke les infos de l'utilisateur de l'application
     * actuellement connecté
     */
    private String utilisateur = "CREATE TABLE utilisateur(id STRING PRIMARY KEY, login STRING, mdp STRING);";


    /**
     * Construction de l'accès à une base de données locale
     * @param context contexte dans lequel l'objet est instancié
     * @param name    nom de la base de données
     * @param version n° de version de la base
     */
    public MySQLiteOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    /**
     * Méthode redéfinie appelée automatiquement par le constructeur
     * uniquement si celui-ci repère que la base n'existe pas encore
     * @param db Objet SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String now = Global.getNow();
        db.execSQL(lignefraisforfait);
        db.execSQL(lignefraishorsforfait);
        db.execSQL(lignefraissupprimes);
        db.execSQL(config);
        db.execSQL(utilisateur);
        db.execSQL("INSERT INTO config (parametre, valeur) VALUES (\"synchronisation\", \"" + now + "\")");
    }

    /**
     * Méthode redéfinie appelée automatiquement s'il y a changement de version de la base
     * @param db         Objet SQLiteDatabase
     * @param oldVersion Ancien n° de version
     * @param newVersion Nouveau n° de version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}

