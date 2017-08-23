package com.echecs.projet_integrateur.pkgActivites;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.echecs.projet_integrateur.R;

import java.util.Random;

/**
 * Activité qui présente les différentes options au moment de créer une nouvelle partie
 */
public class OptionsNouvellePartie extends Activity {

    private LinearLayout layoutHumains, layoutOrdinateur;
    private EditText editTextTempsBlancs, editTextTempsNoirs;
    private Button buttonCommencerPartie;
    private RadioButton radioButtonHumains, radioButtonOrdinateur, radioButtonOrdiBlancs, radioButtonOrdiNoirs, radioButtonOrdiAleatoire;
    private CheckBox checkBoxTemps, checkBoxBluetooth;
    private SeekBar seekBar;
    private TextView txtDifficulte;

    /**
     * crée le layout de l'activité d'options pour une nouvelle partie
     *
     * @param savedInstanceState technicalité du programme
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_nouvelle_partie);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff2c2c2c")));
        }
        layoutHumains = (LinearLayout) findViewById(R.id.linearLayout_humains);
        layoutOrdinateur = (LinearLayout) findViewById(R.id.linearLayout_ordinateur);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size.x, (int) (size.y * 0.45));
        layoutHumains.setLayoutParams(layoutParams);
        layoutOrdinateur.setLayoutParams(layoutParams);

        editTextTempsBlancs = (EditText) findViewById(R.id.editText_tempsBlancs);
        editTextTempsNoirs = (EditText) findViewById(R.id.editText_tempsNoirs);

        buttonCommencerPartie = (Button) findViewById(R.id.button_commencerPartie);

        radioButtonHumains = (RadioButton) findViewById(R.id.radioButton_humains);
        radioButtonOrdinateur = (RadioButton) findViewById(R.id.radioButton_ordinateur);
        radioButtonOrdiBlancs = (RadioButton) findViewById(R.id.radioButton_ordi_blancs);
        radioButtonOrdiNoirs = (RadioButton) findViewById(R.id.radioButton_ordi_noirs);
        radioButtonOrdiAleatoire = (RadioButton) findViewById(R.id.radioButton_ordi_aleatoire);

        checkBoxTemps = (CheckBox) findViewById(R.id.checkBox_temps);
        checkBoxBluetooth = (CheckBox) findViewById(R.id.checkBox_bluetooth);

        txtDifficulte = (TextView) findViewById(R.id.textView_ordi_difficulte);

        seekBar = (SeekBar) findViewById(R.id.seekBar_ordi_difficulte);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtDifficulte.setText("Difficulté : " + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        buttonCommencerPartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action = "";
                if (radioButtonHumains.isChecked()) {
                    action += "0";
                    if (checkBoxTemps.isChecked()) {
                        if (editTextTempsBlancs.getText().length() != 0) {
                            action += "T" + editTextTempsBlancs.getText();
                        } else {
                            action += "T0";
                        }
                        if (editTextTempsNoirs.getText().length() != 0) {
                            action += "T" + editTextTempsNoirs.getText();
                        } else {
                            action += "T0";
                        }
                    } else {
                        action += "N";
                    }
                    if (checkBoxBluetooth.isChecked()) {
                        action += "B";
                    }
                } else if (radioButtonOrdinateur.isChecked()) {
                    action += "1";
                    if (radioButtonOrdiBlancs.isChecked()) {
                        action += "0";
                    } else if (radioButtonOrdiNoirs.isChecked()) {
                        action += "1";
                    } else if (radioButtonOrdiAleatoire.isChecked()) {
                        Random aleatoire = new Random();
                        action += aleatoire.nextInt(2);
                    }
                    action += seekBar.getProgress();
                }
                Intent intent = new Intent("finish_activity");
                sendBroadcast(intent);
                intent = new Intent(OptionsNouvellePartie.this, Jeu.class);
                intent.setAction(action);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Appelé quand l'utilisateur utilise la flèche en haut à droite, retourne à la bonne activité
     *
     * @param item l'item qui est sélectionné par l'utilisateur
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (getIntent().getAction()) {
            case "partie_en_cours_menu":
                intent = new Intent(OptionsNouvellePartie.this, Menu.class);
                intent.setAction("partie_en_cours");
                startActivity(intent);
                break;
            case "partie_en_cours_jeu":
                break;
            case "android.intent.action.MAIN_menu":
                intent = new Intent(OptionsNouvellePartie.this, Menu.class);
                intent.setAction("aucune_partie");
                startActivity(intent);
                break;
        }
        finish();
        return true;
    }

    /**
     * permet d'afficher le bon layout en fonction du RadioButton choisi (ordinateur/humain)
     *
     * @param view le RadioButton cliqué
     */
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radioButton_humains:
                if (checked) {
                    layoutOrdinateur.setVisibility(View.GONE);
                    layoutHumains.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.radioButton_ordinateur:
                if (checked) {
                    layoutHumains.setVisibility(View.GONE);
                    layoutOrdinateur.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    /**
     * Active la modification des EditText en fonction de l'état du CheckBox "Temps"
     *
     * @param view le CheckBox cliqué
     */
    public void onCheckBoxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkBox_temps:
                if (checked) {
                    editTextTempsBlancs.setEnabled(true);
                    editTextTempsNoirs.setEnabled(true);
                } else {
                    editTextTempsBlancs.setEnabled(false);
                    editTextTempsNoirs.setEnabled(false);
                }
                break;
        }
    }

    /**
     * appelé lorsque l'utilisateur clique sur le bouton retour
     */
    @Override
    public void onBackPressed() {
        Intent intent;
        switch (getIntent().getAction()) {
            case "partie_en_cours_menu":
                intent = new Intent(OptionsNouvellePartie.this, Menu.class);
                intent.setAction("partie_en_cours");
                startActivity(intent);
                break;
            case "partie_en_cours_jeu":
                break;
            case "android.intent.action.MAIN_menu":
                intent = new Intent(OptionsNouvellePartie.this, Menu.class);
                intent.setAction("aucune_partie");
                startActivity(intent);
                break;
            case "aucune_partie_menu":
                intent = new Intent(OptionsNouvellePartie.this, Menu.class);
                intent.setAction("aucune_partie");
                startActivity(intent);
        }
        finish();
    }
}
