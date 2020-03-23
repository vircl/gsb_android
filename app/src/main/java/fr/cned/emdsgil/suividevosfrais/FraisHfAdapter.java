/**
 * Adapter permettant de lister les frais hors forfait
 * @author CNED
 * @author Virginie CLAUDE
 */
package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Adapter permettant de lister les frais hors forfait
 */
class FraisHfAdapter extends BaseAdapter {

	private final ArrayList<FraisHf> lesFrais ; // liste des frais du mois
	private final LayoutInflater inflater ;
	private AccesLocal accesLocal;

	/**
	 * Constructeur de l'adapter pour valoriser les propriétés
	 * @param context  Accès au contexte de l'application
	 * @param lesFrais Liste des frais hors forfait
	 */
	public FraisHfAdapter(Context context, ArrayList<FraisHf> lesFrais) {
		inflater      = LayoutInflater.from(context) ;
		this.lesFrais = lesFrais ;
		this.accesLocal = new AccesLocal(context);
	}

	/**
	 * retourne le nombre d'éléments de la listview
	 *
	 * @return int
	 */
	@Override
	public int getCount() {
		return lesFrais.size() ;
	}

	/**
	 * retourne l'item de la listview à un index précis
	 *
	 * @return Object
	 */
	@Override
	public Object getItem(int index) {
		return lesFrais.get(index) ;
	}

	/**
	 * retourne l'index de l'élément actuel
	 *
	 * return long
	 */
	@Override
	public long getItemId(int index) {
		return index;
	}

	/**
	 * Classe ViewHolder
	 * structure contenant les éléments d'une ligne
	 */
	private class ViewHolder {
		TextView    txtListJour ;
		TextView    txtListMontant ;
		TextView    txtListMotif ;
		ImageButton cmdSuppHf;
	}

	/**
	 * Affichage dans la liste
	 */
	@Override
	public View getView(int index, View convertView, ViewGroup parent) {

		// Instance de la classe ViewHolder définie ci-dessus
		ViewHolder holder ;

		// Si la ligne active reçue n'existe pas encore
		if (convertView == null) {

			holder = new ViewHolder() ;

			// Construction de la ligne à partir de layout_liste
			convertView = inflater.inflate(R.layout.layout_liste, parent, false) ;

			// Chaque propriété de holder est liée aux objets graphiques
			holder.txtListJour    = convertView.findViewById(R.id.txtListJour);
			holder.txtListMontant = convertView.findViewById(R.id.txtListMontant);
			holder.txtListMotif   = convertView.findViewById(R.id.txtListMotif);
			holder.cmdSuppHf      = convertView.findViewById(R.id.cmdSuppHf);

			// Affecte le holder comme tag de la ligne
			convertView.setTag(holder) ;

		}else{

			// La ligne existe déjà, holder récupère le hoder de la ligne
			holder = (ViewHolder)convertView.getTag();

		}

		// Valorisation des propriétés de holder avec les frais
		holder.txtListJour.setText(String.format(Locale.FRANCE, "%d", lesFrais.get(index).getJour()));
		holder.txtListMontant.setText(String.format(Locale.FRANCE, "%.2f", lesFrais.get(index).getMontant())) ;
		holder.txtListMotif.setText(lesFrais.get(index).getMotif()) ;
		holder.cmdSuppHf.setTag(index);

		// Gestion de l'événement clic sur le bouton suppression
		holder.cmdSuppHf.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			int position = (int) v.getTag();
			int id = lesFrais.get(position).getId();
			lesFrais.remove(position);

			// Enregistrement
			accesLocal.supprimerFraisHorsForfait(id);

			// Rafraîchi l'écran
			notifyDataSetChanged();
			}
		});

		// Retourne la vue construite
		if(lesFrais.get(index).getActif().equals(true)) {
			return convertView;
		} else {
			convertView = null;
			return convertView;
		}
	}
}