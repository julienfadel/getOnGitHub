package com.echecs.projet_integrateur.pkgVue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.RelativeLayout;

/**
 * Les cases graphiques de l'échiquier, contiennent les pièces
 * Created by Simon on 2015-02-17.
 */

public class Case extends RelativeLayout {

    private Piece piece;
    private int positionX;
    private int positionY;
    private boolean couleur;
    private ViewDeplacementPossible v;
    private int pad;

    /**
     * initialise la case
     *
     * @param context   permet d'afficher la case
     * @param couleur   true si blanc, false si noir
     * @param piece     la pièce à mettre sur la case
     * @param positionX position horizontale dans l'échiquier
     * @param positionY position verticale dans l'échiquier
     * @param largeur   la largeur de l'écran
     */
    public Case(Context context, boolean couleur, Piece piece, int positionX, int positionY, int largeur) {
        super(context);
        this.couleur = couleur;
        setCouleurOriginal();
        pad = largeur / 13;
        if (piece != null) {
            addView(piece);
            piece.setPadding(pad, pad, pad, pad);
        }
        this.positionX = positionX;
        this.positionY = positionY;
        v = new ViewDeplacementPossible(this.getContext(), largeur);
        v.draw(new Canvas());
        this.addView(v);
        v.setVisibility(GONE);
        v.bringToFront();
        invalidate();
    }

    /**
     * change la couleur de la case en alternance
     */
    public void setCouleurOriginal() {
        if (couleur) {
            setBackgroundColor(Color.rgb(240, 217, 181));
        } else {
            setBackgroundColor(Color.rgb(181, 136, 99));
        }
    }

    /**
     * change la couleur temporaire pour afficher le dernier coup
     */
    public void setCouleurTemporaire() {
        setBackgroundColor(Color.LTGRAY);
    }

    /**
     * Rend la vue sur la case visible qui indique si je peux me déplacer sur cette case
     *
     * @param afficher true si je peux me déplacer sur cette case, false si je ne peux pas
     */
    public void setPossible(boolean afficher) {
        if (afficher) {
            v.setVisibility(VISIBLE);
            v.bringToFront();
        } else {
            v.setVisibility(GONE);
        }
    }

    /**
     * Rajoute la pièce sur une case
     *
     * @param piece la pièce à mettre sur la case
     */
    public void setPiece(Piece piece) {
        if (piece == null && this.piece != null) {
            removeView(this.piece);
        }
        this.piece = piece;
        if (piece != null) {
            addView(piece);
            piece.setPadding(pad, pad, pad, pad);
        }
    }

    /**
     * @return la position horizontale de la pièce sur l'échiquier
     */
    public int getPositionX() {
        return positionX;
    }

    /**
     * @return la position verticale de la pièce sur l'échiquier
     */
    public int getPositionY() {
        return positionY;
    }

    /**
     * @return la pièce sur la case
     */
    public Piece getPiece() {
        return piece;
    }
}
