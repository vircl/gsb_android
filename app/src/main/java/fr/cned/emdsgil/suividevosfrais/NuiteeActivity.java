/**
 * Fichier de définition de la classe NuiteeActivity
 *
 * @author CNED
 * @author Virginie CLAUDE
 */
package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker.OnDateChangedListener;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


/**
 * Gestion des éléments forfaitisés "Nuitee"
 *
 */
public class NuiteeActivity extends AppCompatActivity {

    // informations affichées dans l'activité
    private Integer    annee ;
    private Integer    mois ;
    private Integer    qte ;
    private EditText   editText;
    private DatePicker datePicker;
    private ImageView  imgReturn;
    private Button     cmdBtnPlus;
    private Button     cmdBtnMoins;
    private Button     cmdBtnValider;
    private Integer    pas;
    private Context    context;
    private AccesLocal accesLocal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialisation
        setContentView(R.layout.activity_nuitee);
        setTitle("GSB : Frais Nuitées");

        this.editText      = (EditText)findViewById((R.id.txtNuitee));
        this.datePicker    = (DatePicker)findViewById(R.id.datNuitee);
        this.imgReturn     = (ImageView)findViewById(R.id.imgNuiteeReturn);
        this.cmdBtnPlus    = (Button)findViewById(R.id.cmdNuiteePlus);
        this.cmdBtnMoins   = (Button)findViewById(R.id.cmdNuiteeMoins);
        this.cmdBtnValider = (Button)findViewById(R.id.cmdNuiteeValider);
        this.pas           = 1;
        this.context       = NuiteeActivity.this;
        this.accesLocal    = new AccesLocal(context);


        // modification de l'affichage du DatePicker
        Global.changeAfficheDate(datePicker, false);

        // valorisation des propriétés
        valoriseProprietes() ;

        // chargement des méthodes événementielles
        imgReturn_clic() ;
        cmdValider_clic() ;
        cmdPlus_clic() ;
        cmdMoins_clic() ;
        dat_clic() ;

        // empêche la saisie directe sur l'EditText
        editText.setFocusable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.retour_accueil))) {
            retourActivityPrincipale() ;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Valorisation des propriétés avec les informations affichées
     */
    private void valoriseProprietes() {
        annee       = datePicker.getYear() ;
        mois        = datePicker.getMonth() + 1 ;
        qte         = 0 ;
        Integer key = annee * 100 + mois ;
        if (Global.listFraisMois.containsKey(key)) {
            qte = Global.listFraisMois.get(key).getNuitee() ;
        }
        editText.setText(String.format(Locale.FRANCE, "%d", qte)) ;
    }

    /**
     * Sur la selection de l'image : retour au menu principal
     */
    private void imgReturn_clic() {
        imgReturn.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                retourActivityPrincipale() ;
            }
        }) ;
    }

    /**
     * Sur le clic du bouton valider : sérialisation
     */
    private void cmdValider_clic() {
        cmdBtnValider.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            accesLocal.setFraisForfait(Global.getCleMois(annee, mois), "NUI", qte, Global.getNow());
            retourActivityPrincipale() ;
            }
        }) ;
    }

    /**
     * Sur le clic du bouton plus : ajout dans la quantité (le pas est défini dans onCreate)
     */
    private void cmdPlus_clic() {
        cmdBtnPlus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            qte+=pas ;
            enregNewQte() ;
            }
        }) ;
    }

    /**
     * Sur le clic du bouton moins : enlève dans la quantité si c'est possible (pas défini dans onCreate)
     */
    private void cmdMoins_clic() {
        cmdBtnMoins.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            qte = Math.max(0, qte-pas) ;
            enregNewQte() ;
            }
        }) ;
    }

    /**
     * Sur le changement de date : mise à jour de l'affichage de la qte
     */
    private void dat_clic() {
        final DatePicker uneDate = datePicker;
        uneDate.init(uneDate.getYear(), uneDate.getMonth(), uneDate.getDayOfMonth(), new OnDateChangedListener(){
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                valoriseProprietes() ;
            }
        });
    }

    /**
     * Enregistrement dans la zone de texte et dans la liste de la nouvelle qte, à la date choisie
     */
    private void enregNewQte() {

        // enregistrement dans la zone de texte
        editText.setText(String.format(Locale.FRANCE, "%d", qte)) ;

        // enregistrement dans la liste
        Integer key = annee*100+mois ;

        if (!Global.listFraisMois.containsKey(key)) {
            // creation du mois et de l'annee s'ils n'existent pas déjà
            Global.listFraisMois.put(key, new FraisMois(annee, mois)) ;
        }
        Global.listFraisMois.get(key).setNuitee(qte) ;
    }

    /**
     * Retour à l'activité principale (le menu)
     */
    private void retourActivityPrincipale() {
        Intent intent = new Intent(context, MainActivity.class) ;
        startActivity(intent) ;
    }
}
