package com.echecs.projet_integrateur.pkgVue;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.echecs.projet_integrateur.R;
import com.echecs.projet_integrateur.pkgControleur.ControleurRevision;

/**
 * Permet d'afficher des parties et de mettre des boutons permettant de changer de coups
 */
public class FenetreRevision extends LinearLayout {

    private Echiquier echiquier;
    private LinearLayout linBoutons;
    private Context context;
    private Button btnArriere, btnAvant;
    private ControleurRevision controleurRevision;

    /**
     * initialise la fenêtre révision
     *
     * @param context    permet l'affichage des composants graphiques
     * @param largeur    la largeur de l'écran
     * @param nomFichier le nom du fichier duquel lire la partie
     */
    public FenetreRevision(Context context, int largeur, String nomFichier) {
        super(context);
        this.context = context;
        echiquier = new Echiquier(context, largeur);
        initialiserFenetreRevision();
        controleurRevision = new ControleurRevision(context, echiquier, nomFichier);
    }

    /**
     * initialise les différents éléments de la fenêtre révision
     */
    private void initialiserFenetreRevision() {
        initialiserLinBoutons();
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setBackgroundColor(getResources().getColor(R.color.background));
        addView(echiquier);
        addView(linBoutons);
    }

    /**
     * initialise les layouts pour les boutons
     */
    private void initialiserLinBoutons() {
        btnArriere = new Button(context);
        btnArriere.setTextSize(40);
        btnArriere.setText("<");
        btnArriere.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                controleurRevision.bouger(false);
            }
        });
        btnAvant = new Button(context);
        btnAvant.setTextSize(40);
        btnAvant.setText(">");
        btnAvant.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                controleurRevision.bouger(true);
            }
        });
        linBoutons = new LinearLayout(context);
        linBoutons.setOrientation(HORIZONTAL);
        linBoutons.setGravity(Gravity.CENTER);
        setBackgroundColor(getResources().getColor(R.color.background));
        linBoutons.addView(btnArriere);
        linBoutons.addView(btnAvant);
    }

}
