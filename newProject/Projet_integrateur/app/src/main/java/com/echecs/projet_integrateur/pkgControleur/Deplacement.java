package com.echecs.projet_integrateur.pkgControleur;

import java.io.Serializable;

/**
 * Contient les données nécessaires à un déplacement
 */
public class Deplacement implements Serializable {
    private int positionInitialeX;
    private int positionInitialeY;
    private int positionFinaleX;
    private int positionFinaleY;
    private int choixPromotion;

    /**
     * @param positionInitialeX la position initiale en X
     * @param positionInitialeY la position initiale en Y
     * @param positionFinaleX   la position finale en X
     * @param positionFinaleY   la position finale en Y
     * @param choixPromotion    le choix de la promotion (0 s'il n'y en a pas)
     */
    public Deplacement(int positionInitialeX, int positionInitialeY, int positionFinaleX, int positionFinaleY, int choixPromotion) {
        this.positionInitialeX = positionInitialeX;
        this.positionInitialeY = positionInitialeY;
        this.positionFinaleX = positionFinaleX;
        this.positionFinaleY = positionFinaleY;
        this.choixPromotion = choixPromotion;
    }

    /**
     * @return la position initiale en X
     */
    public int getPositionInitialeX() {
        return positionInitialeX;
    }

    /**
     * @return la position initiale en Y
     */
    public int getPositionInitialeY() {
        return positionInitialeY;
    }

    /**
     * @return la position finale en X
     */
    public int getPositionFinaleX() {
        return positionFinaleX;
    }

    /**
     * @return la position finale en Y
     */
    public int getPositionFinaleY() {
        return positionFinaleY;
    }

    /**
     * @return le choix de la promotion
     */
    public int getChoixPromotion() {
        return choixPromotion;
    }

    /**
     * change le choixPromotion
     *
     * @param choixPromotion la valeur du choix
     */
    public void setChoixPromotion(int choixPromotion) {
        this.choixPromotion = choixPromotion;
    }
}