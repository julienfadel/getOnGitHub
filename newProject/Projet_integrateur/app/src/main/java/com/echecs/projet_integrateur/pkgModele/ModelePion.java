package com.echecs.projet_integrateur.pkgModele;

import java.util.ArrayList;

/**
 * Extension de ModèlePiece pour le pion
 * Created by Simon on 2015-02-24.
 */
public class ModelePion extends ModelePiece {

    boolean pionBouge;
    boolean deuxDernierTour;

    /**
     * Constructeur pour le pion
     *
     * @param couleur         la couleur de la pièce
     * @param posX            la position en x de la pièce
     * @param posY            la position en y de la pièce
     * @param pionBouge       si le pion a bougé
     * @param deuxDernierTour si le pion a bougé dans les deux derniers tours
     */
    public ModelePion(boolean couleur, int posX, int posY, boolean pionBouge, boolean deuxDernierTour) {
        super(couleur, posX, posY);
        this.pionBouge = pionBouge;
        this.deuxDernierTour = deuxDernierTour;
    }

    /**
     * Si la pièce a dejà bougée
     *
     * @return true si la pièce a déjà bougée
     */
    public boolean isBouge() {
        return pionBouge;
    }

    /**
     * Si le pion a bougé dans les deux derniers tours
     *
     * @return vrai si a bougé dans les deux derniers tours
     */
    public boolean isDeuxDernierTour() {
        return deuxDernierTour;
    }

    /**
     * Définir si le pion a bougé dans les deux derniers tours
     *
     * @param deuxDernierTour si a bougé dans les deux derniers tours
     */
    public void setDeuxDernierTour(boolean deuxDernierTour) {
        this.deuxDernierTour = deuxDernierTour;
    }

    /**
     * Changer la position en x de la pièce
     *
     * @param posX la position en x
     */
    @Override
    public void setX(int posX) {
        this.posX = posX;
        pionBouge = true;
    }

    /**
     * Changer la position en y de la pièce
     *
     * @param posY la position en y
     */
    @Override
    public void setY(int posY) {
        if (Math.abs(posY - this.posY) == 2) {
            deuxDernierTour = true;
        }
        this.posY = posY;
        pionBouge = true;
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
        int deplacement;

        if (tableauPiece == null) {
            tableauPiece = new ModelePiece[8][8];

            for (ModelePiece piece : lstModelePiece) {
                tableauPiece[piece.getY()][piece.getX()] = piece;
            }
        }

        if (couleur) {
            deplacement = -1;
        } else {
            deplacement = 1;
        }

        if (positionFinaleX == posX && positionFinaleY - posY == 2 * deplacement) {
            if (!pionBouge) {
                if (tableauPiece[posY + deplacement][positionFinaleX] == null && tableauPiece[posY + 2 * deplacement][positionFinaleX] == null) {
                    coupAutorise = true;
                }
            }
        } else if (positionFinaleX == posX && positionFinaleY - posY == deplacement) {
            if (tableauPiece[posY + deplacement][positionFinaleX] == null) {
                coupAutorise = true;
            }
        } else if (Math.abs(positionFinaleX - posX) == 1 && positionFinaleY - posY == deplacement) {
            if (positionFinaleX - posX == 1) {
                if (tableauPiece[posY + deplacement][posX + 1] != null) {
                    coupAutorise = true;
                } else {
                    if (tableauPiece[posY][posX + 1] != null) {
                        if (tableauPiece[posY][posX + 1] instanceof ModelePion && tableauPiece[posY][posX + 1].getCouleur() != couleur) {
                            if (((ModelePion) tableauPiece[posY][posX + 1]).isDeuxDernierTour()) {
                                coupAutorise = true;
                            }
                        }
                    }
                }
            } else if (positionFinaleX - posX == -1) {
                if (tableauPiece[posY + deplacement][posX - 1] != null) {
                    coupAutorise = true;
                } else {
                    if (tableauPiece[posY][posX - 1] != null) {
                        if (tableauPiece[posY][posX - 1] instanceof ModelePion && tableauPiece[posY][posX - 1].getCouleur() != couleur) {
                            if (((ModelePion) tableauPiece[posY][posX - 1]).isDeuxDernierTour()) {
                                coupAutorise = true;
                            }
                        }
                    }
                }
            }
        }

        return coupAutorise;
    }
}