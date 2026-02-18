package com.snakeinfinite.game.utiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class ManejoDeAudio {

    private static HashMap<String, Sound> sonidos = new HashMap<>();

    // Cargar todos los sonidos del juego
    public static void cargar() {
        cargarSonido("sonido_seleccion", "sonidos/sonido_seleccion.mp3");
        cargarSonido("sonido_manzana", "sonidos/sonido_manzana.mp3");

        // Agregar más sonidos cuando los tengas:
        // cargarSonido("confirmar", "audios/sonidos/confirmar.wav");
        // cargarSonido("mover", "audios/sonidos/mover.mp3");
    }

    private static void cargarSonido(String nombre, String ruta) {
        try {
            Sound s = Gdx.audio.newSound(Gdx.files.internal(ruta));
            sonidos.put(nombre, s);
        } catch (Exception e) {
            System.err.println("ERROR cargando sonido: " + ruta);
        }
    }

    public static void reproducirSonido(String nombre) {
        Sound s = sonidos.get(nombre);
        if (s != null) {
            s.play(1f);
            System.out.println("Reproduciendo: " + nombre);
        } else {
            System.out.println("Sonido no encontrado: " + nombre);
        }
    }

    public static void dispose() {
        for (Sound s : sonidos.values()) {
            s.dispose();
        }
        sonidos.clear();
    }
}
