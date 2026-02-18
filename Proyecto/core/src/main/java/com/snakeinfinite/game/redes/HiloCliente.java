package com.snakeinfinite.game.redes;

import com.badlogic.gdx.Gdx;
import com.snakeinfinite.game.interfaces.ControladorJuegoRed;

import java.io.IOException;
import java.net.*;

/**
 * Hilo encargado de recibir y enviar mensajes UDP al servidor de Snake Infinite
 */
public class HiloCliente extends Thread {
    private DatagramSocket socket;
    private int puertoServidor = 5555;
    private String ipServidorStr = "255.255.255.255"; // Broadcast inicial
    private InetAddress ipServidor;
    private boolean finalizado = false;
    private ControladorJuegoRed controladorJuego;

    public HiloCliente(ControladorJuegoRed controladorJuego) {
        try {
            this.controladorJuego = controladorJuego;
            ipServidor = InetAddress.getByName(ipServidorStr);
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            System.out.println("Cliente UDP creado. Buscando servidor...");
        } catch (SocketException | UnknownHostException e) {
            System.err.println("Error al crear socket del cliente: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        enviarMensaje("Conectar");
        while (!finalizado) {
            DatagramPacket paquete = new DatagramPacket(new byte[2048], 2048);
            try {
                socket.receive(paquete);
                procesarMensaje(paquete);
            } catch (IOException e) {
                if (!finalizado) {
                    System.err.println("Error al recibir paquete: " + e.getMessage());
                }
            }
        }
    }

    private void procesarMensaje(DatagramPacket paquete) {
        String mensaje = (new String(paquete.getData())).trim();
        String[] partes = mensaje.split(":");

        System.out.println("[CLIENTE] Mensaje recibido: " + mensaje);

        switch (partes[0]) {
            case "Conectado":
                // Formato: Conectado:numeroJugador:tiempoPartida
                this.ipServidor = paquete.getAddress();
                int numeroJugador = Integer.parseInt(partes[1]);
                float tiempoRestante = Float.parseFloat(partes[2]);

                Gdx.app.postRunnable(() ->
                        controladorJuego.onConectar(numeroJugador, tiempoRestante)
                );
                break;

            case "Iniciar":
                // El servidor indica que ambos jugadores están listos
                Gdx.app.postRunnable(() -> controladorJuego.onIniciarJuego());
                break;

            case "ActualizarPosicion":
                // Formato: ActualizarPosicion:numeroJugador:x:y
                int numJugador = Integer.parseInt(partes[1]);
                float x = Float.parseFloat(partes[2]);
                float y = Float.parseFloat(partes[3]);

                Gdx.app.postRunnable(() ->
                        controladorJuego.onActualizarPosicionSerpiente(numJugador, x, y)
                );
                break;

            case "ActualizarCuerpo":
                // Formato: ActualizarCuerpo:numeroJugador:x1,y1;x2,y2;x3,y3...
                int numJug = Integer.parseInt(partes[1]);
                String cuerpo = partes[2];

                Gdx.app.postRunnable(() ->
                        controladorJuego.onActualizarCuerpoSerpiente(numJug, cuerpo)
                );
                break;

            case "ActualizarManzana":
                // Formato: ActualizarManzana:x:y
                float manzanaX = Float.parseFloat(partes[1]);
                float manzanaY = Float.parseFloat(partes[2]);

                Gdx.app.postRunnable(() ->
                        controladorJuego.onActualizarPosicionManzana(manzanaX, manzanaY)
                );
                break;

            case "ActualizarPuntaje":
                // Formato: ActualizarPuntaje:puntaje1:puntaje2
                int p1 = Integer.parseInt(partes[1]);
                int p2 = Integer.parseInt(partes[2]);

                Gdx.app.postRunnable(() ->
                        controladorJuego.onActualizarPuntaje(p1, p2)
                );
                break;

            case "ActualizarTiempo":
                // Formato: ActualizarTiempo:segundosRestantes
                float tiempo = Float.parseFloat(partes[1]);

                Gdx.app.postRunnable(() ->
                        controladorJuego.onActualizarTiempo(tiempo)
                );
                break;

            case "ManzanaComida":
                // Formato: ManzanaComida:numeroJugador
                int jugadorComio = Integer.parseInt(partes[1]);

                Gdx.app.postRunnable(() ->
                        controladorJuego.onManzanaComida(jugadorComio)
                );
                break;

            case "Colision":
                // Formato: Colision:numeroJugadorQueChoco
                int jugadorChoco = Integer.parseInt(partes[1]);

                Gdx.app.postRunnable(() ->
                        controladorJuego.onColision(jugadorChoco)
                );
                break;

            case "FinalizarJuego":
                // Formato: FinalizarJuego:ganador:razon
                int ganador = Integer.parseInt(partes[1]);
                String razon = partes[2];

                Gdx.app.postRunnable(() ->
                        controladorJuego.onFinalizarJuego(ganador, razon)
                );
                break;

            case "Desconexion":
                int jugadorQueSeFue = Integer.parseInt(partes[1]);
                Gdx.app.postRunnable(() ->
                        controladorJuego.onJugadorDesconectado(jugadorQueSeFue)
                );
                break;
        }
    }

    /**
     * Envía un mensaje al servidor
     */
    public void enviarMensaje(String mensaje) {
        byte[] mensajeBytes = mensaje.getBytes();
        DatagramPacket paquete = new DatagramPacket(
                mensajeBytes, mensajeBytes.length, ipServidor, puertoServidor
        );

        try {
            socket.send(paquete);
            System.out.println("[CLIENTE] Mensaje enviado: " + mensaje);
        } catch (IOException e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
        }
    }

    /**
     * Cierra el cliente
     */
    public void terminar() {
        this.finalizado = true;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        this.interrupt();
    }
}