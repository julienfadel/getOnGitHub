package com.echecs.projet_integrateur.pkgVue;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.echecs.projet_integrateur.R;
import com.echecs.projet_integrateur.pkgActivites.Jeu;
import com.echecs.projet_integrateur.pkgActivites.JeuBluetooth;
import com.echecs.projet_integrateur.pkgControleur.Controleur;
import com.echecs.projet_integrateur.pkgControleur.ControleurBluetooth;

import java.util.ArrayList;

/**
 * Contient l'échiquier ainsi que les layouts des pièces mangées et le timer
 */
public class Fenetre extends LinearLayout {

    public static final int VICTOIRE_BLANCS = 12;
    public static final int VICTOIRE_NOIRS = 2;
    public static final int NULLE = 22;

    private int hauteurEcran;
    private int largeurEcran;
    private Echiquier echiquier;
    private Controleur controleur;
    private Context context;
    private LinearLayout linNoirs, linBlancs;
    private ImageView boutonNoirs, boutonBlancs;
    private GridLayout gridMangeNoirs, gridMangeBlancs;
    private ArrayList<Piece> lstPiecesMangees;
    private TextView txtTimerBlancs, txtTimerNoirs;
    private FragmentManager manager;
    private int numero;
    private int minutesBlancs = 10, secondesBlancs = 0, minutesNoirs = 10, secondesNoirs = 00;
    private ChoixPromotion dialogNoir;
    private ChoixPromotion dialogBlanc;
    private ChoixPromotion dialog;
    private boolean joueur;
    private boolean couleurOrdinateur;
    private boolean premierCoup;
    private boolean couleurBluetooth;
    private boolean bluetooth;
    private boolean bughouse;
    private boolean bughouseSelectionne;
    private ButtonBughouse buttonBughouseSelectionne;
    private ArrayList<ButtonBughouse> lstBtnBlancs, lstBtnNoirs;
    private boolean timerBlancs = true, timerNoirs = true;

    /**
     * initialise les différents éléments de la fenêtre (échiquier, layouts de pièces mangées, timers)
     *
     * @param context           permet l'affichage des éléments graphiques
     * @param largeurEcran      la largeur de l'écran
     * @param hauteurEcran      la hauteur de l'écran
     * @param manager           le FragmentManager de l'activité
     * @param joueur            faux si l'utilisateur joue contre l'ordinateur
     * @param couleurOrdinateur la couleur de l'ordinateur
     * @param minutesBlancs     le nombre de minutes allouées aux blancs
     * @param minutesNoirs      le nombre de minutes allouées aux noirs
     * @param difficulte        la difficulté de l'ordinateur
     */
    public Fenetre(Context context, int largeurEcran, int hauteurEcran, FragmentManager manager, boolean joueur, boolean couleurOrdinateur, int minutesBlancs, int minutesNoirs, int difficulte) {
        super(context);
        this.manager = manager;
        this.context = context;
        this.largeurEcran = largeurEcran;
        this.hauteurEcran = hauteurEcran;
        this.joueur = joueur;
        this.minutesBlancs = minutesBlancs;
        this.minutesNoirs = minutesNoirs;
        bluetooth = false;
        premierCoup = true;
        if (!joueur) {
            this.couleurOrdinateur = couleurOrdinateur;
        }
        echiquier = new Echiquier(context, largeurEcran, this, this.joueur, this.couleurOrdinateur);
        initialiserFenetre(false);
        choixPromotion();
        controleur = new Controleur(context, this, this.joueur, this.couleurOrdinateur, difficulte);
        controleur.setTourDeJeu(echiquier.getTourDeJeu());
        if (!joueur) {
            if (couleurOrdinateur) {
                controleur.jouerOrdinateur();
            }
        }
    }

    /**
     * initialise les différents éléments de la fenêtre lorsqu'il y a connexion bluetooth
     *
     * @param context          permet l'affichage des éléments graphiques
     * @param largeurEcran     la largeur de l'écran
     * @param hauteurEcran     la hauteur de l'écran
     * @param manager          le FragmentManager de l'activité
     * @param couleurBluetooth la couleur de l'utilisateur
     * @param connectedThread  le thread de connexion permettant la communication
     * @param bughouse         vrai si le mode bughouse est activé
     */
    public Fenetre(Context context, int largeurEcran, int hauteurEcran, FragmentManager manager, boolean couleurBluetooth, JeuBluetooth.ConnectedThread connectedThread, boolean bughouse) {
        super(context);
        this.manager = manager;
        this.context = context;
        this.largeurEcran = largeurEcran;
        this.hauteurEcran = hauteurEcran;
        this.couleurBluetooth = couleurBluetooth;
        bluetooth = true;
        if (!bughouse) {
            echiquier = new Echiquier(context, largeurEcran, this, couleurBluetooth);
            initialiserFenetre(true);
            choixPromotion();
            controleur = new ControleurBluetooth(context, this, couleurBluetooth, connectedThread);
            controleur.setTourDeJeu(echiquier.getTourDeJeu());
        } else {
            initialiserFenetreBughouse();
        }
    }

    /**
     * initialise les éléments de la fenêtre s'il y a bughouse
     */
    private void initialiserFenetreBughouse() {
        bughouseSelectionne = false;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setBackgroundColor(getResources().getColor(R.color.background));
        echiquier = new Echiquier(context, largeurEcran);
        lstBtnBlancs = new ArrayList<>();
        lstBtnNoirs = new ArrayList<>();
        bughouseSelectionne = false;
        GridLayout layoutBoutonsBlancs = initialiserBoutonsBughouse(true);
        GridLayout layoutBoutonsNoirs = initialiserBoutonsBughouse(false);
        layoutBoutonsNoirs.setRotation(180);
        echiquier.setPadding(0, largeurEcran / 16, 0, largeurEcran / 16);
        addView(layoutBoutonsNoirs);
        addView(echiquier);
        addView(layoutBoutonsBlancs);
    }

    /**
     * initialise les différents boutons du mode bughouse
     *
     * @param couleur la couleur des pièces des boutons
     * @return le GridLayout contenant tous les boutons
     */
    private GridLayout initialiserBoutonsBughouse(boolean couleur) {
        GridLayout grid = new GridLayout(context);
        for (int i = 0; i < 5; i++) {
            GridLayout.LayoutParams par = new GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(i));
            int cote = largeurEcran / 5;
            par.width = cote;
            par.height = cote;
            Piece piece = null;
            switch (i) {
                case 0:
                    piece = new Pion(context, couleur, cote);
                    break;
                case 1:
                    piece = new Cavalier(context, couleur, cote);
                    break;
                case 2:
                    piece = new Fou(context, couleur, cote);
                    break;
                case 3:
                    piece = new Tour(context, couleur, cote);
                    break;
                case 4:
                    piece = new Reine(context, couleur, cote);
                    break;
            }
            ButtonBughouse buttonBughouse = new ButtonBughouse(context, piece, 0);
            if (couleur) {
                lstBtnBlancs.add(buttonBughouse);
            } else {
                lstBtnNoirs.add(buttonBughouse);
            }
            grid.addView(buttonBughouse, par);
            buttonBughouse.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    echiquier.deselectionner();
                    if (!bughouseSelectionne) {
                        buttonBughouseSelectionne = (ButtonBughouse) v;
                        buttonBughouseSelectionne.setSelected(true);
                        bughouseSelectionne = true;
                    } else {
                        buttonBughouseSelectionne.setSelected(false);
                        if (((ButtonBughouse) v).isSelected()) {
                            ((ButtonBughouse) v).setSelected(false);
                            buttonBughouseSelectionne = null;
                            bughouseSelectionne = false;
                        } else {
                            buttonBughouseSelectionne = (ButtonBughouse) v;
                            buttonBughouseSelectionne.setSelected(true);
                        }
                    }
                }
            });
        }
        return grid;
    }

    /**
     * déselectionne tous les boutons bughouse
     */
    public void deselectionnerBughouse() {
        bughouseSelectionne = false;
        buttonBughouseSelectionne.setPressed(false);
        buttonBughouseSelectionne = null;
    }

    /**
     * active les boutons bughouse pour une certaine couleur
     *
     * @param couleur la couleur des pièces des boutons à activer
     */
    public void enableButtonsBughouse(boolean couleur) {
        for (int i = 0; i < 5; i++) {
            lstBtnBlancs.get(i).setEnabled(couleur);
            lstBtnNoirs.get(i).setEnabled(!couleur);
        }
    }

    /**
     * initialise les dialogues de choix pour la promotion
     */
    public void choixPromotion() {
        dialogBlanc = new ChoixPromotion();
        dialogBlanc.setFenetre(Fenetre.this);
        dialogBlanc.setCouleur(true);

        dialogNoir = new ChoixPromotion();
        dialogNoir.setFenetre(Fenetre.this);
        dialogNoir.setCouleur(false);
    }

    /**
     * affiche les choix pour la promotion
     *
     * @param couleur la couleur de la pièce promue
     */
    public void showPromotion(boolean couleur) {
        numero = 0;
        if (couleur) {
            dialog = dialogBlanc;
        } else {
            dialog = dialogNoir;
        }
        dialog.setNumero(0);
        dialog.show(Fenetre.this.manager, "dialog");
    }

    /**
     * change le numéro de la promotion dans le controleur
     *
     * @param numero le numéro à changer
     */
    public void setNumero(int numero) {
        controleur.sendPromotion(numero);
    }

    /**
     * initiliase la fenêtre en fonction de s'il y a une connexion bluetooth ou non
     *
     * @param bluetooth indique s'il y a une connexion bluetooth
     */
    private void initialiserFenetre(boolean bluetooth) {
        initialiserGrids();
        if (!bluetooth) {
            intiailiserTxtTimers();
        }
        initialiserLinNoirs();
        initialiserLinBlancs();
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setBackgroundColor(getResources().getColor(R.color.background));
        if (!bluetooth) {
            if (!joueur) {
                if (couleurOrdinateur) {
                    addView(linBlancs);
                    addView(echiquier);
                    addView(linNoirs);
                } else {
                    addView(linNoirs);
                    addView(echiquier);
                    addView(linBlancs);
                }
            } else {
                addView(linNoirs);
                addView(echiquier);
                addView(linBlancs);
            }
        } else {
            if (couleurBluetooth) {
                addView(linNoirs);
                addView(echiquier);
                addView(linBlancs);
            } else {
                addView(linBlancs);
                addView(echiquier);
                addView(linNoirs);
            }
        }
    }

    /**
     * initialise les textes des timers en fonction des valeurs choisies au début de la partie
     */
    private void intiailiserTxtTimers() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(largeurEcran / 12, largeurEcran / 12);
        txtTimerBlancs = new TextView(context);
        txtTimerNoirs = new TextView(context);
        if (secondesBlancs < 10) {
            txtTimerBlancs.setText(minutesBlancs + ":0" + secondesBlancs);
        } else {
            txtTimerBlancs.setText(minutesBlancs + ":" + secondesBlancs);
        }
        if (secondesNoirs < 10) {
            txtTimerNoirs.setText(minutesNoirs + ":0" + secondesNoirs);
        } else {
            txtTimerNoirs.setText(minutesNoirs + ":" + secondesNoirs);
        }
        txtTimerBlancs.setTextColor(Color.WHITE);
        txtTimerNoirs.setTextColor(Color.WHITE);
        txtTimerBlancs.setTextSize(15);
        txtTimerNoirs.setTextSize(15);
        txtTimerBlancs.setPadding(largeurEcran / 40, 0, largeurEcran / 40, 0);
        txtTimerNoirs.setPadding(largeurEcran / 40, 0, largeurEcran / 40, 0);
        if (minutesBlancs == 0) {
            txtTimerBlancs.setVisibility(INVISIBLE);
            timerBlancs = false;
        }
        if (minutesNoirs == 0) {
            txtTimerNoirs.setVisibility(INVISIBLE);
            timerNoirs = false;
        }
    }

    /**
     * initialise le LinearLayout contenant le timer, les pièces mangés et le bouton indiquant le tour pour les noirs
     */
    private void initialiserLinNoirs() {
        linNoirs = new LinearLayout(context);
        LayoutParams layoutLinNoirs = new LayoutParams(largeurEcran, (hauteurEcran - (8 * largeurEcran)) / 2);
        linNoirs.setLayoutParams(layoutLinNoirs);
        boutonNoirs = new ImageView(context);
        boutonNoirs.setPadding(largeurEcran / 24, 0, largeurEcran / 24, 0);
        ViewGroup.LayoutParams layoutButton = new ViewGroup.LayoutParams(largeurEcran / 6, largeurEcran / 6);
        boutonNoirs.setLayoutParams(layoutButton);
        boutonNoirs.setImageDrawable(getResources().getDrawable(R.drawable.bouton_noir));
        boutonNoirs.setVisibility(INVISIBLE);
        linNoirs.setOrientation(HORIZONTAL);
        linNoirs.setVerticalGravity(Gravity.CENTER_VERTICAL);
        linNoirs.setHorizontalGravity(Gravity.LEFT);
        linNoirs.addView(boutonNoirs);
        linNoirs.addView(gridMangeNoirs);
        if (txtTimerNoirs != null) {
            linNoirs.addView(txtTimerNoirs);
        }
    }

    /**
     * initialise le LinearLayout contenant le timer, les pièces mangés et le bouton indiquant le tour pour les noirs
     */
    private void initialiserLinBlancs() {
        linBlancs = new LinearLayout(context);
        LayoutParams layoutLinBlancs = new LayoutParams(largeurEcran, (hauteurEcran - (8 * largeurEcran)) / 2);
        linBlancs.setLayoutParams(layoutLinBlancs);
        boutonBlancs = new ImageView(context);
        boutonBlancs.setPadding(largeurEcran / 24, 0, largeurEcran / 24, 0);
        ViewGroup.LayoutParams layoutButton = new ViewGroup.LayoutParams(largeurEcran / 6, largeurEcran / 6);
        boutonBlancs.setLayoutParams(layoutButton);
        boutonBlancs.setImageDrawable(getResources().getDrawable(R.drawable.bouton_blanc));
        boutonBlancs.setVisibility(VISIBLE);
        linBlancs.setOrientation(HORIZONTAL);
        linBlancs.setVerticalGravity(Gravity.CENTER_VERTICAL);
        linBlancs.setHorizontalGravity(Gravity.LEFT);
        linBlancs.addView(boutonBlancs);
        linBlancs.addView(gridMangeBlancs);
        if (txtTimerBlancs != null) {
            linBlancs.addView(txtTimerBlancs);
        }
    }

    /**
     * initialise les GridLayout des pièces mangées
     */
    private void initialiserGrids() {
        lstPiecesMangees = new ArrayList();
        gridMangeNoirs = new GridLayout(context);
        gridMangeBlancs = new GridLayout(context);
        ArrayList<GridLayout.Spec> lignesNoir = new ArrayList();
        ArrayList<GridLayout.Spec> colonnesNoir = new ArrayList();
        ArrayList<GridLayout.Spec> lignesBlanc = new ArrayList();
        ArrayList<GridLayout.Spec> colonnesBlanc = new ArrayList();
        for (int i = 0; i < 8; i++) {
            if (i < 2) {
                lignesNoir.add(GridLayout.spec(i));
                lignesBlanc.add(GridLayout.spec(i));
            }
            colonnesNoir.add(GridLayout.spec(i));
            colonnesBlanc.add(GridLayout.spec(i));
        }
        for (int i = 0; i < lignesNoir.size() + lignesBlanc.size(); i++) {
            for (int j = 0; j < colonnesNoir.size(); j++) {
                boolean couleur;
                GridLayout.LayoutParams par;
                if (i < 2) {
                    couleur = true;
                    par = new GridLayout.LayoutParams(lignesNoir.get(i), colonnesNoir.get(j));
                } else {
                    couleur = false;
                    par = new GridLayout.LayoutParams(lignesBlanc.get(i - 2), colonnesBlanc.get(j));
                }
                int cote = largeurEcran / 12;
                par.width = cote;
                par.height = cote;
                Piece piece = new Pion(context, couleur, cote);
                if (i % 2 == 0) {
                    switch (j) {
                        case 0:
                            piece = new Reine(context, couleur, cote);
                            break;
                        case 1:
                            piece = new Tour(context, couleur, cote);
                            break;
                        case 2:
                            piece = new Tour(context, couleur, cote);
                            break;
                        case 3:
                            piece = new Fou(context, couleur, cote);
                            break;
                        case 4:
                            piece = new Fou(context, couleur, cote);
                            break;
                        case 5:
                            piece = new Cavalier(context, couleur, cote);
                            break;
                        case 6:
                            piece = new Cavalier(context, couleur, cote);
                            break;
                        case 7:
                            piece = null;
                            break;
                    }
                }
                if (piece != null) {
                    piece.setLayoutParams(par);
                    if (i < 2) {
                        gridMangeNoirs.addView(piece, par);
                    } else {
                        gridMangeBlancs.addView(piece, par);
                    }
                    piece.setVisibility(INVISIBLE);
                    lstPiecesMangees.add(piece);
                }
            }
        }
    }

    /**
     * change la visibilité des boutons indiquant le tour
     *
     * @param tour le tour actuel
     */
    public void changerBouton(boolean tour) {
        if (tour) {
            boutonBlancs.setVisibility(VISIBLE);
            boutonNoirs.setVisibility(INVISIBLE);
        } else {
            boutonNoirs.setVisibility(VISIBLE);
            boutonBlancs.setVisibility(INVISIBLE);
        }
    }

    /**
     * change les LinearLayout de place permettant de tourner l'échiquier
     *
     * @param tour le tour actuel
     */
    public void changerLins(boolean tour) {
        removeAllViews();
        if (tour) {
            addView(linNoirs);
            addView(echiquier);
            addView(linBlancs);
        } else {
            addView(linBlancs);
            addView(echiquier);
            addView(linNoirs);
        }
    }

    /**
     * Indique quelles pièces ont été mangées
     *
     * @param valeur valeur de la pièce mangée
     */
    public void changerPiecesMangees(int valeur) {
        int indice = 0;
        if (valeur % 20 >= 10) {
            indice = 15;
        }
        if (valeur > 20) {
            valeur = 1;
        }
        switch (valeur % 10) {
            case 1:
            case 7:
                indice += 7;
                int ajout = 0;
                for (int i = 0; i < 8; i++) {
                    if (lstPiecesMangees.get(indice + i).getVisibility() == VISIBLE) {
                        ajout += 1;
                    }
                }
                indice += ajout;
                break;
            case 2:
                indice += 5;
                if (lstPiecesMangees.get(indice).getVisibility() == VISIBLE) {
                    indice += 1;
                }
                break;
            case 3:
                indice += 3;
                if (lstPiecesMangees.get(indice).getVisibility() == VISIBLE) {
                    indice += 1;
                }
                break;
            case 4:
            case 8:
                indice += 1;
                if (lstPiecesMangees.get(indice).getVisibility() == VISIBLE) {
                    indice += 1;
                }
                break;
            case 5:
                break;
        }

        lstPiecesMangees.get(indice).setVisibility(VISIBLE);
    }

    /**
     * change si le timer est activé ou non
     *
     * @param timerEnMarche vrai si le timer est activé
     */
    public void setTimerEnMarche(boolean timerEnMarche) {
        if (!premierCoup) {
            controleur.setTimerEnMarche(timerEnMarche);
        }
    }

    /**
     * change si c'est le premier coup
     *
     * @param premierCoup vrai si c'est le premier coup
     */
    public void setPremierCoup(boolean premierCoup) {
        this.premierCoup = premierCoup;
    }

    /**
     * change la valeur du timer en fonction du tour
     *
     * @param tour le tour actuel
     */
    public void changerTimer(boolean tour) {
        if (tour) {
            if (secondesBlancs != 0) {
                secondesBlancs -= 1;
            } else {
                if (minutesBlancs == 0 && timerBlancs) {
                    controleur.setTimerEnMarche(false);
                    sauvegarderPartie(VICTOIRE_NOIRS);
                } else {
                    minutesBlancs -= 1;
                    secondesBlancs = 59;
                }
            }
            if (secondesBlancs < 10) {
                txtTimerBlancs.setText(minutesBlancs + ":0" + secondesBlancs);
            } else {
                txtTimerBlancs.setText(minutesBlancs + ":" + secondesBlancs);
            }
        } else {
            if (secondesNoirs != 0) {
                secondesNoirs -= 1;
            } else {
                if (minutesNoirs == 0 && timerNoirs) {
                    controleur.setTimerEnMarche(false);
                    sauvegarderPartie(VICTOIRE_BLANCS);
                } else {
                    minutesNoirs -= 1;
                    secondesNoirs = 59;
                }
            }
            if (secondesNoirs < 10) {
                txtTimerNoirs.setText(minutesNoirs + ":0" + secondesNoirs);
            } else {
                txtTimerNoirs.setText(minutesNoirs + ":" + secondesNoirs);
            }
        }
    }

    /**
     * permet d'afficher un message sous forme de Toast s'il y a échec et sinon demande si l'utilisateur veut sauvegarder la partie si la partie est finie
     */
    public void message() {
        boolean tour = echiquier.getTourDeJeu();
        int etat = controleur.getEtat(tour);
        if (bluetooth) {
            if (bughouse) {
                enableButtonsBughouse(tour);
            }
        }
        switch (etat) {
            case 1:
                Toast.makeText(context, "Échec", Toast.LENGTH_SHORT).show();
                if (!bluetooth) {
                    if (!joueur) {
                        if (tour == couleurOrdinateur) {
                            controleur.jouerOrdinateur();
                        }
                    }
                }
                break;
            case VICTOIRE_NOIRS:
                sauvegarderPartie(etat);
                break;
            case 11:
                Toast.makeText(context, "Échec", Toast.LENGTH_SHORT).show();
                if (!bluetooth) {
                    if (!joueur) {
                        if (tour == couleurOrdinateur) {
                            controleur.jouerOrdinateur();
                        }
                    }
                }
                break;
            case VICTOIRE_BLANCS:
                sauvegarderPartie(etat);
                break;
            case NULLE:
                sauvegarderPartie(etat);
                break;
            default:
                if (!bluetooth) {
                    if (!joueur) {
                        if (tour == couleurOrdinateur) {
                            controleur.jouerOrdinateur();
                        }
                    }
                }
                break;
        }
    }

    /**
     * demande à l'utilisateur s'il veut sauvegarder sa partie pour ensuite afficher un menu lui demandant s'il veut revenir au menu ou commencer une nouvelle partie
     *
     * @param etat indique qui a gagné ou s'il y a eu nulle
     */
    private void sauvegarderPartie(final int etat) {
        controleur.setTimerEnMarche(false);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        controleur.sauvegarderPartie();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
                if (bluetooth) {
                    ((JeuBluetooth) context).retournerAuMenu();
                } else {
                    switch (etat) {
                        case VICTOIRE_NOIRS:
                            if (!bluetooth) {
                                ((Jeu) context).victoire(VICTOIRE_NOIRS);
                            } else {
                                ((JeuBluetooth) context).victoire(VICTOIRE_NOIRS);
                            }
                            break;
                        case VICTOIRE_BLANCS:
                            if (!bluetooth) {
                                ((Jeu) context).victoire(VICTOIRE_BLANCS);
                            } else {
                                ((JeuBluetooth) context).victoire(VICTOIRE_BLANCS);
                            }
                            break;
                        case NULLE:
                            if (!bluetooth) {
                                ((Jeu) context).victoire(NULLE);
                            } else {
                                ((JeuBluetooth) context).victoire(NULLE);
                            }
                            break;
                    }
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Voulez-vous sauvegarder la partie?")
                .setPositiveButton("Oui", dialogClickListener)
                .setNegativeButton("Non", dialogClickListener)
                .show();
    }

    /**
     * @return l'échiquier
     */
    public Echiquier getEchiquier() {
        return echiquier;
    }

    /**
     * actualise les préférences
     */
    public void actualiserPreferences() {
        controleur.changerPreferences();
    }

    /**
     * permet de changer les préférences dans l'échiquier
     *
     * @param preferenceTourner la valeur associée à si l'utilisateur veut tourner l'échiquier
     * @param dernierCoup       vrai si l'utilisateur veut que le dernier coup soit affiché
     * @param coupsPossibles    vrai si l'utilisateur veut que les coups possibles lui soient affichées
     */
    public void changerPreferences(int preferenceTourner, boolean dernierCoup, boolean coupsPossibles) {
        echiquier.setPreferenceTourner(preferenceTourner);
        echiquier.setDernierCoup(dernierCoup);
        echiquier.setCoupsPossibles(coupsPossibles);
    }

    /**
     * @return le controleur
     */
    public Controleur getControleur() {
        return controleur;
    }

    //0 : rien
    //1 : roi blanc en echec
    //2 : roi blanc en mat
    //11 : roi noir en echec
    //12 : roi noir en mat
    //20 : pat

    /*14 12 13 15 16 13 12 14
    11 11 11 11 11 11 11 11
    0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0
    1 1 1 1 1 1 1 1
    4 2 3 5 6 3 2 4*/
}
