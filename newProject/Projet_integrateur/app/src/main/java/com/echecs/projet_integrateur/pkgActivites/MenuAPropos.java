package com.echecs.projet_integrateur.pkgActivites;

import android.app.Activity;
import android.os.Bundle;

import com.echecs.projet_integrateur.R;

/**
 * Affiche des informations sur le programme
 */
public class MenuAPropos extends Activity {
    /**
     * initialise le menu à propos
     *
     * @param savedInstanceState technicalité du programme
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apropos);
    }

    /**
     * reviens au menu
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
