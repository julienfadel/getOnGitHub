package com.echecs.projet_integrateur.pkgVue;

/** Classe qui permet de dessiner le cercle rouge, qui represente les déplacements possibles pour une certaine pièce sur l'échiquier.
 * Created by Julien on 2015-04-14.
 */


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ViewDeplacementPossible extends View {
    Paint paint = new Paint();
    int largeur;

    /**
     * Initialise le ViewDeplacementPossible
     *
     * @param context permet d'afficher
     * @param largeur la largeur de la case
     */
    public ViewDeplacementPossible(Context context, int largeur) {
        super(context);
        this.largeur = largeur;
    }

    /**
     * Méthode qui dessine le cercle
     *
     * @param canvas le pinceau qui permet de dessiner le cercle
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int rayon = largeur / 6;
        paint.setColor(Color.RED);
        canvas.drawCircle(largeur / 2, largeur / 2, rayon, paint);
        setBackgroundColor(Color.TRANSPARENT);
    }
}
