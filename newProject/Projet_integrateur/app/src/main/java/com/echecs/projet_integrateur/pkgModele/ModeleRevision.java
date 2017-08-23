package com.echecs.projet_integrateur.pkgModele;

import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Modèle pour la révision, lis les fichiers de partie
 */
public class ModeleRevision {

    private Context context;
    private String nomFichier;

    /**
     * initialise le modèle révision
     *
     * @param context    permet la lecture des fichiers
     * @param nomFichier nom du fichier à lire
     */
    public ModeleRevision(Context context, String nomFichier) {
        this.context = context;
        this.nomFichier = nomFichier;
    }

    /**
     * initialise la liste des positions
     *
     * @return la liste des positions
     * @throws NullPointerException
     */
    public ArrayList<int[][]> initialiserLstPositions() throws NullPointerException {
        Partie partie = null;
        ObjectInputStream lecture = null;
        try {
            FileInputStream fis = context.openFileInput(nomFichier);
            lecture = new ObjectInputStream(fis);
            Object contenu;
            contenu = lecture.readObject();
            partie = (Partie) contenu;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                lecture.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return partie.getLstPositions();
    }
}
