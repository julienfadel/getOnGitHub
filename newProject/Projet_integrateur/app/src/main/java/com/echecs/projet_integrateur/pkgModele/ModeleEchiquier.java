package com.echecs.projet_integrateur.pkgModele;

import android.content.Context;

import com.echecs.projet_integrateur.pkgControleur.Controleur;
import com.echecs.projet_integrateur.pkgControleur.ControleurBluetooth;
import com.echecs.projet_integrateur.pkgControleur.Deplacement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Gère les calculs pour vérifier les coups autorisé et l'ordinateur
 * Created by Simon on 2015-02-24.
 */
public class ModeleEchiquier {
    Context context;
    private ArrayList<ModelePiece> lstModelePiece = new ArrayList<>();
    private ArrayList<Deplacement> lstDeplacement = new ArrayList<>();
    private Controleur controleur;
    private Bot ordinateur;
    private boolean tourDeJeu;
    private boolean envoyerPromotion;
    private ModelePiece[][] tmpTableauPiece;
    boolean couleurOrdinateur;
    int difficulte;
    private Deplacement deplacementAEnvoyer;

    /**
     * Le constructeur
     *
     * @param context    pour la lecture des fichiers
     * @param controleur le controleur du jeu
     */
    public ModeleEchiquier(Context context, Controleur controleur) {
        this.context = context;
        this.controleur = controleur;
        initialiserEchiquier();
    }

    /**
     * Initialise les paramètres de l'intelligence artificielle
     *
     * @param couleurOrdinateur la couleur que l'ordinateur joue
     * @param difficulte        le niveau de difficulté de l'ordinateur
     */
    public void initialiserParametresOrdinateur(boolean couleurOrdinateur, int difficulte) {
        this.couleurOrdinateur = couleurOrdinateur;
        this.difficulte = difficulte;
    }

    /**
     * Lis le fichier de la position initiale et crée la liste des pièces
     */
    private void initialiserEchiquier() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("positionInitiale.txt")));
            String ligne = reader.readLine();
            int i = 0;
            while (ligne != null) {
                String tab[] = ligne.split(" ");
                for (int j = 0; j < tab.length; j++) {
                    int valeur = Integer.parseInt(tab[j]);
                    boolean couleur, promue;
                    if (valeur % 20 > 10) {
                        couleur = false;
                    } else {
                        couleur = true;
                    }
                    if (valeur > 20) {
                        promue = true;
                    } else {
                        promue = false;
                    }
                    ModelePiece modelePiece;
                    switch (valeur % 10) {
                        case 1:
                            boolean bouge = false;
                            if (couleur) {
                                if (i < 6) {
                                    bouge = true;
                                }
                            } else {
                                if (i > 1) {
                                    bouge = true;
                                }
                            }
                            modelePiece = new ModelePion(couleur, j, i, bouge, false);
                            break;
                        case 2:
                            modelePiece = new ModeleCavalier(couleur, promue, j, i);
                            break;
                        case 3:
                            modelePiece = new ModeleFou(couleur, promue, j, i);
                            break;
                        case 4:
                            modelePiece = new ModeleTour(couleur, promue, j, i, false);
                            break;
                        case 5:
                            modelePiece = new ModeleReine(couleur, promue, j, i);
                            break;
                        case 6:
                            modelePiece = new ModeleRoi(couleur, j, i, false);
                            break;
                        case 7:
                            modelePiece = new ModelePion(couleur, j, i, true, true);
                            break;
                        case 8:
                            modelePiece = new ModeleTour(couleur, promue, j, i, true);
                            break;
                        case 9:
                            modelePiece = new ModeleRoi(couleur, j, i, true);
                            break;
                        default:
                            modelePiece = null;
                            break;
                    }
                    if (modelePiece != null) {
                        lstModelePiece.add(modelePiece);
                    }

                }

                i++;
                ligne = reader.readLine();
            }
            setLstDeplacement(true);
        } catch (IOException e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }

    /**
     * Renvoit un tableau représentant la position des pièces sur l'échiquier
     *
     * @return le tableau avec la position de chaque pièce sur le jeu
     */
    public int[][] getPosition() {
        int[][] position = new int[8][8];

        for (ModelePiece piece : lstModelePiece) {
            int valeur = 0;

            if (piece instanceof ModelePion) {
                if (((ModelePion) piece).isDeuxDernierTour()) {
                    valeur = 7;
                } else {
                    valeur = 1;
                }
            } else if (piece instanceof ModeleCavalier) {
                valeur = 2;
                if (((ModeleCavalier) piece).isPromue()) {
                    valeur += 20;
                }
            } else if (piece instanceof ModeleFou) {
                valeur = 3;
                if (((ModeleFou) piece).isPromue()) {
                    valeur += 20;
                }
            } else if (piece instanceof ModeleTour) {
                if (!((ModeleTour) piece).isBouge()) {
                    valeur = 8;
                } else {
                    valeur = 4;
                }
                if (((ModeleTour) piece).isPromue()) {
                    valeur += 20;
                }
            } else if (piece instanceof ModeleReine) {
                valeur = 5;
                if (((ModeleReine) piece).isPromue()) {
                    valeur += 20;
                }
            } else if (piece instanceof ModeleRoi) {
                if (!((ModeleRoi) piece).isBouge()) {
                    valeur = 9;
                } else {
                    valeur = 6;
                }
            }

            if (valeur != 0) {
                if (!piece.getCouleur()) {
                    valeur += 10;
                }
                position[piece.getY()][piece.getX()] = valeur;
            }
        }

        return position;
    }

    /**
     * Trouver le meilleur coup pour l'ordinateur
     */
    public void calculerCoupOrdinateur() {
        ordinateur = new Bot(couleurOrdinateur, difficulte, this);
        ordinateur.calculerCoupOrdinateur(copierListe(lstModelePiece), lstDeplacement);
    }

    /**
     * Faire un vrai déplacement de l'ordinateur
     *
     * @param deplacementOrdinateur le déplacement
     */
    public void jouerCoupOrdinateur(Deplacement deplacementOrdinateur) {
        controleur.setPositionInitialeX(deplacementOrdinateur.getPositionInitialeX());
        controleur.setPositionFinaleX(deplacementOrdinateur.getPositionFinaleX());
        controleur.setPositionInitialeY(deplacementOrdinateur.getPositionInitialeY());
        controleur.setPositionFinaleY(deplacementOrdinateur.getPositionFinaleY());
        changerPosition(deplacementOrdinateur, true, false);
    }

    /**
     * Faire un changement de position dans les calculs de l'ordinateur
     *
     * @param deplacement      le deplacement
     * @param lstPieceInitiale la liste des pièces
     * @return une nouvelle liste de pièces
     */
    public ArrayList<ModelePiece> changerPositionOrdinateur(Deplacement deplacement, ArrayList<ModelePiece> lstPieceInitiale) {
        ArrayList<ModelePiece> lstPieces = copierListe(lstPieceInitiale);

        int statut = faireChangementPosition(deplacement, true, lstPieces);

        switch (statut) {
            case 4:
                break;
            case 2:
                promotion(deplacement, true, lstPieces);
                break;
        }

        return lstPieces;
    }

    /**
     * Faire un vrai changement de position
     *
     * @param deplacement    le déplacement
     * @param coupOrdinateur si c'est un coup de l'ordinateur
     * @param envoyerCoup    si c'est en mode bluetooth
     */
    public void changerPosition(Deplacement deplacement, boolean coupOrdinateur, boolean envoyerCoup) {
        int statut = coupJoueur(deplacement, coupOrdinateur, lstModelePiece, lstDeplacement);
        deplacementAEnvoyer = deplacement;
        envoyerPromotion = false;

        switch (statut) {
            case 1:
                envoyerCoup = false;
                break;
            case 2:
                promotion(deplacement, false, lstModelePiece);
                envoyerCoup = false;
                break;
            case 3:
                envoyerPromotion = true;
                controleur.getPromotion(deplacement, tourDeJeu);
                break;
            case 4:
                setLstDeplacement(!tourDeJeu);
                controleur.setPosition(getPosition());
                break;
        }

        if (envoyerCoup && !envoyerPromotion) {
            if (controleur instanceof ControleurBluetooth) {
                ((ControleurBluetooth) controleur).envoyerDeplacement(deplacementAEnvoyer);
            }
        }
    }

    /**
     * Fait un changement de position quand c'est un coup sur le vrai échiquier
     *
     * @param deplacement     le déplacement
     * @param coupOrdinateur  si c'est un ordinateur
     * @param lstPieces       la liste des pièces
     * @param lstDeplacements la liste des déplacements autorisés
     * @return l'état du changement de position
     */
    public int coupJoueur(Deplacement deplacement, boolean coupOrdinateur, ArrayList<ModelePiece> lstPieces, ArrayList<Deplacement> lstDeplacements) {
        boolean coupAutorise = false;
        for (Deplacement deplacement2 : lstDeplacements) { //vérifie si le coup est autorisé
            if ((deplacement.getPositionInitialeX() == deplacement2.getPositionInitialeX()) && (deplacement.getPositionInitialeY() == deplacement2.getPositionInitialeY()) && (deplacement.getPositionFinaleX() == deplacement2.getPositionFinaleX()) && (deplacement.getPositionFinaleY() == deplacement2.getPositionFinaleY())) {
                coupAutorise = true;
            }
        }

        if (coupAutorise) {
            return faireChangementPosition(deplacement, coupOrdinateur, lstPieces);
        } else {
            return 1;
        }

    }

    /**
     * Effectue un changement de position à une liste
     *
     * @param deplacement    le déplacement
     * @param coupOrdinateur si c'est un coup de l'ordinateur
     * @param lstPieces      la liste des pièces
     * @return l'état du changement du position
     */
    public int faireChangementPosition(Deplacement deplacement, boolean coupOrdinateur, ArrayList<ModelePiece> lstPieces) {
        ModelePiece pieceBouger = null, pieceEnlever = null;

        for (ModelePiece piece : lstPieces) { //va chercher la pièce bougée
            if (piece.getX() == deplacement.getPositionInitialeX() && piece.getY() == deplacement.getPositionInitialeY()) {
                pieceBouger = piece;
            }
            if ((piece instanceof ModelePion)) {
                ((ModelePion) piece).setDeuxDernierTour(false);
            }
            if (piece.getX() == deplacement.getPositionFinaleX() && piece.getY() == deplacement.getPositionFinaleY()) {
                pieceEnlever = piece;
            }
        }

        pieceBouger.setX(deplacement.getPositionFinaleX());
        pieceBouger.setY(deplacement.getPositionFinaleY());

        if (pieceEnlever != null) { //enlève la pièce à enlever si elle existe
            lstPieces.remove(pieceEnlever);
        }
        //prise en passant
        if ((pieceBouger instanceof ModelePion) && pieceEnlever == null && (Math.abs(deplacement.getPositionFinaleX() - deplacement.getPositionInitialeX()) == 1 && Math.abs(deplacement.getPositionFinaleY() - deplacement.getPositionInitialeY()) == 1)) {
            for (ModelePiece piece : lstPieces) {
                if (piece.getX() == deplacement.getPositionFinaleX() && piece.getY() == deplacement.getPositionInitialeY()) {
                    pieceEnlever = piece;
                }
            }
            lstPieces.remove(pieceEnlever);
        }
        //rock
        if ((pieceBouger instanceof ModeleRoi) && (pieceBouger.getY() == deplacement.getPositionInitialeY()) && (Math.abs(pieceBouger.getX() - deplacement.getPositionInitialeX()) == 2)) {
            for (ModelePiece piece : lstPieces) {
                if (piece instanceof ModeleTour && piece.getCouleur() == pieceBouger.getCouleur()) {
                    if (Math.abs(piece.getX() - pieceBouger.getX()) <= 2) {
                        if (!((ModeleTour) piece).isBouge()) {
                            piece.setX(pieceBouger.getX() + (pieceBouger.getX() - piece.getX()) / Math.abs(pieceBouger.getX() - piece.getX()));
                        }
                    }
                }
            }
        }

        if ((pieceBouger instanceof ModelePion) && (pieceBouger.getY() == 0 || pieceBouger.getY() == 7)) {
            if (coupOrdinateur) {
                return 2;
            } else {
                return 3;
            }
        } else {
            return 4;
        }
    }

    /**
     * Effectue la promotion
     *
     * @param deplacement           le deplacement
     * @param coupOrdinateurVirtuel si la promotion se produit dans les calculs de l'ordinateur
     * @param lstPieces             la liste des pièces sur laquelle faire la promotion
     */
    public void promotion(Deplacement deplacement, boolean coupOrdinateurVirtuel, ArrayList<ModelePiece> lstPieces) {
        int numero = deplacement.getChoixPromotion();
        boolean couleur;
        ModelePiece pieceBouger = null, piecePromotion;

        if (lstPieces == null) {
            lstPieces = lstModelePiece;
        }

        if (numero > 10) {
            couleur = false;
            numero -= 10;
        } else {
            couleur = true;
        }

        for (ModelePiece piece : lstPieces) { //va chercher la pièce bougée
            if (piece.getCouleur() == couleur && (piece instanceof ModelePion) && (piece.getY() == 0 || piece.getY() == 7)) {
                pieceBouger = piece;
            }
        }

        lstPieces.remove(pieceBouger);

        switch (numero) {
            case 2:
                piecePromotion = new ModeleCavalier(pieceBouger.getCouleur(), true, pieceBouger.getX(), pieceBouger.getY());
                break;
            case 3:
                piecePromotion = new ModeleFou(pieceBouger.getCouleur(), true, pieceBouger.getX(), pieceBouger.getY());
                break;
            case 4:
                piecePromotion = new ModeleTour(pieceBouger.getCouleur(), true, pieceBouger.getX(), pieceBouger.getY(), true);
                break;
            case 5:
                piecePromotion = new ModeleReine(pieceBouger.getCouleur(), true, pieceBouger.getX(), pieceBouger.getY());
                break;
            default:
                piecePromotion = pieceBouger;
        }

        lstPieces.add(piecePromotion);

        if (!coupOrdinateurVirtuel) {
            setLstDeplacement(!pieceBouger.getCouleur());
            controleur.setPosition(getPosition());
        }
        deplacementAEnvoyer = deplacement;
        if (envoyerPromotion) {
            if (controleur instanceof ControleurBluetooth) {
                ((ControleurBluetooth) controleur).envoyerDeplacement(deplacementAEnvoyer);
            }
            envoyerPromotion = false;
        }
    }

    /**
     * Vérifie si un coup est autorisé
     *
     * @param pieceBouger     la pièce qui bouge dans le déplacement
     * @param pieceEnlever    la pièce qui se fait capturer dans le déplacement s'il y a lieu
     * @param positionFinaleX la position finale en x
     * @param positionFinaleY la position finale en y
     * @param liste           la liste des pieces
     * @return true si le coup est autorisé
     */
    public boolean coupAutorise(ModelePiece pieceBouger, ModelePiece pieceEnlever, int positionFinaleX, int positionFinaleY, ArrayList<ModelePiece> liste) {
        boolean coupAutorise;
        coupAutorise = pieceBouger.directionAutorise(positionFinaleX, positionFinaleY, liste, this, tmpTableauPiece); //vérifie si pièce bouge dans la bonne direction

        if (coupAutorise) {
            coupAutorise = !verifierEchec(pieceBouger, pieceEnlever, positionFinaleX, positionFinaleY, liste); //vérifie l'échec
        }

        return coupAutorise; //retourne true si un coup est autorisé
    }

    /**
     * Fait une copie d'une liste, qui sera complètement indépendante de la première
     *
     * @param liste
     * @return la liste copiée
     */
    public ArrayList<ModelePiece> copierListe(ArrayList<ModelePiece> liste) {
        ArrayList<ModelePiece> lstModelePiece2 = new ArrayList<>();

        for (ModelePiece piece : liste) {
            if (piece instanceof ModelePion) {
                lstModelePiece2.add(new ModelePion(piece.getCouleur(), piece.getX(), piece.getY(), ((ModelePion) piece).isBouge(), ((ModelePion) piece).isDeuxDernierTour()));
            } else if (piece instanceof ModeleCavalier) {
                lstModelePiece2.add(new ModeleCavalier(piece.getCouleur(), ((ModeleCavalier) piece).isPromue(), piece.getX(), piece.getY()));
            } else if (piece instanceof ModeleFou) {
                lstModelePiece2.add(new ModeleFou(piece.getCouleur(), ((ModeleFou) piece).isPromue(), piece.getX(), piece.getY()));
            } else if (piece instanceof ModeleTour) {
                lstModelePiece2.add(new ModeleTour(piece.getCouleur(), ((ModeleTour) piece).isPromue(), piece.getX(), piece.getY(), ((ModeleTour) piece).isBouge()));
            } else if (piece instanceof ModeleReine) {
                lstModelePiece2.add(new ModeleReine(piece.getCouleur(), ((ModeleReine) piece).isPromue(), piece.getX(), piece.getY()));
            } else if (piece instanceof ModeleRoi) {
                lstModelePiece2.add(new ModeleRoi(piece.getCouleur(), piece.getX(), piece.getY(), ((ModeleRoi) piece).isBouge()));
            }
        }

        return lstModelePiece2;
    }

    /**
     * Vérifie si le roi est en échec après un déplacement
     *
     * @param pieceBouger     la pièce qui bouge dans le déplacement
     * @param pieceEnlever    la pièce qui se fait capturer dans le déplacement s'il y a lieu
     * @param positionFinaleX la position finale en x
     * @param positionFinaleY la position finale en y
     * @param liste           la liste des pieces
     * @return true si le roi est en échec
     */
    public boolean verifierEchec(ModelePiece pieceBouger, ModelePiece pieceEnlever, int positionFinaleX, int positionFinaleY, ArrayList<ModelePiece> liste) {
        ArrayList<ModelePiece> lstModelePiece2;
        ModelePiece pieceBouger2;

        lstModelePiece2 = copierListe(liste);

        pieceBouger2 = lstModelePiece2.get((liste.indexOf(pieceBouger)));

        pieceBouger2.setX(positionFinaleX);
        pieceBouger2.setY(positionFinaleY);

        if (pieceEnlever != null) {
            lstModelePiece2.remove(liste.indexOf(pieceEnlever));
        }

        if ((pieceBouger2 instanceof ModelePion) && pieceEnlever == null && (Math.abs(positionFinaleX - pieceBouger.getX()) == 1 && Math.abs(positionFinaleY - pieceBouger.getY()) == 1)) {
            for (ModelePiece piece : lstModelePiece2) {
                if (piece.getX() == positionFinaleX && piece.getY() == pieceBouger.getY()) {
                    pieceEnlever = piece;
                }
            }
            lstModelePiece2.remove(pieceEnlever);
        }

        return enEchec(pieceBouger2.getCouleur(), lstModelePiece2);
    }

    /**
     * Vérifie si le roi est mis en échec par une pièce
     *
     * @param couleur                couleur du roi
     * @param lstModelePieceVerifier la liste des pièces
     * @return true si le roi est en échec
     */
    public boolean enEchec(boolean couleur, ArrayList<ModelePiece> lstModelePieceVerifier) {
        ModelePiece roi = null;
        boolean echec = false;

        for (ModelePiece piece : lstModelePieceVerifier) {
            if ((piece instanceof ModeleRoi) && (piece.getCouleur() == couleur)) {
                roi = piece;
            }
        }

        for (ModelePiece piece : lstModelePieceVerifier) {
            if (piece.getCouleur() != couleur) {
                if (piece.directionAutorise(roi.getX(), roi.getY(), lstModelePieceVerifier, this, null)) {
                    echec = true;
                }
            }
        }

        return echec;
    }

    /**
     * Calcule les coups autorisés pour une liste et une couleur
     *
     * @param couleur   couleur du trait
     * @param lstPieces liste des pièces à considérer
     * @return la liste des coups autorisés
     */
    public ArrayList<Deplacement> genererLstDeplacement(boolean couleur, ArrayList<ModelePiece> lstPieces) {
        ArrayList<Deplacement> listeDeplacement = new ArrayList<>();
        ArrayList<ModelePiece> liste = copierListe(lstPieces);

        tmpTableauPiece = new ModelePiece[8][8];

        for (ModelePiece piece : lstPieces) {
            tmpTableauPiece[piece.getY()][piece.getX()] = piece;
        }

        for (ModelePiece piece : liste) {
            if (piece.getCouleur() == couleur) {
                int positionX = piece.getX();
                int positionY = piece.getY();
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (!(positionX == j && positionY == i)) {
                            boolean forme = false;

                            if (piece instanceof ModelePion) {
                                forme = ((Math.abs(positionX - j) <= 1 && Math.abs(positionY - i) <= 1) || ((positionX - j) == 0 && Math.abs(positionY - i) == 2 && (positionY == 1 || positionY == 6)));
                            } else if (piece instanceof ModeleCavalier) {
                                forme = ((Math.abs(positionX - j) == 2 && Math.abs(positionY - i) == 1) || (Math.abs(positionX - j) == 1 && Math.abs(positionY - i) == 2));
                            } else if (piece instanceof ModeleFou) {
                                forme = (Math.abs(positionX - j) == Math.abs(positionY - i));
                            } else if (piece instanceof ModeleTour) {
                                forme = ((positionX == j && positionY != i) || (positionX != j && positionY == i));
                            } else if (piece instanceof ModeleReine) {
                                forme = ((Math.abs(positionX - j) == Math.abs(positionY - i)) || ((positionX == j && positionY != i) || (positionX != j && positionY == i)));
                            } else if (piece instanceof ModeleRoi) {
                                forme = ((Math.abs(positionX - j) <= 1 && Math.abs(positionY - i) <= 1) || (Math.abs(positionX - j) <= 2 && (positionY - i) == 0 && (positionY == 0 || positionY == 7)));
                            }

                            if (forme) {
                                ArrayList<ModelePiece> listeTester = copierListe(liste);
                                ModelePiece pieceTester = listeTester.get(liste.indexOf(piece));
                                ModelePiece pieceEnlever = null;

                                for (ModelePiece pieceChercher : listeTester) {
                                    if (pieceChercher.getX() == j && pieceChercher.getY() == i) {
                                        pieceEnlever = pieceChercher;
                                    }
                                }

                                boolean couleurPieceEnlever = true;

                                if (pieceEnlever != null) {
                                    couleurPieceEnlever = !(pieceEnlever.couleur == couleur);
                                }

                                if (!(pieceEnlever instanceof ModeleRoi) && couleurPieceEnlever) {
                                    if (coupAutorise(pieceTester, pieceEnlever, j, i, listeTester)) {
                                        if ((piece instanceof ModelePion) && ((i == 0 && couleur == true) || (i == 7 && couleur == false))) {
                                            for (int k = 2; k < 6; k++) {
                                                int nb = k;
                                                if (!couleur) {
                                                    nb += 10;
                                                }
                                                listeDeplacement.add(new Deplacement(pieceTester.getX(), pieceTester.getY(), j, i, nb));
                                            }
                                        } else {
                                            listeDeplacement.add(new Deplacement(pieceTester.getX(), pieceTester.getY(), j, i, 0));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return listeDeplacement;
    }

    /**
     * Change la liste des déplacements autorisés
     *
     * @param couleur la couleur du trait
     */
    public void setLstDeplacement(boolean couleur) {
        lstDeplacement.clear();

        lstDeplacement = genererLstDeplacement(couleur, lstModelePiece);
    }

    /**
     * Renvoie l'état du jeu
     *
     * @param couleur la couleur du trait
     * @return 0 : rien, 1 : roi blanc en echec, 2 : roi blanc en mat, 11 : roi noir en echec, 12 : roi noir en mat, 20 : pat
     */
    public int getEtat(boolean couleur) {

        //0 : rien
        //1 : roi blanc en echec
        //2 : roi blanc en mat
        //11 : roi noir en echec
        //12 : roi noir en mat
        //20 : pat

        boolean enEchec;

        enEchec = enEchec(couleur, lstModelePiece);

        if (enEchec) {
            if (lstDeplacement.size() == 0) {
                if (couleur) {
                    return 2;
                } else {
                    return 12;
                }
            } else {
                if (couleur) {
                    return 1;
                } else {
                    return 11;
                }
            }
        } else {
            if (lstDeplacement.size() == 0) {
                return 22;
            } else {
                return 0;
            }
        }
    }

    /**
     * Retourne la liste des déplacements autorisés
     *
     * @return la liste des déplacements autorises
     */
    public ArrayList<Deplacement> getLstDeplacement() {
        return lstDeplacement;
    }

    /**
     * Retourne la liste des pièces
     *
     * @return la liste des pièces
     */
    public ArrayList<ModelePiece> getLstModelePiece() {
        return lstModelePiece;
    }

    /**
     * Le trait
     *
     * @param tourDeJeu le trait
     */
    public void setTourDeJeu(boolean tourDeJeu) {
        this.tourDeJeu = tourDeJeu;
    }
}