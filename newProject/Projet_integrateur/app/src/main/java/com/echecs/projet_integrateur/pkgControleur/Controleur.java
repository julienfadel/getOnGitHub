package com.echecs.projet_integrateur.pkgControleur;

/**
 * Permet la liaison entre la fenêtre et l'échiquier et le modèle
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.echecs.projet_integrateur.pkgModele.Modele;
import com.echecs.projet_integrateur.pkgModele.ModelePiece;
import com.echecs.projet_integrateur.pkgVue.Fenetre;

import java.util.ArrayList;
import java.util.Calendar;

public class Controleur {
    protected Modele modele;
    protected Fenetre fenetre;
    protected Context context;
    private int tempsBlancs = 0, tempsNoirs = 0;
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean timerEnMarche;
    private boolean premiereConfiguration = true;
    private boolean joueur;
    private boolean couleurOrdinateur;
    protected int positionFinaleX;
    protected int positionFinaleY;
    protected int positionInitialeX;
    protected int positionInitialeY;
    private int positionFinaleXTemporaire;
    private int positionFinaleYTemporaire;
    protected ArrayList<int[][]> lstPositions;
    protected Deplacement deplacementPromotion;

    /**
     * initialise le controleur
     *
     * @param context           permet la lecture de fichiers dans le modèle
     * @param fenetre           la fenetre contenant les éléments graphiques
     * @param joueur            est faux si l'ordinateur joue
     * @param couleurOrdinateur la couleur de l'ordinateur
     * @param difficulte        la difficulté de l'ordinateur (son niveau de complexité dans l'exploration des coups)
     */
    public Controleur(Context context, Fenetre fenetre, boolean joueur, boolean couleurOrdinateur, int difficulte) {
        this.context = context;
        this.fenetre = fenetre;
        this.joueur = joueur;
        lstPositions = new ArrayList<>();
        fenetre.getEchiquier().setControleur(this);
        modele = new Modele(context, this);
        fenetre.getEchiquier().setPosition(modele.getModeleEchiquier().getPosition());
        if (joueur) {
            initialiserRunnable();
            handler.post(runnable);
        } else {
            this.couleurOrdinateur = couleurOrdinateur;
            modele.getModeleEchiquier().initialiserParametresOrdinateur(couleurOrdinateur, difficulte);
        }
    }

    /**
     * permet le controleur bluetooth sans avoir à y passer tous les paramètres qui lui sont inutils présents dans le constructeur habituel
     */
    public Controleur() {

    }

    /**
     * permet de changer les préférences et les actualise dans la fenêtre
     */
    public void changerPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int preferenceTourner = 0;
        if (joueur) {
            preferenceTourner = Integer.parseInt(preferences.getString("pref_tournerJeu", "0"));
        }
        boolean dernierCoup, coupsPossibles;
        dernierCoup = preferences.getBoolean("pref_dernierCoup", true);
        coupsPossibles = preferences.getBoolean("pref_afficherCoupsPossibles", false);
        fenetre.changerPreferences(preferenceTourner, dernierCoup, coupsPossibles);
        premiereConfiguration = false;
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
     * initialise les runnables qui permettent les timers
     */
    private void initialiserRunnable() {
        runnable = new Runnable() {
            public void run() {
                if (timerEnMarche) {
                    if (fenetre.getEchiquier().getTourDeJeu()) {
                        tempsBlancs += 1;
                        if (tempsBlancs % 10 == 0) {
                            fenetre.changerTimer(true);
                        }
                    } else {
                        tempsNoirs += 1;
                        if (tempsNoirs % 10 == 0) {
                            fenetre.changerTimer(false);
                        }
                    }
                }
                handler.postDelayed(runnable, 100);
            }
        };
    }

    /**
     * envoie au modèle le mouvement qui a été effectué
     *
     * @param positionInitialeX position initiale de la pièce en X
     * @param positionInitialeY position initiale de la pièce en Y
     * @param positionFinaleX   position finale de la pièce en X
     * @param positionFinaleY   position finale de la pièce en Y
     */
    public void verifierMouvement(int positionInitialeX, int positionInitialeY, int positionFinaleX, int positionFinaleY) {
        this.positionFinaleX = positionFinaleX;
        this.positionFinaleY = positionFinaleY;
        this.positionInitialeX = positionInitialeX;
        this.positionInitialeY = positionInitialeY;
        modele.getModeleEchiquier().changerPosition(new Deplacement(positionInitialeX, positionInitialeY, positionFinaleX, positionFinaleY, 0), false, false);
    }

    /**
     * permet de vérifier si un coup serait potentiellement autorisé et donc d'afficher les coups autorisés dans l'échiquier
     *
     * @param x                         position initiale en X
     * @param y                         position initiale en Y
     * @param positionFinaleXTemporaire position finale en X temporaire
     * @param positionFinaleYTemporaire position finale en Y temporaire
     * @return si le coup est autorisé
     */
    public boolean verifierCoupAutorise(int x, int y, int positionFinaleXTemporaire, int positionFinaleYTemporaire) {
        ModelePiece pieceEnlever = null;
        ModelePiece pieceBouger = null;
        ArrayList<ModelePiece> lstModelePiece = modele.getModeleEchiquier().getLstModelePiece();
        this.positionFinaleXTemporaire = positionFinaleXTemporaire;
        this.positionFinaleYTemporaire = positionFinaleYTemporaire;

        for (int i = 0; i < lstModelePiece.size(); i++) {
            if (lstModelePiece.get(i).getX() == x && lstModelePiece.get(i).getY() == y) {
                pieceBouger = lstModelePiece.get(i);
                for (int w = 0; w < lstModelePiece.size(); w++) {
                    if (lstModelePiece.get(w).getX() == positionFinaleXTemporaire && lstModelePiece.get(w).getY() == positionFinaleYTemporaire) {
                        pieceEnlever = lstModelePiece.get(w);
                        positionFinaleXTemporaire = pieceEnlever.getX();
                        positionFinaleYTemporaire = pieceEnlever.getY();
                    }
                }
                return modele.getModeleEchiquier().coupAutorise(pieceBouger, pieceEnlever, positionFinaleXTemporaire, positionFinaleYTemporaire, lstModelePiece);
            }
        }
        return false;
    }

    /**
     * change la position dans l'échiquier
     *
     * @param tableau la position à adopter
     */
    public void setPosition(int[][] tableau) {
        fenetre.getEchiquier().setPosition(tableau); //change la position dans la Fenetre si elle est changée dans le modèle
        fenetre.message();
    }

    /**
     * sauvegarde une position dans le but de la sauvegarder dans une partie plus tard
     *
     * @param tableau la position à sauvegarder
     */
    public void sauvegarderPosition(int[][] tableau) {
        lstPositions.add(tableau);
    }

    /**
     * sauvegarde une partie dans le but de la mettre dans un fichier binaire et va chercher la date et l'heure pour le titre du fichier
     */
    public void sauvegarderPartie() {
        Calendar c = Calendar.getInstance();
        int[] date = new int[5];
        date[0] = c.get(Calendar.YEAR);
        date[1] = c.get(Calendar.MONTH) + 1;
        date[2] = c.get(Calendar.DAY_OF_MONTH);
        date[3] = c.get(Calendar.HOUR_OF_DAY);
        date[4] = c.get(Calendar.MINUTE);
        modele.sauvegarderPartie(lstPositions, date);
    }

    /**
     * retourne l'état du jeu
     *
     * @param couleur le tour
     * @return s'il y a un échec, une nulle ou un mat
     */
    public int getEtat(boolean couleur) {
        return modele.getModeleEchiquier().getEtat(couleur);
    }

    /**
     * permet d'afficher les choix de promotions
     *
     * @param deplacementPromotion le déplacement effectué
     * @param couleur              la couleur du pion à promouvoir
     */
    public void getPromotion(Deplacement deplacementPromotion, boolean couleur) {
        this.deplacementPromotion = deplacementPromotion;
        fenetre.showPromotion(couleur);
    }

    /**
     * fait jouer l'ordinateur
     */
    public void jouerOrdinateur() {
        modele.getModeleEchiquier().calculerCoupOrdinateur();
    }

    /**
     * change si le timer est en marche
     *
     * @param timerEnMarche vrai si le timer est en marche
     */
    public void setTimerEnMarche(boolean timerEnMarche) {
        this.timerEnMarche = timerEnMarche;
    }

    /**
     * envoie le numéro de la promotion de la fenêtre vers le modèle
     *
     * @param numero
     */
    public void sendPromotion(int numero) {
        deplacementPromotion.setChoixPromotion(numero);
        modele.getModeleEchiquier().promotion(deplacementPromotion, false, null);
    }

    /**
     * @return la liste de déplacements possibles du modèle échiquier
     */
    public ArrayList<Deplacement> getLstDeplacement() {
        return modele.getModeleEchiquier().getLstDeplacement();
    }

    /**
     * change la positionFinaleX
     *
     * @param positionFinaleX la valeur à donner
     */
    public void setPositionFinaleX(int positionFinaleX) {
        this.positionFinaleX = positionFinaleX;
    }

    /**
     * change la positionFinaleY
     *
     * @param positionFinaleY la valeur à donner
     */
    public void setPositionFinaleY(int positionFinaleY) {
        this.positionFinaleY = positionFinaleY;
    }

    /**
     * change la positionInitialeX
     *
     * @param positionInitialeX la valeur à donner
     */
    public void setPositionInitialeX(int positionInitialeX) {
        this.positionInitialeX = positionInitialeX;
    }

    /**
     * change la positionInitialeY
     *
     * @param positionInitialeY la valeur à donner
     */
    public void setPositionInitialeY(int positionInitialeY) {
        this.positionInitialeY = positionInitialeY;
    }

    /**
     * @return la positionInitialeX
     */
    public int getPositionInitialeX() {
        return positionInitialeX;
    }

    /**
     * @return la positionInitialeY
     */
    public int getPositionInitialeY() {
        return positionInitialeY;
    }

    /**
     * change le tour de jeu
     *
     * @param tourDeJeu le tour de jeu à donner au modèle
     */
    public void setTourDeJeu(boolean tourDeJeu) {
        modele.getModeleEchiquier().setTourDeJeu(tourDeJeu);
    }
}