package com.echecs.projet_integrateur.pkgActivites;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

/**
 * Activité qui présentent les différentes préférences
 */
public class Preferences extends PreferenceActivity {
    /**
     * initialise la fenêtre de préférences
     *
     * @param savedInstanceState technicalité du programme
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentsPreferences fragmentsPreferences = new FragmentsPreferences();
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragmentsPreferences).commit();
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff444444")));
        }
    }

    /**
     * permet de sortir de l'activité
     *
     * @param item l'item cliqué
     * @return true technicalité du programme
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}