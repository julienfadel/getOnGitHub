package com.echecs.projet_integrateur.pkgModele;

import android.content.Context;

import com.echecs.projet_integrateur.pkgControleur.Controleur;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Porte d'accès à tout le modèle
 * Created by Simon on 2015-02-24.
 */
public class Modele {
    private Context context;
    private ModeleEchiquier modeleEchiquier;
    private Controleur controleur;

    /**
     * Initialise le modèle
     *
     * @param context    supporte la vue
     * @param controleur le controleur du jeu
     */
    public Modele(Context context, Controleur controleur) {
        this.context = context;
        modeleEchiquier = new ModeleEchiquier(context, controleur);
        this.controleur = controleur;
    }

    /**
     * Retourne le ModeleEchiquier
     *
     * @return le ModeleEchiquier
     */
    public ModeleEchiquier getModeleEchiquier() {
        return modeleEchiquier;
    }

    /**
     * Sauvegarde une partie en format binaire
     *
     * @param lstPositions la liste de la position à chaque tour dans le jeu
     * @param date         la date de la partie jouée
     */
    public void sauvegarderPartie(ArrayList<int[][]> lstPositions, int[] date) {
        Partie partie = new Partie(lstPositions, date);
        String nomFichier = "";
        for (int i = 0; i < 5; i++) {
            String dateString;
            if (date[i] < 10) {
                dateString = "0" + date[i];
            } else {
                dateString = "" + date[i];
            }
            String separateur = "";
            if (i < 2) {
                separateur = "-";
            } else if (i == 2) {
                separateur = "  ";
            } else if (i == 3) {
                separateur = ":";
            }
            nomFichier += dateString + separateur;
        }
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(nomFichier, Context.MODE_PRIVATE);
            ObjectOutputStream ecriture = new ObjectOutputStream(fos);
            ecriture.writeObject(partie);
            ecriture.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}