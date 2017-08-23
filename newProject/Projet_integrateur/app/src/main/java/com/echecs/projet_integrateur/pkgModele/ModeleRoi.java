package com.echecs.projet_integrateur.pkgModele;

import java.util.ArrayList;

/**
 * Extension de ModèlePiece pour le roi
 * Created by Simon on 2015-02-24.
 */
public class ModeleRoi extends ModelePiece {

    boolean roiBouge;

    /**
     * Constructeur pour le roi
     *
     * @param couleur  la couleur de la pièce
     * @param posX     la position en x de la pièce
     * @param posY     la position en y de la pièce
     * @param roiBouge si le roi a déjà bougé
     */
    public ModeleRoi(boolean couleur, int posX, int posY, boolean roiBouge) {
        super(couleur, posX, posY);
        this.roiBouge = roiBouge;
    }

    /**
     * Si la pièce a dejà bougée
     *
     * @return true si la pièce a déjà bougée
     */
    public boolean isBouge() {
        return roiBouge;
    }

    /**
     * Changer la position en x de la pièce
     *
     * @param posX la position en x
     */
    @Override
    public void setX(int posX) {
        this.posX = posX;
        roiBouge = true;
    }

    /**
     * Changer la position en y de la pièce
     *
     * @param posY la position en y
     */
    @Override
    public void setY(int posY) {
        this.posY = posY;
        roiBouge = true;
    }

    /**
     * Évalue si la direction du mouvement est autorisé
     *
     * @param positionFinaleX position finale en x à vérifier
     * @param positionFinaleY position finale en y a vérifier
     * @param lstModelePiece  liste des pièces
     * @param modeleEchiquier l'échiquier
     * @param tableauPiece    un tableau des pièces
     * @return true si la direction est autorisée
     */
    @Override
    public boolean directionAutorise(int positionFinaleX, int positionFinaleY, ArrayList<ModelePiece> lstModelePiece, ModeleEchiquier modeleEchiquier, ModelePiece[][] tableauPiece) {
        boolean coupAutorise;

        if (!roiBouge && (Math.abs(posX - positionFinaleX) == 2) && (posY == positionFinaleY)) {
            coupAutorise = true;

            ModelePiece tour = null;

            for (ModelePiece piece : lstModelePiece) {
                if (piece instanceof ModeleTour && piece.getCouleur() == couleur) {
                    if (Math.abs(piece.getX() - positionFinaleX) <= 2 && piece.getY() == posY) {
                        tour = piece;
                    }
                }
            }

            if (tour != null) {
                if (!((ModeleTour) tour).isBouge()) {
                    for (int i = (Math.min(posX, tour.getX()) + 1); i < Math.max(posX, tour.getX()); i++) {
                        for (ModelePiece piece : lstModelePiece) {
                            if (piece.getX() == i && piece.getY() == posY) {
                                coupAutorise = false;
                            }
                        }
                    }

                    if (coupAutorise) {
                        if (modeleEchiquier.enEchec(couleur, lstModelePiece)) {
                            coupAutorise = false;
                        } else {
                            coupAutorise = !modeleEchiquier.verifierEchec(this, null, posX + (positionFinaleX - posX) / 2, posY, lstModelePiece);
                        }
                    }
                } else {
                    coupAutorise = false;
                }
            } else {
                coupAutorise = false;
            }
        } else if (Math.abs(posX - positionFinaleX) <= 1 && Math.abs(posY - positionFinaleY) <= 1) {
            coupAutorise = true;
        } else {
            coupAutorise = false;
        }

        return coupAutorise;
    }
}