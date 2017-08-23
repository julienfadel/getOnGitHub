package com.echecs.projet_integrateur.pkgActivites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.echecs.projet_integrateur.R;

/**
 * Menu principal de l'application
 */
public class Menu extends Activity {

    private Loading loading;
    private Button buttonNouvellePartie;
    private Button buttonRevoirPartie;
    private Button buttonContinuerPartie;
    private Button buttonQuitter;
    private Button buttonPreferences;
    private Button buttonApropos;

    /**
     * crée le menu, affiche le bouton "Continuer partie" si une partie est en cours
     * initialise les listeners des différents boutons pour gérer la liaison d'une activité à l'autre
     *
     * @param savedInstanceState technicalité de l'application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initialiserLoading();
        buttonNouvellePartie = (Button) findViewById(R.id.buttonNouvellePartie);
        buttonContinuerPartie = (Button) findViewById(R.id.buttonContinuerPartie);
        buttonRevoirPartie = (Button) findViewById(R.id.buttonRevoirPartie);
        buttonPreferences = (Button) findViewById(R.id.buttonPreferences);
        buttonQuitter = (Button) findViewById(R.id.buttonQuitter);
        buttonApropos = (Button) findViewById(R.id.button_Apropos);
        if (getIntent().getAction() == "partie_en_cours") {
            buttonContinuerPartie.setVisibility(View.VISIBLE);
        }
        buttonNouvellePartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(loading);
                Intent intent = new Intent(Menu.this, OptionsNouvellePartie.class);
                intent.setAction(getIntent().getAction() + "_menu");
                startActivity(intent);
                finish();
            }
        });
        buttonContinuerPartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(loading);
                finish();
            }
        });
        buttonRevoirPartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, ListFichiers.class);
                startActivity(intent);
            }
        });
        buttonPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Preferences.class);
                startActivity(intent);
            }
        });
        buttonApropos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, MenuAPropos.class);
                startActivity(intent);
            }
        });
        buttonQuitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("finish_activity");
                sendBroadcast(intent);
                finish();
            }
        });
    }

    /**
     * permet de revenir à l'interface utilisateur du téléphone lorsque l'utilisateur clique sur le bouton retour
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    /**
     * initialise le loading
     */
    public void initialiserLoading() {
        GifWebView view = new GifWebView(this, "file:///android_asset/loading8.gif");
        loading = new Loading(this, view);
    }
}
