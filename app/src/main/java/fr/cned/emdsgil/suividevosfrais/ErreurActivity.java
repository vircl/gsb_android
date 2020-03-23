/**
 * Fichier de définition de la classe ErreurActivity
 * Permettant l'affichage d'un message d'erreur
 *
 * @author Virginie CLAUDE
 */
package fr.cned.emdsgil.suividevosfrais;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Classe ErreurActivity
 * Affichage d'un message d'erreur
 */
public class ErreurActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erreur);
    }
}
