package com.snakeinfinite.game.interfaces;

/**
 * Interfaz para manejar eventos de red del juego multijugador Snake Infinite
 */
public interface ControladorJuegoRed {
    /**
     * Llamado cuando el cliente se conecta exitosamente al servidor
     * @param numeroJugador 1 o 2
     * @param tiempoRestante Tiempo de partida en segundos
     */
    void onConectar(int numeroJugador, float tiempoRestante);

    /**
     * Llamado cuando el servidor inicia la partida (ambos jugadores conectados)
     */
    void onIniciarJuego();

    /**
     * Actualiza la posición de la cabeza de una serpiente
     * @param numeroJugador 1 o 2
     * @param x Coordenada X de la cabeza
     * @param y Coordenada Y de la cabeza
     */
    void onActualizarPosicionSerpiente(int numeroJugador, float x, float y);

    /**
     * Actualiza toda la serpiente (cuando crece)
     * @param numeroJugador 1 o 2
     * @param cuerpo String con coordenadas separadas por comas: "x1,y1;x2,y2;x3,y3"
     */
    void onActualizarCuerpoSerpiente(int numeroJugador, String cuerpo);

    /**
     * Actualiza la posición de una manzana
     * @param x Coordenada X
     * @param y Coordenada Y
     */
    void onActualizarPosicionManzana(float x, float y);

    /**
     * Actualiza el puntaje (manzanas obtenidas) de ambos jugadores
     * @param puntaje1 Manzanas del jugador 1
     * @param puntaje2 Manzanas del jugador 2
     */
    void onActualizarPuntaje(int puntaje1, int puntaje2);

    /**
     * Actualiza el tiempo restante de partida
     * @param tiempoRestante Segundos restantes
     */
    void onActualizarTiempo(float tiempoRestante);

    /**
     * Llamado cuando un jugador come una manzana
     * @param numeroJugador Jugador que comió
     */
    void onManzanaComida(int numeroJugador);

    /**
     * Llamado cuando un jugador choca con el cuerpo del otro
     * @param numeroJugador Jugador que chocó (perdedor)
     */
    void onColision(int numeroJugador);

    /**
     * Llamado cuando el juego termina
     * @param ganador 1, 2, o 0 para empate
     * @param razon "tiempo", "colision", "desconexion"
     */
    void onFinalizarJuego(int ganador, String razon);

    /**
     * Llamado cuando se debe volver al menú
     */
    void onVolverAlMenu();
}