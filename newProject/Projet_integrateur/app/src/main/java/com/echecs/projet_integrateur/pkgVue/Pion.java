package com.echecs.projet_integrateur.pkgVue;

import android.content.Context;

import com.echecs.projet_integrateur.R;

/**
 * Classe qui permet de créer et de d'afficher le pion.
 * Created by Simon on 2015-02-17.
 */
public class Pion extends Piece {
    /**
     * Initialise le pion
     *
     * @param context permet d'afficher
     * @param couleur true si blanc, false si noir.
     * @param cote    pour le padding
     */
    public Pion(Context context, boolean couleur, int cote) {
        super(context, cote);
        this.blanc = couleur;
        chercherImage();
    }

    /**
     * permet de générer l'image de la pièce à partir des ressoures
     */
    private void chercherImage() {
        if (blanc) {
            setImageResource(R.drawable.blanc_pion);
        } else {
            setImageResource(R.drawable.noir_pion);
        }
    }
}
