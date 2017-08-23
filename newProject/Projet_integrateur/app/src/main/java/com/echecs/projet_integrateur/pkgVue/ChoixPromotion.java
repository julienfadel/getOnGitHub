package com.echecs.projet_integrateur.pkgVue;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echecs.projet_integrateur.R;

/**
 * Classe qui permet de créer et d'afficher un menu qui contient les pièces de promotions afin de pouvoir en sélectionner une.
 * Created by Julien on 2015-03-03.
 */


public class ChoixPromotion extends DialogFragment {
    private boolean couleur;
    private View view;
    private View reine_blanc, tour_blanc, fou_blanc, cavalier_blanc, reine_noir, tour_noir, fou_noir, cavalier_noir;
    private int numero;
    private Fenetre fenetre;

    /**
     * Change la couleur
     *
     * @param couleur true si blanc, false si noir
     */
    public void setCouleur(boolean couleur) {
        this.couleur = couleur;
    }

    /**
     * Change la fenêtre
     *
     * @param fenetre la fenêtre à changer
     */
    public void setFenetre(Fenetre fenetre) {
        this.fenetre = fenetre;
    }

    /**
     * Constructeur du dialogue
     */
    public ChoixPromotion() {

    }

    /**
     * @return le numéro de la pièce
     */
    public int getNumero() {
        return numero;
    }

    /**
     * appelé à la création du dialogue
     *
     * @param inflater           afin de pouvoir rajouter les options
     * @param container          un groupe de views
     * @param savedInstanceState nécessaire pour créer la vue
     * @return la vue
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle("Choisir une pièce");

        if (couleur) {
            view = inflater.inflate(R.layout.levelup_blanc, null);
        } else {
            view = inflater.inflate(R.layout.levelup_noir, null);

        }

        initialiserViews();
        setCancelable(false);
        return view;
    }

    /**
     * Initialiser les vues pour la promotion
     */
    private void initialiserViews() {
        reine_blanc = (View) view.findViewById(R.id.blanc_reine);
        reine_noir = (View) view.findViewById(R.id.noir_reine);
        fou_noir = (View) view.findViewById(R.id.noir_fou);
        fou_blanc = (View) view.findViewById(R.id.blanc_fou);
        cavalier_blanc = (View) view.findViewById(R.id.blanccavalier);
        cavalier_noir = (View) view.findViewById(R.id.noircavalier);
        tour_blanc = (View) view.findViewById(R.id.blanc_tour);
        tour_noir = (View) view.findViewById(R.id.noir_tour);

        if (couleur) {
            reine_blanc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ChoixPromotion.this.onClick(v);
                }
            });
            tour_blanc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChoixPromotion.this.onClick(v);
                }
            });
            fou_blanc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChoixPromotion.this.onClick(v);
                }
            });
            cavalier_blanc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChoixPromotion.this.onClick(v);
                }
            });

        } else {
            reine_noir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChoixPromotion.this.onClick(v);

                }
            });
            tour_noir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChoixPromotion.this.onClick(v);

                }
            });
            fou_noir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChoixPromotion.this.onClick(v);

                }
            });

            cavalier_noir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChoixPromotion.this.onClick(v);
                }
            });
        }

    }

    /**
     * Changer l'action pour quand on clique sur la vue en fonction de son id
     *
     * @param v la vue selectionnee
     */
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.noir_fou:
                this.numero = 13;
                fenetre.setNumero(numero);
                dismiss();
                break;
            case R.id.blanc_fou:
                this.numero = 3;
                fenetre.setNumero(numero);
                dismiss();
                break;

            case R.id.noir_reine:
                this.numero = 15;
                fenetre.setNumero(numero);
                dismiss();
                break;

            case R.id.blanc_reine:
                this.numero = 5;
                fenetre.setNumero(numero);
                dismiss();
                break;

            case R.id.noircavalier:
                this.numero = 12;
                fenetre.setNumero(numero);
                dismiss();
                break;

            case R.id.blanccavalier:
                this.numero = 2;
                fenetre.setNumero(numero);
                dismiss();
                break;

            case R.id.noir_tour:
                this.numero = 14;
                fenetre.setNumero(numero);
                dismiss();
                break;

            case R.id.blanc_tour:
                this.numero = 4;
                fenetre.setNumero(numero);
                dismiss();
                break;


        }


    }

    /**
     * Change le numéro de la pièce dans la view
     *
     * @param numero le numéro de la pièce.
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }
}
