package com.snakeinfinite.game.utiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Recursos {

    public static Texture fondoMenu;
    public static Texture botonJugar;
    public static Texture botonOpciones;
    public static Texture botonSalir;

    public static void cargar() {
    }

    public static void destruir() {
        fondoMenu.dispose();
        botonJugar.dispose();
        botonOpciones.dispose();
        botonSalir.dispose();
    }
}
