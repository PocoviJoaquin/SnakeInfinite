package com.snakeinfinite.game.utiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.snakeinfinite.game.redes.HiloCliente;

/**
 * Maneja el input del cliente y envía la dirección al servidor
 */
public class ManejoDeInputCliente extends InputAdapter {
    private HiloCliente hiloCliente;
    private int numeroJugador; // 1 o 2
    private int direccionActualX = 1; // Derecha por defecto
    private int direccionActualY = 0;
    private int direccionAnteriorX = 1;
    private int direccionAnteriorY = 0;

    public ManejoDeInputCliente(HiloCliente hiloCliente, int numeroJugador) {
        this.hiloCliente = hiloCliente;
        this.numeroJugador = numeroJugador;
    }

    /**
     * Actualiza y envía la dirección al servidor
     */
    public void actualizarDireccion() {
        int nuevaDirX = direccionActualX;
        int nuevaDirY = direccionActualY;

        // Jugador 1 usa WASD, Jugador 2 usa Flechas
        if (numeroJugador == 1) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) && direccionActualY == 0) {
                nuevaDirX = 0;
                nuevaDirY = 1;
            } else if (Gdx.input.isKeyPressed(Input.Keys.S) && direccionActualY == 0) {
                nuevaDirX = 0;
                nuevaDirY = -1;
            } else if (Gdx.input.isKeyPressed(Input.Keys.A) && direccionActualX == 0) {
                nuevaDirX = -1;
                nuevaDirY = 0;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D) && direccionActualX == 0) {
                nuevaDirX = 1;
                nuevaDirY = 0;
            }
        } else if (numeroJugador == 2) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP) && direccionActualY == 0) {
                nuevaDirX = 0;
                nuevaDirY = 1;
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && direccionActualY == 0) {
                nuevaDirX = 0;
                nuevaDirY = -1;
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && direccionActualX == 0) {
                nuevaDirX = -1;
                nuevaDirY = 0;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && direccionActualX == 0) {
                nuevaDirX = 1;
                nuevaDirY = 0;
            }
        }

        // Solo enviar si la dirección cambió
        if (nuevaDirX != direccionAnteriorX || nuevaDirY != direccionAnteriorY) {
            direccionActualX = nuevaDirX;
            direccionActualY = nuevaDirY;
            enviarDireccion(nuevaDirX, nuevaDirY);
            direccionAnteriorX = nuevaDirX;
            direccionAnteriorY = nuevaDirY;
        }
    }

    private void enviarDireccion(int dirX, int dirY) {
        // Formato: "CambiarDireccion:dirX:dirY"
        String mensaje = "CambiarDireccion:" + dirX + ":" + dirY;
        hiloCliente.enviarMensaje(mensaje);
    }

    public void setDireccion(int x, int y) {
        this.direccionActualX = x;
        this.direccionActualY = y;
    }
}