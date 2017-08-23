package com.echecs.projet_integrateur.pkgModele;

import java.util.ArrayList;

/**
 * Extension de ModèlePiece pour la reine
 * Created by Simon on 2015-02-24.
 */
public class ModeleReine extends ModelePiece {

    boolean promue;

    /**
     * Le constructeur pour la reine
     *
     * @param couleur la couleur de la pièce
     * @param promue  si la pièce est le résultat d'une promotion
     * @param posX    la position en x de la pièce
     * @param posY    la position en y de la pièce
     */
    public ModeleReine(boolean couleur, boolean promue, int posX, int posY) {
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
        boolean coupAutorise = false;
        if ((posX == positionFinaleX || posY == positionFinaleY) || (Math.abs(posX - positionFinaleX) == Math.abs(posY - positionFinaleY))) {
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
                } else if (deplacementX != 0 && deplacementY != 0) {
                    if (deplacementX * deplacementY > 0) {
                        if (m.getX() == Math.min(posX, positionFinaleX) + i && m.getY() == Math.min(posY, positionFinaleY) + i) {
                            coupAutorise = false;
                        }
                    } else {
                        if (m.getX() == Math.min(posX, positionFinaleX) + Math.abs(deplacementX) - i && m.getY() == Math.min(posY, positionFinaleY) + i) {
                            coupAutorise = false;
                        }
                    }
                }
            }
        }
        return coupAutorise;
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
