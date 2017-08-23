package com.echecs.projet_integrateur.pkgControleur;

import android.content.Context;

import com.echecs.projet_integrateur.pkgModele.ModeleRevision;
import com.echecs.projet_integrateur.pkgVue.Echiquier;

import java.util.ArrayList;

/**
 * Permet de faire la liaison entre la FenetreRevision et le ModeleRevision sans utiliser toutes les méthodes du controleur habituel
 */
public class ControleurRevision {

    private ModeleRevision modeleRevision;
    private Echiquier echiquier;
    private ArrayList<int[][]> lstPositions = new ArrayList<>();
    private int indice;

    /**
     * initialise le controleur révision
     *
     * @param context    permet de lire les fichiers dans le ModeleRevision
     * @param echiquier  l'échiquier à afficher
     * @param nomFichier le nom du fichier contenant la partie à réviser
     */
    public ControleurRevision(Context context, Echiquier echiquier, String nomFichier) {
        modeleRevision = new ModeleRevision(context, nomFichier);
        this.echiquier = echiquier;
        try {
            lstPositions = modeleRevision.initialiserLstPositions();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        indice = 0;
        echiquier.convertirPosition(lstPositions.get(indice));
    }

    /**
     * change la position de l'échiquier en y envoyant une nouvelle position sous forme de tableau d'entiers
     *
     * @param sens si l'utilisateur avance ou recule dans les coups
     */
    public void bouger(boolean sens) {
        if (sens) {
            if (indice < lstPositions.size() - 1) {
                indice += 1;
            }
        } else {
            if (indice > 0) {
                indice -= 1;
            }
        }
        echiquier.convertirPosition(lstPositions.get(indice));
    }

}
