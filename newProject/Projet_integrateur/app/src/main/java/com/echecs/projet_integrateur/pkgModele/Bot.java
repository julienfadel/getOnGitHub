package com.echecs.projet_integrateur.pkgModele;

import android.os.Handler;
import android.os.Message;

import com.echecs.projet_integrateur.pkgControleur.Deplacement;

import java.util.ArrayList;
import java.util.Random;

/**
 * La classe pour l'intelligence artificielle
 *
 * @author Simon St-Amant, Julien Fadel, Jérémie
 */
public class Bot extends Thread {

    private boolean couleurBot;
    private int nbCoupsMax;
    private ModeleEchiquier echiquier;
    private int valeurFonction;
    private ArrayList<ModelePiece> lstPieceInitiale;
    private ArrayList<Deplacement> lstDeplacementsAChoisir;
    private Random random = new Random();
    private int compteurMax, compteurMin, compteurFeuille;
    private ArrayList<Integer> valeurs;
    private ArrayList<Deplacement> lstDeplacementsPossibles;
    private Handler handler;


    /**
     * Constructeur de l'intelligence artificielle
     *
     * @param couleurBot la couleur que joue l'ordinateur
     * @param nbCoupsMax le niveau de l'intelligence artificielle
     * @param echiquier  le ModeleEchiquier, pour avoir accès au restant du programme
     */
    public Bot(boolean couleurBot, int nbCoupsMax, ModeleEchiquier echiquier) {
        this.couleurBot = couleurBot;
        this.nbCoupsMax = nbCoupsMax;
        this.echiquier = echiquier;
    }

    /**
     * Méthode qui met en thread les calculs pour l'intelligence artificielle
     */
    public void run() {
        compteurMax = 0;
        compteurMin = 0;
        compteurFeuille = 0;
        lstDeplacementsAChoisir = new ArrayList<>();
        valeurs = new ArrayList<>();
        valeurFonction = fonctionMinimax(lstPieceInitiale, null, 0, -401, 401, 0);
        lstDeplacementsPossibles = new ArrayList<>();
        for (int i = 0; i < lstDeplacementsAChoisir.size(); i++) {
            if (valeurs.get(i) == valeurFonction) {
                lstDeplacementsPossibles.add(lstDeplacementsAChoisir.get(i));
            }
        }
        Message msg = new Message();
        Deplacement deplacementChoisi = lstDeplacementsPossibles.get(random.nextInt(lstDeplacementsPossibles.size()));
        ;
        msg.obj = deplacementChoisi;
        msg.setTarget(handler);
        msg.sendToTarget();
        return;
    }

    /**
     * Méthode qui part le calcul pour l'intelligence artificielle
     *
     * @param lstPiece       la liste des pièces actuelle
     * @param lstDeplacement la liste des déplacements autorisés au prochain coup
     */
    public void calculerCoupOrdinateur(ArrayList<ModelePiece> lstPiece, ArrayList<Deplacement> lstDeplacement) {
        this.lstPieceInitiale = lstPiece;
        //this.lstDeplacementInitiale = lstDeplacement;
        Random random = new Random();
        if (nbCoupsMax == 0) {
            echiquier.jouerCoupOrdinateur(lstDeplacement.get(random.nextInt(lstDeplacement.size())));
        } else {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    echiquier.jouerCoupOrdinateur((Deplacement) msg.obj);
                }
            };
            start();
        }
    }

    /**
     * Fonction minimax qui cherche le meilleur coup possible
     *
     * @param lstPieces      la liste des pièces à ce moment
     * @param deplacement    le deplacement auquel réfère cette passe de minimax
     * @param nbCoups        le nombre de coups que le bot a calculé (la profondeur du calcul à ce point)
     * @param alpha          valeur dans le calcul d'optimisation
     * @param beta           valeur dans le calcul d'optimisation
     * @param nbDernierCoups le nombre de coups autorisés qu'il y avait au dernier tour
     * @return
     */
    private int fonctionMinimax(ArrayList<ModelePiece> lstPieces, Deplacement deplacement, int nbCoups, int alpha, int beta, int nbDernierCoups) {
        int valeur = 0;
        ArrayList<Deplacement> lstDeplacements;
        if (nbCoups == nbCoupsMax) {
            lstPieces = echiquier.changerPositionOrdinateur(deplacement, lstPieces);
            if (nbCoups % 2 == 1) {
                lstDeplacements = echiquier.genererLstDeplacement(!couleurBot, lstPieces);
                valeur = evaluer(lstPieces, lstDeplacements, couleurBot, nbCoups, nbDernierCoups);
            } else {
                lstDeplacements = echiquier.genererLstDeplacement(couleurBot, lstPieces);
                valeur = evaluer(lstPieces, lstDeplacements, !couleurBot, nbCoups, nbDernierCoups);
            }

            //afficherLstPieces(lstPieces);
            if (nbCoupsMax == 1) {
                lstDeplacementsAChoisir.add(deplacement);
                valeurs.add(valeur);
            }
            compteurFeuille++;
            return valeur;
        } else if (nbCoups % 2 == 1) {
            valeur = 400;
            compteurMin++;
            lstPieces = echiquier.changerPositionOrdinateur(deplacement, lstPieces);
            lstDeplacements = echiquier.genererLstDeplacement(!couleurBot, lstPieces);
            if (lstDeplacements.isEmpty()) {
                valeur = evaluer(lstPieces, lstDeplacements, couleurBot, nbCoups, nbDernierCoups);
                if (nbCoups == 1) {
                    lstDeplacementsAChoisir.add(deplacement);
                    valeurs.add(valeur);
                }
                return valeur;
            } else {
                for (Deplacement d : lstDeplacements) {
                    valeur = Math.min(valeur, fonctionMinimax(lstPieces, d, nbCoups + 1, alpha, beta, lstDeplacements.size()));
                    //if (valeur <= alpha) { //Coupure alpha
                    //    if (nbCoups == 1) {
                    //        lstDeplacementsAChoisir.add(deplacement);
                    //        valeurs.add(valeur);
                    //   }
                    //    return valeur;
                    //}
                    beta = Math.min(beta, valeur);
                }
                if (nbCoups == 1) {
                    lstDeplacementsAChoisir.add(deplacement);
                    valeurs.add(valeur);
                }
            }
        } else if (nbCoups % 2 == 0) {
            compteurMax++;
            valeur = -400;
            if (deplacement != null) {
                lstPieces = echiquier.changerPositionOrdinateur(deplacement, lstPieces);
            }
            lstDeplacements = echiquier.genererLstDeplacement(couleurBot, lstPieces);
            if (lstDeplacements.isEmpty()) {
                valeur = evaluer(lstPieces, lstDeplacements, !couleurBot, nbCoups, nbDernierCoups);
                return valeur;
            } else {
                for (Deplacement d : lstDeplacements) {
                    valeur = Math.max(valeur, fonctionMinimax(lstPieces, d, nbCoups + 1, alpha, beta, lstDeplacements.size()));
                    //if(valeur >= beta) {
                    //    if (nbCoups!=0) {
                    //        return valeur;
                    //    }
                    //}
                    alpha = Math.max(alpha, valeur);
                }
            }
        }
        return valeur;
    }

    /**
     * Fonction d'évaluation qui renvoie la valeur d'une position donnée
     *
     * @param lstPieces                 la liste des pièces dans cette position
     * @param lstDeplacementsNotCouleur la liste des déplacements autorisés au prochain tour
     * @param couleurCoup               la couleur du joueur ayant fait le dernier coup
     * @param nbCoups                   la profondeur du calcul
     * @param nbCoupsCouleur            le nombre de coups autorisés au dernier tour pour le joueur ayant fait le dernier coup
     * @return la valeur de la position
     */
    private int evaluer(ArrayList<ModelePiece> lstPieces, ArrayList<Deplacement> lstDeplacementsNotCouleur, boolean couleurCoup, int nbCoups, int nbCoupsCouleur) {
        int valeur;
        int nbPointsCouleur = 0;
        int nbPointsNotCouleur = 0;
        int[] tableauPionsCouleur = {0, 0, 0, 0, 0, 0, 0, 0};
        int[] tableauPionsNotCouleur = {0, 0, 0, 0, 0, 0, 0, 0};
        int nbPionsIsolesCouleur = 0;
        int nbPionsIsolesNotCouleur = 0;
        int nbPionsDoublesCouleur = 0;
        int nbPionsDoublesNotCouleur = 0;

        boolean enEchec = echiquier.enEchec(!couleurCoup, lstPieces);

        if (lstDeplacementsNotCouleur.isEmpty()) {
            if (enEchec) {
                if (couleurCoup == couleurBot) {
                    valeur = (200 - nbCoups);
                } else {
                    valeur = -(200 - nbCoups);
                }
            } else {
                if (couleurCoup == couleurBot) {
                    valeur = (-nbCoups);
                } else {
                    valeur = nbCoups;
                }
            }
        } else {
            for (ModelePiece piece : lstPieces) {
                int nbPoints = 0;

                if (piece instanceof ModelePion) {
                    if (piece.getCouleur() == couleurBot) {
                        nbPointsCouleur += 1;
                        tableauPionsCouleur[piece.getX()] += 1;
                    } else {
                        nbPointsNotCouleur += 1;
                        tableauPionsNotCouleur[piece.getX()] += 1;
                    }
                } else if (piece instanceof ModeleCavalier) {
                    if (piece.getCouleur() == couleurBot) {
                        nbPointsCouleur += 3;
                    } else {
                        nbPointsNotCouleur += 3;
                    }
                } else if (piece instanceof ModeleFou) {
                    if (piece.getCouleur() == couleurBot) {
                        nbPointsCouleur += 3;
                    } else {
                        nbPointsNotCouleur += 3;
                    }
                } else if (piece instanceof ModeleTour) {
                    if (piece.getCouleur() == couleurBot) {
                        nbPointsCouleur += 5;
                    } else {
                        nbPointsNotCouleur += 5;
                    }
                } else if (piece instanceof ModeleReine) {
                    if (piece.getCouleur() == couleurBot) {
                        nbPointsCouleur += 9;
                    } else {
                        nbPointsNotCouleur += 9;
                    }
                }
            }

            for (int i = 0; i < 8; i++) {
                if (tableauPionsCouleur[i] > 1) {
                    nbPionsDoublesCouleur++;
                }
                if (tableauPionsNotCouleur[i] > 1) {
                    nbPionsDoublesNotCouleur++;
                }
            }

            for (int i = 1; i < 7; i++) {
                if (tableauPionsCouleur[i] > 0 && tableauPionsCouleur[i - 1] == 0 && tableauPionsCouleur[i + 1] == 0) {
                    nbPionsIsolesCouleur += tableauPionsCouleur[i];
                }
                if (tableauPionsNotCouleur[i] > 0 && tableauPionsNotCouleur[i - 1] == 0 && tableauPionsNotCouleur[i + 1] == 0) {
                    nbPionsIsolesNotCouleur += tableauPionsNotCouleur[i];
                }
            }

            if (tableauPionsCouleur[0] > 0 && tableauPionsCouleur[1] == 0) {
                nbPionsIsolesCouleur += tableauPionsCouleur[0];
            }
            if (tableauPionsNotCouleur[0] > 0 && tableauPionsNotCouleur[1] == 0) {
                nbPionsIsolesNotCouleur += tableauPionsNotCouleur[0];
            }

            if (tableauPionsCouleur[7] > 0 && tableauPionsCouleur[6] == 0) {
                nbPionsIsolesCouleur += tableauPionsCouleur[6];
            }
            if (tableauPionsNotCouleur[7] > 0 && tableauPionsNotCouleur[6] == 0) {
                nbPionsIsolesNotCouleur += tableauPionsNotCouleur[6];
            }

            valeur = (int) Math.round((nbPointsCouleur - nbPointsNotCouleur) - 0.5 * (nbPionsDoublesCouleur - nbPionsDoublesNotCouleur + nbPionsIsolesCouleur - nbPionsIsolesNotCouleur) + 0.05 * (nbCoupsCouleur - lstDeplacementsNotCouleur.size()));
        }
        return valeur;
    }
}
