package com.echecs.projet_integrateur.pkgModele;

import java.util.ArrayList;

/**
 * Extension de ModèlePiece pour le cavalier
 * Created by Simon on 2015-02-24.
 */
public class ModeleCavalier extends ModelePiece {

    boolean promue;

    /**
     * @param couleur
     * @param promue
     * @param posX
     * @param posY
     */
    public ModeleCavalier(boolean couleur, boolean promue, int posX, int posY) {
        super(couleur, posX, posY);
        this.promue = promue;
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
        if ((Math.abs(posX - positionFinaleX) == 2 && Math.abs(posY - positionFinaleY) == 1) || (Math.abs(posX - positionFinaleX) == 1 && Math.abs(posY - positionFinaleY) == 2)) { //vérifie que le cavalier bouge de 2 dans une direction et de 1 dans une autre
            return true;
        } else {
            return false;
        }
    }

    /**
     * Si la pièce est le résultat d'une promotion
     *
     * @return true si la pièce est promue
     */
    public boolean isPromue() {
        return promue;
    }
}