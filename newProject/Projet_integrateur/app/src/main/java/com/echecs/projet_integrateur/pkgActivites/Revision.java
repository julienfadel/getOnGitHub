package com.echecs.projet_integrateur.pkgActivites;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;

import com.echecs.projet_integrateur.pkgVue.FenetreRevision;

/**
 * L'activité permettant d'afficher la révision de parties
 */
public class Revision extends Activity {

    private FenetreRevision fenetreRevision;

    /**
     * initialise la fenêtre révision
     *
     * @param savedInstanceState technicalité du programme
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initialiserFenetreRevision();
        super.onCreate(savedInstanceState);
        setContentView(fenetreRevision);
    }

    /**
     * initialiser la fenetre révision
     */
    private void initialiserFenetreRevision() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        fenetreRevision = new FenetreRevision(this, size.x, getIntent().getAction());
    }
}
