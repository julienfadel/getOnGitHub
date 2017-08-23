package com.echecs.projet_integrateur.pkgActivites;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.echecs.projet_integrateur.R;

/**
 * Contient les différentes préférences
 */
public class FragmentsPreferences extends PreferenceFragment {

    private ListPreference prefTournerJeu;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    /**
     * initialise les préférences et leurs listeners
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        prefTournerJeu = (ListPreference) findPreference("pref_tournerJeu");
        prefTournerJeu.setSummary(prefTournerJeu.getEntry());
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("pref_tournerJeu")) {
                    prefTournerJeu.setSummary(prefTournerJeu.getEntry());
                }
            }
        };
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(prefListener);
    }

    /**
     * lorsque résumé, enregistre un listener qui regarde si une préférence est changée
     */
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(prefListener);
    }

    /**
     * lorsque pausé, désenregistre un listener qui regarde si une préférence est changée
     */
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(prefListener);
    }
}
