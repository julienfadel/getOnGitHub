package com.echecs.projet_integrateur.pkgModele;

import java.util.ArrayList;

/**
 * Une pièce générique pour faire les calculs
 * Created by Simon on 2015-02-24.
 */
public class ModelePiece {

    protected boolean couleur; //blanc = true, noir = false
    protected int posX, posY;

    /**
     * Constructeur pour une pièce
     *
     * @param couleur la couleur de la pièce
     * @param posX    la position en x de la pièce
     * @param posY    la position en y de la pièce
     */
    public ModelePiece(boolean couleur, int posX, int posY) {
        this.couleur = couleur;
        this.posX = posX;
        this.posY = posY;
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
    public boolean directionAutorise(int positionFinaleX, int positionFinaleY, ArrayList<ModelePiece> lstModelePiece, ModeleEchiquier modeleEchiquier, ModelePiece[][] tableauPiece) {
        return true;
    }

    /**
     * Retourne la couleur de la pièce
     *
     * @return la couleur de la pièce
     */
    public boolean getCouleur() {
        return couleur;
    }

    /**
     * Retourne la position en x de la pièce
     *
     * @return la position en x de la pièce
     */
    public int getX() {
        return posX;
    }

    /**
     * Retourne la position en y de la pièce
     *
     * @return la position en y de la pièce
     */
    public int getY() {
        return posY;
    }

    /**
     * Changer la position en x de la pièce
     *
     * @param posX la position en x
     */
    public void setX(int posX) {
        this.posX = posX;
    }

    /**
     * Changer la position en y de la pièce
     *
     * @param posY la position en y
     */
    public void setY(int posY) {
        this.posY = posY;
    }

}
