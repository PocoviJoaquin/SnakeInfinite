package com.snakeinfinite.game.utiles;

import com.badlogic.gdx.Game;
import com.snakeinfinite.game.pantallas.MenuPrincipal;

public class Principal extends Game {

    @Override
    public void create() {

        // Cargar sonidos al iniciar el juego
        ManejoDeAudio.cargar();

        // Iniciar con el menú principal
        setScreen(new MenuPrincipal(this));
    }

    @Override
    public void dispose() {
        super.dispose();

        // Liberar sonidos
        ManejoDeAudio.dispose();

        // Liberar pantalla actual
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
