/**
 * Fichier de définition de la classe API
 * Cette classe permet de communiquer entre l'applicaiton Android et le webservice REST
 *
 * @author Virginie CLAUDE
 */
package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.concurrent.ExecutionException;


/**
 * Classe API
 * Permet la communication entre l'application android et le webservice
*/
public class API {

    private AccesLocal accesLocal;
    //private String adresseAPI = "https://gsb.virginieclaude.fr/api/";
    private String adresseAPI = "http://192.168.1.51/projets/PPE/GSB/DEV/AppliWeb/api/";

    /**
     * Constructeur
     *
     * @param context Activity context
     */
    public API(Context context){
        this.accesLocal = new AccesLocal(context);
    }

    /**
     * Fonction login : connecte l'utilisateur à l'API REST
     *
     * @param login Identifiant de l'utilisateur
     * @param mdp   Mot de passe utilisateur
     */
    public void connexion(String login, String mdp) {

        HTTPAsync  httpAsync  = null;
        JSONObject jsonObject;

        // Connecte l'utilisateur
        try {
            jsonObject = new JSONObject("{\"login\":\"" + login + "\",\"mdp\":\"" + mdp + "\"}");
            httpAsync  = new HTTPAsync(this.adresseAPI + "login", "POST", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpAsync.execute();

        // Lecture de la réponse HTTP
        try {
            String reponse           = httpAsync.get();
            JSONObject reponseObject = new JSONObject(reponse);
            accesLocal.majInfosUtilisateur(reponseObject, mdp);
        } catch (ExecutionException e) {
            Log.d("API.connexion", "ExecutionException");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d("API.connexion", "InterruptedException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d("API.connexion", "JSONException");
            e.printStackTrace();
        }
    }


    /**
     * Synchronisation des données de l'appli avec celles du serveur
     */
    public void transfert() {
        HTTPAsync  httpAsync = null;
        String     json;
        JSONObject jsonObject;

        try {
            json       = accesLocal.getJsonSynchro(Global.token);
            jsonObject = new JSONObject(json);
            httpAsync  = new HTTPAsync(this.adresseAPI + Global.idVisiteur + "?token=" + Global.token, "POST", jsonObject);
        } catch (JSONException e) {
            Log.d("API.transfert", "HTTPAsync JSONException");
            e.printStackTrace();
        }

        assert httpAsync != null;
        httpAsync.execute();

        try {
            accesLocal.updateBaseLocale(httpAsync.get());
        } catch (JSONException e) {
            Log.d("API.transfert", "updateBaseLocale JSONException");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d("API.transfert", "updateBaseLocale ExecutionException");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d("API.transfert", "updateBaseLocale InterruptedException");
            e.printStackTrace();
        }
    }
}
