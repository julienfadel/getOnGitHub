package com.echecs.projet_integrateur.pkgActivites;

import android.content.Context;
import android.webkit.WebView;

/**
 * Permet l'affichage de .gif
 */
public class GifWebView extends WebView {
    /**
     * @param context nécessaire à l'affichage
     * @param path    l'adresse du .gif
     */
    public GifWebView(Context context, String path) {
        super(context);
        loadUrl(path);
    }

}