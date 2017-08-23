package com.echecs.projet_integrateur.pkgActivites;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Affiche le .gif du loading
 */
public class Loading extends LinearLayout {
    /**
     * Initialise le loading
     *
     * @param context nécessaire à l'affichage
     * @param view    le .gif à afficher
     */
    public Loading(Context context, GifWebView view) {
        super(context);
        this.setOrientation(HORIZONTAL);
        TextView txt = new TextView(context);
        txt.setText(" Loading");
        txt.setTextSize(70);
        txt.setTextColor(Color.WHITE);
        view.setBackgroundColor(Color.DKGRAY);
        txt.setGravity(Gravity.CENTER);
        view.setHorizontalScrollBarEnabled(false);
        view.setVerticalScrollBarEnabled(false);
        this.setGravity(Gravity.CENTER);
        this.addView(txt);
        this.addView(view);
        this.setBackgroundColor(Color.DKGRAY);
    }
}
