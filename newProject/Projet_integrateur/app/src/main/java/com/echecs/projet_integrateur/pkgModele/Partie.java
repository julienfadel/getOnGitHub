package com.echecs.projet_integrateur.pkgModele;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Contient toutes les positions d'une partie d'échecs et permet d'être stockée dans un fichier binaire grâce à l'implémentation de la sérialisation
 */
public class Partie implements Serializable {

    private ArrayList<int[][]> lstPositions = new ArrayList<>();
    private String nom;
    private int[] date = new int[5];

    /**
     * initialise une partie
     *
     * @param lstPositions la liste de toutes les positions
     * @param date         la date
     */
    public Partie(ArrayList<int[][]> lstPositions, int[] date) {
        this.lstPositions = lstPositions;
        this.date = date;
    }

    /**
     * @return la liste de toutes les positions
     */
    public ArrayList<int[][]> getLstPositions() {
        return lstPositions;
    }
}
