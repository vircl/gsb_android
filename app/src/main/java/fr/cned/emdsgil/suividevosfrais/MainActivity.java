/**
 * Fichier Main Activity
 * Décrit la classe MainActivity, point d'entrée de l'application
 *
 * @author CNED
 * @author Virginie CLAUDE
 */
package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

/**
 * Classe MainActivity
 * Point d'entrée de l'application
 * Permet l'accès aux différents menus
 */
public class MainActivity extends AppCompatActivity {

    private AccesLocal accesLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("GSB : Suivi des frais");
        this.accesLocal = new AccesLocal(MainActivity.this);
    }

    /**
     * L'utilisateur navigue jusqu'à l'activity principale
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * L'utilisateur retourne sur l'activity principale
     */
    @Override
    protected void onResume() {
        super.onResume();
        initActivity();
    }

    /**
     * Initialisation de l'activity principale
     */
    private void initActivity()
    {
        accesLocal.recupUser(MainActivity.this, connexionInternet());

        if (!Global.idVisiteur.equals("")) {
            accesLocal.recupFrais();
        } else {
            if(connexionInternet()) {
                loadLoginActivity();
            } else {
                loadErreurActivity();
            }
        }

        // chargement des méthodes événementielles
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdKm)), KmActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdRepas)), RepasActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdEtape)), EtapeActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdNuitee)), NuiteeActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdHf)), HfActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdHfRecap)), HfRecapActivity.class);
        findViewById(R.id.txtSynchro).setVisibility(View.INVISIBLE);
        cmdTransfert_clic();
    }



    /**
     * Affichage de la vue login
     */
    private void loadLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    private void loadErreurActivity() {
        Intent intent = new Intent(MainActivity.this, ErreurActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Sur la sélection d'un bouton dans l'activité principale ouverture de l'activité correspondante
     * @return void
     */
    private void cmdMenu_clic(ImageButton button, final Class classe) {
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            // ouvre l'activité
            Intent intent = new Intent(MainActivity.this, classe);
            startActivity(intent);
            }
        });
    }

    /**
     * Synchronisation des informations avec le serveur
     * @return void
     */
    private void cmdTransfert_clic() {
        findViewById(R.id.cmdTransfert).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            API api          = new API(MainActivity.this);
            TextView synchro =  findViewById(R.id.txtSynchro);
            synchro.setVisibility(View.VISIBLE);
            synchro.setText("Synchronisation en cours");
            if(connexionInternet()) {
                if (Global.token.equals("")) {
                    loadLoginActivity();
                } else {
                    api.transfert();
                    synchro.setText("Synchronisation effectuée");
                }
            } else {
                synchro.setText("Appareil non connecté");
                loadErreurActivity();
            }
            }
        });
    }

    /**
     * Cette fonction teste si l'appareil a accès à internet
     *
     * @return boolean
     */
    private boolean connexionInternet() {
       NetworkInfo network = ((ConnectivityManager) Objects.requireNonNull(getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo();
       return network != null && network.isConnected();
    }

}
