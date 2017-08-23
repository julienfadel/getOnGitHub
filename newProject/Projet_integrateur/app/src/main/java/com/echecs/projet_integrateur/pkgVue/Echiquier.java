package com.echecs.projet_integrateur.pkgVue;

import android.content.Context;
import android.view.View;
import android.widget.GridLayout;

import com.echecs.projet_integrateur.pkgControleur.Controleur;

import java.util.ArrayList;

/**
 * Le terrain de jeu des pièces, permet de les bouger, élément principal du jeu
 * Created by Simon on 2015-02-24.
 */
public class Echiquier extends GridLayout {

    private ArrayList<GridLayout.Spec> lignes;
    private ArrayList<GridLayout.Spec> colonnes;
    private ArrayList<Case> lstCasesCoupsPossibles;
    private int largeur;
    private int cote;
    private Case[][] tabCase;
    private Case caseSelectionnee = null;
    private int[][] position = new int[8][8];
    private boolean debut;
    private boolean tourDeJeu;
    private Controleur controleur;
    private Fenetre fenetre;
    private boolean pieceSelectionne = false;
    private int positionInitialeX = -1, positionInitialeY = -1, positionFinaleX = -1, positionFinaleY = -1;
    private int preferenceTourner = 0;
    private boolean joueur;
    private boolean couleurOrdinateur;
    private boolean dernierCoup = true;
    private boolean premierCoup;
    private boolean revision;
    private boolean coupsPossibles;
    private boolean couleurBluetooth;
    private boolean bluetooth;
    private int anciennePositionInitialeX;
    private int anciennePositionInitialeY;

    /**
     * Constructeur de l'échiquier pour une partie normale
     *
     * @param context           permet d'afficher les éléments graphiques
     * @param largeur           la largeur de l'écran
     * @param fenetre           la fenêtre dans laquelle est comprise l'échiquier
     * @param joueur            false si l'ordinateur joue
     * @param couleurOrdinateur la couleur de l'ordinateur (true si blanc, false si noir)
     */
    public Echiquier(Context context, int largeur, Fenetre fenetre, boolean joueur, boolean couleurOrdinateur) {
        super(context);
        tourDeJeu = true;
        this.largeur = largeur;
        this.fenetre = fenetre;
        debut = true;
        bluetooth = false;
        this.joueur = joueur;
        revision = false;
        initialiserEchiquier();
        if (!joueur) {
            this.couleurOrdinateur = couleurOrdinateur;
            if (couleurOrdinateur) {
                tournerJeu(false);
            }
        }
    }

    /**
     * Constructeur de l'échiquier pour la révision
     *
     * @param context permet d'afficher des views
     * @param largeur la largeur de l'ecran
     */
    public Echiquier(Context context, int largeur) {
        super(context);
        this.largeur = largeur;
        revision = true;
        initialiserEchiquier();
    }

    /**
     * Constructeur de l'échiquier pour le bluetooth
     *
     * @param context          permet d'afficher les vues
     * @param largeur          la largeur de l'écran
     * @param fenetre          la fenêtre dans laquelle est comprise l'échiquier
     * @param couleurBluetooth true si blanc, false si noir
     */
    public Echiquier(Context context, int largeur, Fenetre fenetre, boolean couleurBluetooth) {
        super(context);
        tourDeJeu = true;
        this.largeur = largeur;
        this.fenetre = fenetre;
        debut = true;
        bluetooth = true;
        preferenceTourner = 0;
        coupsPossibles = false;
        dernierCoup = false;
        this.couleurBluetooth = couleurBluetooth;
        initialiserEchiquier();
        tournerJeu(couleurBluetooth);
    }

    /**
     * Initialise les cases dans un gridLayout
     */
    private void initialiserEchiquier() { //initialise l'échiquier visuellement en créant des cases
        tabCase = new Case[8][8];
        lignes = new ArrayList<>();
        colonnes = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            lignes.add(GridLayout.spec(i));
            colonnes.add(GridLayout.spec(i));
        }
        for (int i = 0; i < lignes.size(); i++) {
            for (int j = 0; j < colonnes.size(); j++) {
                Case c;
                GridLayout.LayoutParams par = new GridLayout.LayoutParams(lignes.get(i), colonnes.get(j));
                cote = largeur / 8;
                par.width = cote;
                par.height = cote;
                if ((i + j) % 2 == 0) {
                    c = new Case(this.getContext(), true, null, j, i, par.width);
                } else {
                    c = new Case(this.getContext(), false, null, j, i, par.width);
                }
                c.setLayoutParams(par);
                addView(c, par);
                tabCase[i][j] = c;
            }
        }
        if (!revision) {
            if (!bluetooth) {
                initialiserListeners();
            } else {
                intialiserListenersBluetooth();
            }
        }
    }

    /**
     * Initialise les listeners des cases pour le bluetooth
     */
    private void intialiserListenersBluetooth() {
        lstCasesCoupsPossibles = new ArrayList<>();
        for (int i = 0; i < tabCase.length; i++) {
            for (int j = 0; j < tabCase[i].length; j++) {
                tabCase[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Piece piece = ((Case) v).getPiece();
                        if (couleurBluetooth == tourDeJeu) {
                            if (!pieceSelectionne) { //si aucune pièce n'est sélectionnée, permet juste de choisir une case sur laquelle il y a une pièce à qui c'est le tour
                                if (piece != null) {
                                    if (tourDeJeu == piece.isBlanc()) {
                                        caseSelectionnee = (Case) v;
                                        piece.setSelectionne(true);
                                        pieceSelectionne = true;
                                        positionInitialeX = ((Case) v).getPositionX();
                                        positionInitialeY = ((Case) v).getPositionY();
                                        if (coupsPossibles) {
                                            remettreBackgroundOriginal(); //effacer anciens coups autorisés
                                            afficherCoupsPossibles(positionInitialeX, positionInitialeY); //afficher les coups autorisés
                                        }
                                    }
                                }
                            } else {
                                if (piece != null) { //si une case contenant une pièce est sélectionnée
                                    if (piece.isSelectionne()) { //si la pièce est déjà sélectionnée, la désélectionne
                                        caseSelectionnee = null;
                                        piece.setSelectionne(false);
                                        pieceSelectionne = false;
                                        positionInitialeX = -1;
                                        positionInitialeY = -1;
                                        if (coupsPossibles) {
                                            remettreBackgroundOriginal();
                                        }
                                    } else if (tourDeJeu == piece.isBlanc()) { //si la pièce est de la même couleur que la pièce sélectionnée, change la sélection
                                        caseSelectionnee = (Case) v;
                                        piece.setSelectionne(true);
                                        tabCase[positionInitialeY][positionInitialeX].getPiece().setSelectionne(false);
                                        positionInitialeX = ((Case) v).getPositionX();
                                        positionInitialeY = ((Case) v).getPositionY();
                                        if (coupsPossibles) {
                                            remettreBackgroundOriginal();
                                            afficherCoupsPossibles(positionInitialeX, positionInitialeY);
                                        }//afficher les coups autorisés
                                    } else { //si la deuxième pièce sélectionnée est de couleur différente que la première ==> capture
                                        caseSelectionnee = null;
                                        positionFinaleX = ((Case) v).getPositionX();
                                        positionFinaleY = ((Case) v).getPositionY();
                                        tabCase[positionInitialeY][positionInitialeX].getPiece().setSelectionne(false);
                                        pieceSelectionne = false;
                                        controleur.verifierMouvement(positionInitialeX, positionInitialeY, positionFinaleX, positionFinaleY);
                                        if (coupsPossibles) {
                                            remettreBackgroundOriginal();
                                        }
                                    }
                                } else { //si aucune pièce, mais seulement une case est sélectionnée, permet de bouger la pièce sur une case vide
                                    caseSelectionnee = null;
                                    positionFinaleX = ((Case) v).getPositionX();
                                    positionFinaleY = ((Case) v).getPositionY();
                                    tabCase[positionInitialeY][positionInitialeX].getPiece().setSelectionne(false);
                                    pieceSelectionne = false;
                                    controleur.verifierMouvement(positionInitialeX, positionInitialeY, positionFinaleX, positionFinaleY);
                                    if (coupsPossibles) {
                                        remettreBackgroundOriginal();
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * Déselectionner une case.
     */
    public void deselectionner() {
        if (caseSelectionnee != null) {
            caseSelectionnee.getPiece().setSelectionne(false);
            pieceSelectionne = false;
        }
    }

    /**
     * Initialise les listeners sur les cases ainsi que sur les pièces.
     */
    private void initialiserListeners() { //initialise chaque listener en 2 parties : quand une pièce est sélectionnée et quand aucune pièce est sélectionnée
        lstCasesCoupsPossibles = new ArrayList<>();
        for (int i = 0; i < tabCase.length; i++) {
            for (int j = 0; j < tabCase[i].length; j++) {
                tabCase[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Piece piece = ((Case) v).getPiece();
                        if (joueur || (!joueur && couleurOrdinateur != tourDeJeu)) {
                            if (!pieceSelectionne) { //si aucune pièce n'est sélectionnée, permet juste de choisir une case sur laquelle il y a une pièce à qui c'est le tour
                                if (piece != null) {
                                    if (tourDeJeu == piece.isBlanc()) {
                                        piece.setSelectionne(true);
                                        pieceSelectionne = true;
                                        positionInitialeX = ((Case) v).getPositionX();
                                        positionInitialeY = ((Case) v).getPositionY();
                                        if (coupsPossibles) {
                                            remettreBackgroundOriginal(); //effacer anciens coups autorisés
                                            afficherCoupsPossibles(positionInitialeX, positionInitialeY); //afficher les coups autorisés
                                        }
                                    }
                                }
                            } else {
                                if (piece != null) { //si une case contenant une pièce est sélectionnée
                                    if (piece.isSelectionne()) { //si la pièce est déjà sélectionnée, la désélectionne
                                        piece.setSelectionne(false);
                                        pieceSelectionne = false;
                                        positionInitialeX = -1;
                                        positionInitialeY = -1;
                                        if (coupsPossibles) {
                                            remettreBackgroundOriginal();
                                        }
                                    } else if (tourDeJeu == piece.isBlanc()) { //si la pièce est de la même couleur que la pièce sélectionnée, change la sélection
                                        piece.setSelectionne(true);
                                        tabCase[positionInitialeY][positionInitialeX].getPiece().setSelectionne(false);
                                        positionInitialeX = ((Case) v).getPositionX();
                                        positionInitialeY = ((Case) v).getPositionY();
                                        if (coupsPossibles) {
                                            remettreBackgroundOriginal();
                                            afficherCoupsPossibles(positionInitialeX, positionInitialeY);
                                        }//afficher les coups autorisés
                                    } else { //si la deuxième pièce sélectionnée est de couleur différente que la première ==> capture
                                        positionFinaleX = ((Case) v).getPositionX();
                                        positionFinaleY = ((Case) v).getPositionY();
                                        tabCase[positionInitialeY][positionInitialeX].getPiece().setSelectionne(false);
                                        pieceSelectionne = false;
                                        controleur.verifierMouvement(positionInitialeX, positionInitialeY, positionFinaleX, positionFinaleY);
                                        if (coupsPossibles) {
                                            remettreBackgroundOriginal();
                                        }
                                    }
                                } else { //si aucune pièce, mais seulement une case est sélectionnée, permet de bouger la pièce sur une case vide
                                    positionFinaleX = ((Case) v).getPositionX();
                                    positionFinaleY = ((Case) v).getPositionY();

                                    tabCase[positionInitialeY][positionInitialeX].getPiece().setSelectionne(false);
                                    pieceSelectionne = false;
                                    controleur.verifierMouvement(positionInitialeX, positionInitialeY, positionFinaleX, positionFinaleY);
                                    if (coupsPossibles) {
                                        remettreBackgroundOriginal();
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * Remettre le background de départ
     */
    private void remettreBackgroundOriginal() {
        if (lstCasesCoupsPossibles.size() != 0) {
            for (Case c : lstCasesCoupsPossibles) {
                c.setPossible(false);
            }
            lstCasesCoupsPossibles.clear();
        }
    }

    /**
     * Afficher les coups possibles
     *
     * @param positionInitialeX la position horizontale x de la piece sur l'échiquier
     * @param positionInitialeY la position verticale y de la piece sur l'échiquier
     */
    private void afficherCoupsPossibles(int positionInitialeX, int positionInitialeY) {
        for (int i = 0; i < tabCase.length; i++) {
            for (int j = 0; j < tabCase[i].length; j++) {
                if (tabCase[i][j].getPiece() != null) {
                    if (!(tabCase[i][j].getPiece() instanceof Roi)) {
                        if (controleur.verifierCoupAutorise(positionInitialeX, positionInitialeY, j, i)) {
                            if (tabCase[i][j].getPiece().isBlanc() != tourDeJeu) {
                                tabCase[i][j].setPossible(true);
                                lstCasesCoupsPossibles.add(tabCase[i][j]);
                            }
                        }
                    }
                } else {
                    if (controleur.verifierCoupAutorise(positionInitialeX, positionInitialeY, j, i)) {
                        tabCase[i][j].setPossible(true);
                        lstCasesCoupsPossibles.add(tabCase[i][j]);
                    }
                }
            }
        }
    }

    /**
     * Change la position visuelle des pièces sur le terrain
     *
     * @param positionChangee le nouveau tableau de position
     */
    public void setPosition(int[][] positionChangee) { //change la position visuelle
        controleur.sauvegarderPosition(positionChangee);
        if (debut) { //si première position
            position = positionChangee;  //sinon position est vide
            premierCoup = true;
        }
        convertirPosition(positionChangee);
        if (!debut) { //si position autre que la première
            tourDeJeu = !tourDeJeu; //change le tour
            controleur.setTourDeJeu(tourDeJeu);
            fenetre.changerBouton(tourDeJeu);
            if (!bluetooth) {
                if (joueur) {
                    if (preferenceTourner == 1) {
                        tournerCases();
                    } else if (preferenceTourner == 2) {
                        tournerJeu(tourDeJeu);
                        fenetre.changerLins(tourDeJeu);
                    }
                }
            }
            position = positionChangee;
            //fenetre.message(tourDeJeu);
            if (premierCoup) {
                premierCoup = false;
                fenetre.setPremierCoup(false);
                fenetre.setTimerEnMarche(true);
            }
        }
        debut = false;
    }

    /**
     * Convertit une position d'un tableau d'entier à des cases visuelles
     *
     * @param positionChangee tableau d'entier de la nouvelle position
     */
    public void convertirPosition(int[][] positionChangee) {
        for (int i = 0; i < position.length; i++) {
            for (int j = 0; j < position[i].length; j++) {
                if (tabCase[i][j].getPiece() != null) {
                    if (tabCase[i][j].getPiece().isDernierCoup()) {
                        tabCase[i][j].getPiece().setDernierCoup(false);
                    }
                }
                if (position[i][j] != 0 && positionChangee[i][j] != 0 && !debut) {
                    if ((position[i][j] / 10) != (positionChangee[i][j] / 10)) {
                        fenetre.changerPiecesMangees(position[i][j]);
                    }
                }
                if (position[i][j] % 10 == 7 && positionChangee[i][j] == 0) {  //prise en passant
                    fenetre.changerPiecesMangees(position[i][j]);
                }
                boolean blanc;
                if (positionChangee[i][j] % 20 < 10) {
                    blanc = true;
                } else {
                    blanc = false;
                }
                tabCase[i][j].setPiece(null);
                boolean defaut = false;
                switch (positionChangee[i][j] % 10) {
                    case 1:
                    case 7:
                        tabCase[i][j].setPiece(new Pion(this.getContext(), blanc, cote));
                        break;
                    case 2:
                        tabCase[i][j].setPiece(new Cavalier(this.getContext(), blanc, cote));
                        break;
                    case 3:
                        tabCase[i][j].setPiece(new Fou(this.getContext(), blanc, cote));
                        break;
                    case 4:
                    case 8:
                        tabCase[i][j].setPiece(new Tour(this.getContext(), blanc, cote));
                        break;
                    case 5:
                        tabCase[i][j].setPiece(new Reine(this.getContext(), blanc, cote));
                        break;
                    case 6:
                    case 9:
                        tabCase[i][j].setPiece(new Roi(this.getContext(), blanc, cote));
                        break;
                    default:
                        defaut = true;
                        break;
                }
                if (!debut && !defaut && !revision) {
                    dernierCoup(i, j);
                }
                tabCase[i][j].invalidate(); //redessine la case changée
                tabCase[i][j].refreshDrawableState();
            }
        }
    }

    /**
     * Changer la couleur de la case du dernier coup joué
     *
     * @param i la position i horizontale de la case
     * @param j la position j verticale de la case
     */

    private void dernierCoup(int i, int j) {
        if (tabCase[i][j] == tabCase[controleur.getPositionFinaleY()][controleur.getPositionFinaleX()] && dernierCoup) {
            tabCase[anciennePositionInitialeY][anciennePositionInitialeX].setCouleurOriginal();
            anciennePositionInitialeX = controleur.getPositionInitialeX();
            anciennePositionInitialeY = controleur.getPositionInitialeY();
            tabCase[controleur.getPositionFinaleY()][controleur.getPositionFinaleX()].getPiece().setDernierCoup(true);
            tabCase[controleur.getPositionInitialeY()][controleur.getPositionInitialeX()].setCouleurTemporaire();
        }
        if (!dernierCoup) { // si oui remettre le dernier coup initiale a la couleur originale
            tabCase[anciennePositionInitialeY][anciennePositionInitialeX].setCouleurOriginal();
        }
    }

    /**
     * changer le controleur
     *
     * @param controleur
     */
    public void setControleur(Controleur controleur) {
        this.controleur = controleur;
    }

    /**
     * Tourner les cases de 180 degrés
     */
    public void tournerCases() {
        for (int i = 0; i < tabCase.length; i++) {
            for (int j = 0; j < tabCase[i].length; j++) {
                if (tourDeJeu) {
                    tabCase[i][j].setRotation(0);
                } else {
                    tabCase[i][j].setRotation(180);
                }
            }
        }
    }

    /**
     * Tourner le jeu au complet
     *
     * @param tour true si blanc false si noir.
     */
    public void tournerJeu(boolean tour) {
        if (tour) {
            setRotation(0);
        } else {
            setRotation(180);
        }
        for (int i = 0; i < tabCase.length; i++) {
            for (int j = 0; j < tabCase[i].length; j++) {
                if (tour) {
                    tabCase[i][j].setRotation(0);
                } else {
                    tabCase[i][j].setRotation(180);
                }
            }
        }
    }

    /**
     * @return le tour de jeu (true si blanc, false si noir)
     */
    public boolean getTourDeJeu() {
        return tourDeJeu;
    }

    /**
     * Change la préférence de la rotation des cases
     *
     * @param preferenceTourner indice de la préférence de rotation des cases
     */
    public void setPreferenceTourner(int preferenceTourner) {
        this.preferenceTourner = preferenceTourner;
        if (!bluetooth) {
            if (joueur) {
                switch (preferenceTourner) {
                    case 0:
                        setRotation(0);
                        for (int i = 0; i < tabCase.length; i++) {
                            for (int j = 0; j < tabCase[i].length; j++) {
                                tabCase[i][j].setRotation(0);
                            }
                        }
                        break;
                    case 1:
                        setRotation(0);
                        tournerCases();
                        break;
                    case 2:
                        tournerJeu(tourDeJeu);
                        break;
                }
            }
        } else {
            this.preferenceTourner = 0;
        }
    }

    /**
     * Changer l'option de l'affichage dernier coup.
     *
     * @param dernierCoup true si l'utilisateur veut voir le dernier coup, false sinon
     */
    public void setDernierCoup(boolean dernierCoup) {
        if (!bluetooth) {
            this.dernierCoup = dernierCoup;
        } else {
            this.dernierCoup = false;
        }
    }

    /**
     * Afficher les coups possibles
     *
     * @param coupsPossibles true si l'utilisateur veut, false sinon.
     */
    public void setCoupsPossibles(boolean coupsPossibles) {
        if (!bluetooth) {
            this.coupsPossibles = coupsPossibles;
            if (!coupsPossibles) {
                remettreBackgroundOriginal();
            }
        } else {
            this.coupsPossibles = false;
        }
    }
}
