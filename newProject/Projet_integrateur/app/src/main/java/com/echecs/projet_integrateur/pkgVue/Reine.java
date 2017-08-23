package com.echecs.projet_integrateur.pkgVue;

import android.content.Context;

import com.echecs.projet_integrateur.R;

/**
 * Classe qui permet de créer et d'afficher la reine.
 * Created by Julien on 2015-02-17.
 */
public class Reine extends Piece {
    /**
     * Initialise la reine
     *
     * @param context permet d'afficher
     * @param couleur true si blanc, false si noir.
     * @param cote    pour le padding
     */
    public Reine(Context context, boolean couleur, int cote) {
        super(context, cote);
        this.blanc = couleur;
        chercherImage();
    }

    /**
     * permet de générer l'image de la pièce à partir des ressoures
     */
    private void chercherImage() {
        if (blanc) {
            setImageResource(R.drawable.blanc_reine);
        } else {
            setImageResource(R.drawable.noir_reine);
        }
    }
}
