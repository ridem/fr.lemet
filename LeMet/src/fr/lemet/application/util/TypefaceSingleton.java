package fr.lemet.application.util;

import android.graphics.Typeface;

import fr.lemet.application.application.TransportsMetzApplication;

public class TypefaceSingleton extends TransportsMetzApplication{

    private static TypefaceSingleton instance = new TypefaceSingleton();

    private TypefaceSingleton() {}

    public static TypefaceSingleton getInstance() {
        return instance;
    }

    public Typeface getDroidSerif() {
        return Typeface.createFromAsset(super.getAssets(), "fonts/Lato-Lig");
    }
}