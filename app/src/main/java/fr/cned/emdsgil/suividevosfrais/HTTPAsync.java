/**
 * Fichier décrivant la classe HTTPAsync
 * Permetant l'exécution de requêtes de l'appli Android vers une API REST
 *
 * @author Virginie CLAUDE
 */
package fr.cned.emdsgil.suividevosfrais;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.stream.Collectors;

/**
 * Classe HTTPAsync
 * Permet l'exécution de requêtes de l'application Android vers une API REST
 * La requête est exécutée dans un thread séparé
 */
public class HTTPAsync extends AsyncTask<String, Void, String> {
    private String     methode;
    private URL        url;
    private JSONObject jsonPost;

    /**
     * Constructeur de la classe
     *
     * @param url
     */
    public HTTPAsync(String url) {
        this.init(url,"GET");
    }

    /**
     * Constructeur de la classe
     * Les données seront envoyées en GET
     * @param url
     * @param methode
     * @param jsonPost données à envoyer à l'api sous forme d'un objet json
     */
    public HTTPAsync(String url, String methode, JSONObject jsonPost){
        this.init(url, methode);
        this.jsonPost = jsonPost;
    }

    /**
     * Méthode d'initialisation appelée par le constructeur de la classe
     * Valorisation de la méthode d'envoi des données et de l'URL de l'API
     * @param methode String
     *                GET | POST
     */
    private void init(String url, String methode) {
        this.methode  = methode;
        this.jsonPost = new JSONObject();
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode exécutée avant l'exécution du thread
     * Affichage d'une barre de progression sur l'activity en cours
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * Exécution du thread
     * Appel de l'api via un objet HttpURLConnection
     * @param  strings
     * @return JSONObject les données retournées par l'API sont enregistrées dans une instance de JSONObject
     */
    @Override
    protected String doInBackground(String... strings) {

        HttpURLConnection urlConnection = null;
        String result;

        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod(this.methode);


            // Si on est en POST, envoi du fichier JSON dans la variable data
            if(this.methode.equals("POST")) {
                String data = URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(jsonPost.toString(), "UTF-8");
                urlConnection.setDoOutput(true); // Des infos sont envoyés en POST
                OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream());
                osw.write(data);
                osw.flush();
            }

            // Réponse HTTP
            int    code = urlConnection.getResponseCode();
            String msg  = code + " = " + urlConnection.getResponseMessage();

            // Check si une erreur est survenue
            if (code !=  200) {
                result = "{\"erreurHTTP\" : \"" + msg + "\"}";
            // Lecture de la réponse HTTP
            } else {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = readStream(in);
            }

        } catch (Exception e) {
            Log.d("HTTPAsync.doInBackground", "Erreur openUrlConnection");
            result = "{\"erreurHTTP\" : \"Connexion impossible\"}";
            e.printStackTrace();
        } finally {
            // Si la connexion a bien été établie, déconnexion après réponse HTTP
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * Méthode exécutée après l'exécution du thread
     * Permet d'afficher sur l'UI le résultat obtenu dans la méthode doInBackground
     * @param jsonString Le résultat de doInBackGround
     */
    @Override
    protected void onPostExecute(String jsonString) {
        super.onPostExecute(jsonString);
    }

    /**
     * Lecture d'un inputstream
     * Formate la réponse du serveur en une chaîne de caractères
     * @param  in InputStream
     * @return string
     */
    private String readStream(InputStream in) {
        String result = new BufferedReader(new InputStreamReader(in) )
                .lines().collect(Collectors.joining("\n"));
        return result;
    }
}
