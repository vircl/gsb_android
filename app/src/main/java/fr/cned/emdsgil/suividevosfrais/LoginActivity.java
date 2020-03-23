/**
 * Fichier décrivant la classe LoginActivity
 * Cette classer permet l'authentification du visiteur
 *
 * @author Virignie CLAUDE
 */

package fr.cned.emdsgil.suividevosfrais;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Classe LoginActivity
 * Permet l'authentification du visiteur
 *
 * @author Virginie CLAUDE
 */
public class LoginActivity extends AppCompatActivity {
    private Button   cmdLogin;
    private TextView txtErreur;
    private TextView txtLogin;
    private TextView txtMdp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("GSB : Connexion");
        this.cmdLogin = (Button)findViewById(R.id.cmdLogin);
        this.txtErreur = findViewById(R.id.lblErreur);
        this.txtLogin  = findViewById(R.id.txtLogin);
        this.txtMdp    = findViewById(R.id.txtMdp);
        cmdLogin_clic();
    }

    /**
     * Ajout d'un écouteur d'évènement sur le bouton
     */
    private void cmdLogin_clic() {
        cmdLogin.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                connecter();
            }
        }) ;
    }


    /**
     * Connecte l'utilisateur à l'API
     */
    private void connecter() {

        String login = String.valueOf(txtLogin.getText());
        String mdp   = String.valueOf(txtMdp.getText());
        API api      = new API(LoginActivity.this);

        api.connexion(login, mdp);

        // si le token est vide l'authentification a échoué
        if(Global.token.equals("")) {
            txtErreur.setText("Login ou mdp incorrect");
        } else {
            // Retour à la précédente activity
            LoginActivity.this.finish();
        }
    }
}
