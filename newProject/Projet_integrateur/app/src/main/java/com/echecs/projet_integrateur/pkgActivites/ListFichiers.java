package com.echecs.projet_integrateur.pkgActivites;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

/**
 * Affiche la liste des fichiers de sauvegarde avec leur nom
 */
public class ListFichiers extends ListActivity {

    private String[] nomFichiers;

    /**
     * création de l'activité, s'assure que des parties ont été sauvegardées
     *
     * @param savedInstanceState technicalité du programme
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiserNomFichiers();
        if (nomFichiers != null) {
            setListAdapter(new ArrayAdapter<String>(ListFichiers.this, android.R.layout.simple_list_item_1, nomFichiers));
        } else {
            Toast.makeText(this, "Aucune partie sauvegardée", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * initialise les noms des différents fichiers
     */
    private void initialiserNomFichiers() {
        File f = getFilesDir();
        File[] files = f.listFiles();
        if (files.length != 0) {
            nomFichiers = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                nomFichiers[i] = files[i].getName();
            }
        }
    }

    /**
     * appelé lorsqu'un item de la liste est cliqué
     *
     * @param l        le ListView de l'item
     * @param v        l'item
     * @param position la position de l'item dans le ListView
     * @param id       l'id de l'item
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(ListFichiers.this, Revision.class);
        intent.setAction(nomFichiers[position]);
        startActivity(intent);
        finish();
    }
}
