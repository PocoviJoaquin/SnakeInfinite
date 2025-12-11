package com.snakeinfinite.game.entidades;

import com.badlogic.gdx.utils.Array;

public class Serpiente {

    public enum Direccion {
        ARRIBA, ABAJO, IZQUIERDA, DERECHA
    }

    private Array<SegmentoSerpiente> cuerpo;

    private Direccion direccionActual;
    private float temporizadorMovimiento;
    private final float tiempoPorPaso; // frecuencia del movimiento
    private final int tamañoTile;

    public Serpiente(int inicioX, int inicioY, int tamañoTile) {
        this.tamañoTile = tamañoTile;
        this.tiempoPorPaso = 0.15f;  // cada 0.15 segundos se mueve un tile
        this.temporizadorMovimiento = 0;

        cuerpo = new Array<>();
        cuerpo.add(new SegmentoSerpiente(inicioX, inicioY)); // cabeza

        direccionActual = Direccion.DERECHA;
    }

    /**
     * Cambia la dirección de movimiento de la serpiente.
     * Evita movimientos de 180 grados.
     */
    public void cambiarDireccion(Direccion nuevaDireccion) {
        if (direccionActual == Direccion.ARRIBA && nuevaDireccion == Direccion.ABAJO) return;
        if (direccionActual == Direccion.ABAJO && nuevaDireccion == Direccion.ARRIBA) return;
        if (direccionActual == Direccion.IZQUIERDA && nuevaDireccion == Direccion.DERECHA) return;
        if (direccionActual == Direccion.DERECHA && nuevaDireccion == Direccion.IZQUIERDA) return;

        direccionActual = nuevaDireccion;
    }

    /**
     * Actualiza el estado de la serpiente.
     */
    public void actualizar(float delta) {
        temporizadorMovimiento += delta;

        if (temporizadorMovimiento >= tiempoPorPaso) {
            mover();
            temporizadorMovimiento = 0;
        }
    }

    /**
     * Mueve la serpiente un "paso" en la dirección actual.
     */
    private void mover() {
        int xVieja = cuerpo.get(0).x;
        int yVieja = cuerpo.get(0).y;

        // Mover cabeza
        switch (direccionActual) {
            case ARRIBA: cuerpo.get(0).y += tamañoTile; break;
            case ABAJO: cuerpo.get(0).y -= tamañoTile; break;
            case IZQUIERDA: cuerpo.get(0).x -= tamañoTile; break;
            case DERECHA: cuerpo.get(0).x += tamañoTile; break;
        }

        // Mover cuerpo (delante → atrás)
        for (int i = 1; i < cuerpo.size; i++) {
            int xTemp = cuerpo.get(i).x;
            int yTemp = cuerpo.get(i).y;

            cuerpo.get(i).x = xVieja;
            cuerpo.get(i).y = yVieja;

            xVieja = xTemp;
            yVieja = yTemp;
        }
    }

    /**
     * Agrega un nuevo segmento al final del cuerpo de la serpiente.
     */
    public void crecer() {
        SegmentoSerpiente ultimo = cuerpo.peek();
        cuerpo.add(new SegmentoSerpiente(ultimo.x, ultimo.y));
    }

    /**
     * Comprueba si la cabeza colisiona con algún segmento de su propio cuerpo.
     */
    public boolean colisionaConSuCuerpo() {
        SegmentoSerpiente cabeza = cuerpo.get(0);

        for (int i = 1; i < cuerpo.size; i++) {
            if (cabeza.x == cuerpo.get(i).x && cabeza.y == cuerpo.get(i).y) {
                return true;
            }
        }

        return false;
    }

    // Getters
    public Array<SegmentoSerpiente> obtenerCuerpo() {
        return cuerpo;
    }

    public int obtenerCabezaX() {
        return cuerpo.get(0).x;
    }

    public int obtenerCabezaY() {
        return cuerpo.get(0).y;
    }

    public Direccion obtenerDireccion() {
        return direccionActual;
    }
}
