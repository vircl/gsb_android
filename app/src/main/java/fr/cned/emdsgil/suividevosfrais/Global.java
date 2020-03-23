/**
 * Fichier décrivant la classe global
 * Cette classe contient les méthodes et variables globales à l'application
 *
 * @author CNED
 * @author Virginie CLAUDE
 */
package fr.cned.emdsgil.suividevosfrais;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

/**
 * Cette classe contient les méthodes et variables globales à l'application
 */
abstract class Global {


    // tableau d'informations mémorisées
    public static Hashtable<Integer, FraisMois> listFraisMois = new Hashtable<>();

    // fichier contenant les informations sérialisées
    public static final String filename     = "save.fic"; // TODO destroy

    // Récupère les infos de l'utilisateur (id et token)
    public static String idVisiteur         = "";
    public static String token = "";


    /**
     * Modification de l'affichage de la date (juste le mois et l'année, sans le jour)
     */
    public static void changeAfficheDate(DatePicker datePicker, boolean afficheJours) {
        try {
            Field f[] = datePicker.getClass().getDeclaredFields();
            for (Field field : f) {
                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), null);
                if (daySpinnerId != 0)
                {
                    View daySpinner = datePicker.findViewById(daySpinnerId);
                    if (!afficheJours)
                    {
                        daySpinner.setVisibility(View.GONE);
                    }
                }
            }
        } catch (SecurityException | IllegalArgumentException e) {
            Log.d("ERROR", e.getMessage());
        }
    }


    /**
     * Retourne la date et l'heure au format dd/MM/yyyy hh:mm:ss
     * @return String
     */
    public static String getNow() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Construction de la clé mois sous la forme aaaamm
     * @return aaaamm
     */
    public static String getCleMois(Integer annee, Integer mois) {
        String leMois = mois.toString();
        if (mois<10) {
            leMois = "0" + leMois;
        }
        return annee.toString() + leMois;
    }
}
