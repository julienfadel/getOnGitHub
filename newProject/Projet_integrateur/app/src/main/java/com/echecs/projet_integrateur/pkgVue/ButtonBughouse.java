package com.echecs.projet_integrateur.pkgVue;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Boutons nécessaires au bughouse chess
 * Created by Simon on 2015-04-28.
 */
public class ButtonBughouse extends LinearLayout {

    TextView textViewNombre;
    int nombre;
    Piece piece;
    boolean selected;

    /**
     * initialise le ButtonBughouse
     *
     * @param context permet d'afficher les éléments graphiques
     * @param piece   la pièce à mettre dans le bouton
     * @param nombre  le nombre de pièces en stock
     */

    public ButtonBughouse(Context context, Piece piece, int nombre) {
        super(context);
        this.piece = piece;
        this.nombre = nombre;
        setClickable(true);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        int taille = piece.getCote() / 2;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(taille, taille);
        piece.setLayoutParams(layoutParams);
        piece.setPadding(0, 0, 0, 0);
        setBackgroundResource(android.R.drawable.btn_default);
        addView(piece);
        textViewNombre = new TextView(context);
        textViewNombre.setText(nombre + "");
        textViewNombre.setTextColor(Color.BLACK);
        textViewNombre.setGravity(Gravity.CENTER);
        addView(textViewNombre);
    }

    /**
     * change le nombre de pièces et le TextView
     *
     * @param nombre le nombre de pièces en stock
     */
    public void setNombre(int nombre) {
        this.nombre = nombre;
        textViewNombre.setText(nombre + "");
        if (nombre == 0) {
            textViewNombre.setEnabled(false);
        } else {
            textViewNombre.setEnabled(true);
        }
    }

    /**
     * Vérifie si le bouton est sélectionné
     *
     * @param selected vrai s'il est sélectionné
     */
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setBackgroundColor(Color.RED);
        } else {
            setBackgroundColor(Color.GRAY);
        }
    }

    /**
     * @return le nombre de pièces disponibles
     */
    public int getNombre() {
        return nombre;
    }

    /**
     * @return vrai si le bouton est sélectionné
     */
    @Override
    public boolean isSelected() {
        return selected;
    }
}
