package com.echecs.projet_integrateur.pkgActivites;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.echecs.projet_integrateur.R;
import com.echecs.projet_integrateur.pkgVue.Fenetre;

/**
 * Activité du jeu contenant l'échiquier peu importe les options en autant que ce ne soit pas en bluetooth
 */
public class Jeu extends ActionBarActivity {

    public Loading loading;
    private Fenetre fenetre;
    private BroadcastReceiver broadcastReceiver;
    private boolean broadcastRegistered;
    private boolean partieFinie = false;
    private Intent intent;
    private int difficulte;

    /**
     * initialise le jeu lorsqu'il est créé
     *
     * @param savedInstanceState technicalité du programme
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initialiserFenetre();
        super.onCreate(savedInstanceState);
        if (fenetre != null) {
            setContentView(fenetre);
            broadcastRegistered = false;
            fenetre.actualiserPreferences();
            initialiserLoading();
        }
    }

    /**
     * initialise la fenêtre en fonction des choix de l'utilisateur
     */
    private void initialiserFenetre() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        String action = getIntent().getAction();
        boolean joueur = true, couleurOrdinateur = true, bluetooth = false;
        int minutesBlancs = 0, minutesNoirs = 0;
        if (action.charAt(0) == '0') {
            couleurOrdinateur = true;
            if (action.charAt(1) != 'N') {
                minutesBlancs = Integer.parseInt(action.split("T")[1]);
                if (action.charAt(action.length() - 1) != 'B') {
                    minutesNoirs = Integer.parseInt(action.split("T")[2]);
                } else {
                    minutesNoirs = Integer.parseInt(action.split("T")[2].split("B")[0]);
                }
            }
            if (action.charAt(action.length() - 1) == 'B') {
                bluetooth = true;
            }
            difficulte = 0;
        } else if (action.charAt(0) == '1') {
            joueur = false;
            if (action.charAt(1) == '0') {
                couleurOrdinateur = false;
            } else if (action.charAt(1) == '1') {
                couleurOrdinateur = true;
            }
            difficulte = action.charAt(2) - '0';
        }
        if (bluetooth) {
            Intent intent = new Intent(Jeu.this, JeuBluetooth.class);
            startActivity(intent);
        } else {
            fenetre = new Fenetre(this, size.x, size.y, initialiserFragmentManager(), joueur, couleurOrdinateur, minutesBlancs, minutesNoirs, difficulte);
        }
    }

    /**
     * initialise le FragmentManager
     *
     * @return
     */
    public FragmentManager initialiserFragmentManager() {
        FragmentManager manager = getFragmentManager();
        return manager;
    }

    /**
     * crée le menu d'options
     *
     * @param menu le menu d'options
     * @return technicalité du programme
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * appelé lorsqu'une option est cliquée, permet de créer une nouvelle partie, retourner au menu ou changer les préférences
     *
     * @param item l'élément cliqué
     * @return techinicalité du programme
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle b = new Bundle();
        switch (item.getItemId()) {
            case R.id.action_preferences:
                intent = new Intent(Jeu.this, Preferences.class);
                startActivity(intent);
                return true;

            case R.id.nouvelle_partie:
                setContentView(loading);
                intent = new Intent(Jeu.this, OptionsNouvellePartie.class);
                intent.setAction("partie_en_cours_jeu");
                startActivity(intent);
                return true;

            case R.id.menu:
                setContentView(loading);
                intent = new Intent("partie_en_cours", null, Jeu.this, com.echecs.projet_integrateur.pkgActivites.Menu.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * appelé lorsque l'activité est mise en pause, permet aussi de recevoir le broadcast lui disant de se terminer au cas où une autre partie commencerait
     * arrête le timer
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (!partieFinie) {
            if (broadcastRegistered) {
                unregisterReceiver(broadcastReceiver);
            } else {
                broadcastReceiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context arg0, Intent intent) {
                        String action = intent.getAction();
                        if (action.equals("finish_activity")) {
                            unregisterReceiver(this);
                            finish();
                        }
                    }
                };
            }
            registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));
            broadcastRegistered = true;
            if (fenetre != null) {
                fenetre.setTimerEnMarche(false);
            }
        }
    }

    /**
     * appelé lorsque l'activité résume
     * repart le timer
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (fenetre != null) {
            fenetre.setTimerEnMarche(true);
            setContentView(fenetre);
            fenetre.actualiserPreferences();
        }
    }

    /**
     * appelé lorsque l'utilisateur pèse sur le bouton retour, revient au menu
     */
    @Override
    public void onBackPressed() {
        setContentView(loading);
        intent = new Intent("partie_en_cours", null, Jeu.this, com.echecs.projet_integrateur.pkgActivites.Menu.class);
        startActivity(intent);
    }

    /**
     * initialise le loading
     */
    public void initialiserLoading() {
        GifWebView view = new GifWebView(this, "file:///android_asset/loading8.gif");
        loading = new Loading(this, view);
    }

    /**
     * affiche qui a gagné
     *
     * @param etat indique qui a gagné à la méthode
     */
    public void victoire(int etat) {
        String message;
        switch (etat) {
            case Fenetre.VICTOIRE_BLANCS:
                message = "Victoire des blancs!";
                break;
            case Fenetre.VICTOIRE_NOIRS:
                message = "Victoire des noirs!";
                break;
            case Fenetre.NULLE:
                message = "Partie nulle.";
                break;
            default:
                message = "";
                break;
        }
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        partieFinie = true;
                        if (broadcastRegistered) {
                            unregisterReceiver(broadcastReceiver);
                        }
                        setContentView(loading);
                        intent = new Intent(Jeu.this, com.echecs.projet_integrateur.pkgActivites.Menu.class);
                        intent.setAction("aucune_partie");
                        startActivity(intent);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        partieFinie = true;
                        if (broadcastRegistered) {
                            unregisterReceiver(broadcastReceiver);
                        }
                        setContentView(loading);
                        intent = new Intent(Jeu.this, OptionsNouvellePartie.class);
                        intent.setAction("aucune_partie_menu");
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message)
                .setPositiveButton("Retour au menu", dialogClickListener)
                .setNegativeButton("Nouvelle partie", dialogClickListener)
                .show();
    }
}