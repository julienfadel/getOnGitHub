package com.echecs.projet_integrateur.pkgVue;

import android.content.Context;

import com.echecs.projet_integrateur.R;

/**
 * Classe qui permet de créer et de d'afficher la tour.
 * Created by Simon on 2015-02-17.
 */
public class Tour extends Piece {
    /**
     * Initialise la tour
     *
     * @param context permet d'afficher
     * @param couleur true si blanc, false si noir
     * @param cote    pour le padding
     */
    public Tour(Context context, boolean couleur, int cote) {
        super(context, cote);
        this.blanc = couleur;
        chercherImage();
        int pad = (cote * 12) / 90;
        setPadding(pad, pad, pad, pad);
    }

    /**
     * permet de générer l'image de la pièce à partir des ressoures
     */
    private void chercherImage() {
        if (blanc) {
            setImageResource(R.drawable.blanc_tour);
        } else {
            setImageResource(R.drawable.noir_tour);
        }
    }
}
