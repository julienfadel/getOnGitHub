package com.echecs.projet_integrateur.pkgControleur;

import android.content.Context;

import com.echecs.projet_integrateur.pkgActivites.JeuBluetooth;
import com.echecs.projet_integrateur.pkgModele.Modele;
import com.echecs.projet_integrateur.pkgVue.Fenetre;

import java.util.ArrayList;

/**
 * Permet de faire le relai entre la fenêtre et le modèle pour les parties avec Bluetooth
 * Hérite des méthodes du Controleur, mais en modifie aussi certaines
 */
public class ControleurBluetooth extends Controleur {

    private boolean couleurBluetooth;
    private JeuBluetooth.ConnectedThread connectedThread;

    /**
     * initialise le controleur bluetooth
     *
     * @param context          nécessaire à la lecture du fichier de la position initial dans le modèle
     * @param fenetre          la fenetre contenant l'échiquier
     * @param couleurBluetooth la couleur de l'utilisateur
     * @param connectedThread  le thread de la connexion, permet d'envoyer des données
     */
    public ControleurBluetooth(Context context, Fenetre fenetre, boolean couleurBluetooth, JeuBluetooth.ConnectedThread connectedThread) {
        this.context = context;
        this.fenetre = fenetre;
        this.couleurBluetooth = couleurBluetooth;
        this.connectedThread = connectedThread;
        lstPositions = new ArrayList<>();
        fenetre.getEchiquier().setControleur(this);
        modele = new Modele(context, this);
        fenetre.getEchiquier().setPosition(modele.getModeleEchiquier().getPosition());
    }

    /**
     * permet d'envoyer un déplacement à l'autre téléphone
     *
     * @param deplacement le déplacement à envoyer
     */
    public void envoyerDeplacement(Deplacement deplacement) {
        connectedThread.write(deplacement);
    }

    /**
     * reçoit un déplacement de l'autre téléphone, permet de l'effectuer sans vérifier sa validité
     *
     * @param deplacement le déplacement envoyé
     */
    public void recevoirDeplacement(Deplacement deplacement) {
        modele.getModeleEchiquier().changerPosition(deplacement, true, false);
    }

    /**
     * permet de vérifier le mouvement effectué
     *
     * @param positionInitialeX position initiale de la pièce en X
     * @param positionInitialeY position initiale de la pièce en Y
     * @param positionFinaleX   position finale de la pièce en X
     * @param positionFinaleY   position finale de la pièce en Y
     */
    @Override
    public void verifierMouvement(int positionInitialeX, int positionInitialeY, int positionFinaleX, int positionFinaleY) {
        this.positionFinaleX = positionFinaleX;
        this.positionFinaleY = positionFinaleY;
        this.positionInitialeX = positionInitialeX;
        this.positionInitialeY = positionInitialeY;
        modele.getModeleEchiquier().changerPosition(new Deplacement(positionInitialeX, positionInitialeY, positionFinaleX, positionFinaleY, 0), false, true);
    }

    /**
     * évite de changer les préférences qui ne sont pas utilisés dans le jeu avec bluetooth
     */
    @Override
    public void changerPreferences() {

    }
}
