package com.echecs.projet_integrateur.pkgModele;

import java.util.ArrayList;

/**
 * Extension de ModèlePiece pour le tour
 * Created by Simon on 2015-02-24.
 */
public class ModeleTour extends ModelePiece {

    boolean tourBouge, promue;

    /**
     * Le constructeur pour la tour
     *
     * @param couleur   la couleur de la pièce
     * @param promue    si la pièce est le résultat d'une promotion
     * @param posX      la position en x de la pièce
     * @param posY      la position en y de la pièce
     * @param tourBouge si la pièce a déjà bougée
     */
    public ModeleTour(boolean couleur, boolean promue, int posX, int posY, boolean tourBouge) {
        super(couleur, posX, posY);
        this.tourBouge = tourBouge;
        this.promue = promue;
    }

    /**
     * Si la pièce a dejà bougée
     *
     * @return true si la pièce a déjà bougée
     */
    public boolean isBouge() {
        return tourBouge;
    }

    /**
     * Si la pièce est le résultat d'une promotion
     *
     * @return true si la pièce est promue
     */
    public boolean isPromue() {
        return promue;
    }

    /**
     * Changer la position en x de la pièce
     *
     * @param posX la position en x
     */
    @Override
    public void setX(int posX) {
        this.posX = posX;
        tourBouge = true;
    }

    /**
     * Changer la position en y de la pièce
     *
     * @param posY la position en y
     */
    @Override
    public void setY(int posY) {
        this.posY = posY;
        tourBouge = true;
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
        boolean coupAutorise = false;
        if (posX == positionFinaleX || posY == positionFinaleY) {
            coupAutorise = true;
        }
        for (ModelePiece m : lstModelePiece) {
            for (int i = 1; i < Math.max(Math.abs(positionFinaleX - posX), Math.abs(positionFinaleY - posY)); i++) {
                int deplacementX = positionFinaleX - posX;
                int deplacementY = positionFinaleY - posY;
                if (deplacementX == 0) {
                    if (m.getX() == posX && m.getY() == Math.min(posY, positionFinaleY) + i) {
                        coupAutorise = false;
                    }
                } else if (deplacementY == 0) {
                    if (m.getX() == Math.min(posX, positionFinaleX) + i && m.getY() == posY) {
                        coupAutorise = false;
                    }
                }
            }
        }
        return coupAutorise;
    }
}