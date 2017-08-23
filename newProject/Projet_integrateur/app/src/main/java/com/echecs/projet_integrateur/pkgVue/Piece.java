package com.echecs.projet_integrateur.pkgVue;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

/**
 * Classe mère de toutes les pieces de la vue
 */
public class Piece extends ImageView {

    protected boolean blanc;
    protected boolean selectionne;
    protected boolean dernierCoup;
    protected int cote;

    /**
     * initialise la pièce
     *
     * @param context Permet d'afficher
     * @param cote    pour le padding
     */
    public Piece(Context context, int cote) {
        super(context);
        selectionne = false;
        dernierCoup = false;
        this.cote = cote;
        int pad = cote / 9;
        setPadding(pad, pad, pad, pad);
    }

    /**
     * Permet de sélectionner la pièce
     *
     * @return true si la piece est sélectionnée, false sinon.
     */
    public boolean isSelectionne() {
        return selectionne;
    }

    /**
     * Changer la couleur de la case si elle est sélectionnée
     *
     * @param selectionne true si elle est sélectionnée, false sinon
     */
    public void setSelectionne(boolean selectionne) {
        this.selectionne = selectionne;
        if (selectionne) {
            setBackgroundColor(Color.RED);
        } else {
            setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * Change la couleur de la case(dernier coup joué)
     *
     * @param dernierCoup true si l'utilisateur désire afficher le dernier coup, false sinon.
     */
    public void setDernierCoup(boolean dernierCoup) {
        this.dernierCoup = dernierCoup;
        if (dernierCoup) {
            setBackgroundColor(Color.GREEN);
        } else {
            setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * Vérifie la couleur de la pièce
     *
     * @return true si blanc, false sinon.
     */
    public boolean isBlanc() {
        return blanc;
    }

    /**
     * vérifie si l'utilisateur veut afficher le dernier coup
     *
     * @return true si oui, false sinon.
     */
    public boolean isDernierCoup() {
        return dernierCoup;
    }

    /**
     * Un entier qui sert pour le padding autour des pièces.
     *
     * @return un entier qui sert pour le padding
     */
    public int getCote() {
        return cote;
    }
}
