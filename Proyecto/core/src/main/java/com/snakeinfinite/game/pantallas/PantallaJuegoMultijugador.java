package com.snakeinfinite.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.snakeinfinite.game.interfaces.ControladorJuegoRed;
import com.snakeinfinite.game.redes.HiloCliente;
import com.snakeinfinite.game.utiles.ManejoDeInputCliente;
import com.snakeinfinite.game.utiles.Principal;

import java.util.ArrayList;

public class PantallaJuegoMultijugador implements Screen, ControladorJuegoRed {

    private Principal juego;
    private OrthographicCamera camara;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont fuente;
    private BitmapFont fuenteHUD;
    private BitmapFont fuenteGameOver;
    private GlyphLayout layout;

    // Sprites
    private Texture texturaManzana;
    private Texture texturaCabezaJ1, texturaCuerpoJ1;
    private Texture texturaCabezaJ2, texturaCuerpoJ2;
    private Sprite spriteManzana;

    // RED
    private HiloCliente hiloCliente;
    private ManejoDeInputCliente manejoInput;
    private int miNumeroJugador = -1;

    // ESTADO DEL JUEGO
    private boolean esperandoConexion = true;
    private boolean juegoIniciado = false;
    private boolean juegoTerminado = false;

    // Serpientes (se actualizan desde el servidor)
    private ArrayList<Vector2> serpiente1;
    private ArrayList<Vector2> serpiente2;
    private Vector2 posicionManzana;

    // HUD
    private int puntaje1 = 0;
    private int puntaje2 = 0;
    private float tiempoRestante = 300; // 5 minutos

    // Configuración
    private static final int ANCHO_PANTALLA = 800;
    private static final int ALTO_PANTALLA = 600;
    private static final int ALTURA_HUD = 50;
    private static final int TAMAÑO_CELDA = 20;

    private static final Color COLOR_CESPED = new Color(0.2f, 0.6f, 0.2f, 1f);
    private static final Color COLOR_CESPED_CLARO = new Color(0.25f, 0.65f, 0.25f, 1f);

    public PantallaJuegoMultijugador(Principal juego) {
        this.juego = juego;

        this.camara = new OrthographicCamera();
        this.camara.setToOrtho(false, ANCHO_PANTALLA, ALTO_PANTALLA);

        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();

        this.fuente = new BitmapFont();
        this.fuente.getData().setScale(1.5f);

        this.fuenteHUD = new BitmapFont();
        this.fuenteHUD.getData().setScale(1.6f);

        this.fuenteGameOver = new BitmapFont();
        this.fuenteGameOver.getData().setScale(3.5f);

        this.layout = new GlyphLayout();

        // Inicializar listas vacías
        serpiente1 = new ArrayList<>();
        serpiente2 = new ArrayList<>();
        posicionManzana = new Vector2(0, 0);

        cargarSprites();

        // Conectar al servidor
        hiloCliente = new HiloCliente(this);
        hiloCliente.start();
        hiloCliente.enviarMensaje("Conectar");

        System.out.println("Buscando servidor...");
    }

    private void cargarSprites() {
        try {
            texturaManzana = new Texture(Gdx.files.internal("imagenes/manzana.png"));
            spriteManzana = new Sprite(texturaManzana);
            spriteManzana.setSize(TAMAÑO_CELDA, TAMAÑO_CELDA);

            // Jugador 1: Verde
            texturaCabezaJ1 = new Texture(Gdx.files.internal("imagenes/serpiente_cabeza.png"));
            texturaCuerpoJ1 = new Texture(Gdx.files.internal("imagenes/serpiente_cuerpo.png"));

            // Jugador 2: Azul (puedes usar los mismos o crear otros)
            texturaCabezaJ2 = texturaCabezaJ1; // Temporal
            texturaCuerpoJ2 = texturaCuerpoJ1;

            System.out.println("✓ Sprites cargados");
        } catch (Exception e) {
            System.err.println("Error cargando sprites: " + e.getMessage());
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camara.update();
        shapeRenderer.setProjectionMatrix(camara.combined);
        batch.setProjectionMatrix(camara.combined);

        if (esperandoConexion) {
            dibujarPantallaEspera();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                volverAlMenu();
            }
            return;
        }

        if (juegoIniciado && !juegoTerminado && manejoInput != null) {
            manejoInput.actualizarDireccion();
        }

        // Dibujar juego
        dibujarCesped();
        dibujarEntidades();
        dibujarHUD();

        if (juegoTerminado) {
            dibujarGameOver();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            volverAlMenu();
        }
    }

    private void dibujarPantallaEspera() {
        batch.begin();
        fuente.setColor(Color.WHITE);
        fuente.getData().setScale(2f);

        String texto = miNumeroJugador == -1 ?
                "Conectando al servidor..." :
                "Esperando al rival... (Jugador " + miNumeroJugador + ")";

        layout.setText(fuente, texto);
        fuente.draw(batch, texto,
                (ANCHO_PANTALLA - layout.width) / 2,
                ALTO_PANTALLA / 2);

        fuente.getData().setScale(1f);
        String textoESC = "ESC para volver";
        layout.setText(fuente, textoESC);
        fuente.draw(batch, textoESC,
                (ANCHO_PANTALLA - layout.width) / 2,
                ALTO_PANTALLA / 2 - 50);

        batch.end();
    }

    private void dibujarCesped() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar área visible
        int xInicio = -20;
        int xFin = ANCHO_PANTALLA / TAMAÑO_CELDA + 20;
        int yInicio = -20;
        int yFin = ALTO_PANTALLA / TAMAÑO_CELDA + 20;

        for (int x = xInicio; x <= xFin; x++) {
            for (int y = yInicio; y <= yFin; y++) {
                if ((x + y) % 2 == 0) {
                    shapeRenderer.setColor(COLOR_CESPED);
                } else {
                    shapeRenderer.setColor(COLOR_CESPED_CLARO);
                }
                shapeRenderer.rect(x * TAMAÑO_CELDA, y * TAMAÑO_CELDA, TAMAÑO_CELDA, TAMAÑO_CELDA);
            }
        }

        shapeRenderer.end();
    }

    private void dibujarEntidades() {
        batch.begin();

        // Manzana
        if (posicionManzana != null) {
            spriteManzana.setPosition(
                    posicionManzana.x * TAMAÑO_CELDA,
                    posicionManzana.y * TAMAÑO_CELDA
            );
            spriteManzana.draw(batch);
        }

        batch.end();

        // Serpientes con ShapeRenderer (temporal, hasta tener sprites)
        dibujarSerpiente(serpiente1, Color.GREEN, Color.LIME);
        dibujarSerpiente(serpiente2, Color.BLUE, Color.SKY);
    }

    private void dibujarSerpiente(ArrayList<Vector2> cuerpo, Color colorCuerpo, Color colorCabeza) {
        if (cuerpo == null || cuerpo.isEmpty()) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < cuerpo.size(); i++) {
            Vector2 segmento = cuerpo.get(i);

            if (i == 0) {
                shapeRenderer.setColor(colorCabeza);
            } else {
                shapeRenderer.setColor(colorCuerpo);
            }

            shapeRenderer.rect(
                    segmento.x * TAMAÑO_CELDA,
                    segmento.y * TAMAÑO_CELDA,
                    TAMAÑO_CELDA,
                    TAMAÑO_CELDA
            );
        }

        shapeRenderer.end();

        // Bordes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);

        for (Vector2 segmento : cuerpo) {
            shapeRenderer.rect(
                    segmento.x * TAMAÑO_CELDA,
                    segmento.y * TAMAÑO_CELDA,
                    TAMAÑO_CELDA,
                    TAMAÑO_CELDA
            );
        }

        shapeRenderer.end();
    }

    private void dibujarHUD() {
        // Fondo HUD
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, ALTO_PANTALLA - ALTURA_HUD, ANCHO_PANTALLA, ALTURA_HUD);
        shapeRenderer.end();

        batch.begin();
        fuenteHUD.setColor(Color.WHITE);
        float yTexto = ALTO_PANTALLA - 16;

        // Tiempo (izquierda)
        int minutos = (int)(tiempoRestante / 60);
        int segundos = (int)(tiempoRestante % 60);
        String textoTiempo = String.format("Tiempo: %02d:%02d", minutos, segundos);
        fuenteHUD.draw(batch, textoTiempo, 20, yTexto);

        // Puntajes (centro)
        String textoPuntaje = String.format("J1: %d  -  J2: %d", puntaje1, puntaje2);
        layout.setText(fuenteHUD, textoPuntaje);
        fuenteHUD.draw(batch, textoPuntaje, (ANCHO_PANTALLA - layout.width) / 2, yTexto);

        // Tu jugador (derecha)
        String textoJugador = "Tu: J" + miNumeroJugador;
        layout.setText(fuenteHUD, textoJugador);
        fuenteHUD.draw(batch, textoJugador, ANCHO_PANTALLA - layout.width - 20, yTexto);

        batch.end();
    }

    private void dibujarGameOver() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.8f);
        shapeRenderer.rect(0, 0, ANCHO_PANTALLA, ALTO_PANTALLA);
        shapeRenderer.end();

        batch.begin();
        // Implementar mensaje de ganador
        batch.end();
    }

    // ========== CALLBACKS DE RED ==========

    @Override
    public void onConectar(int numeroJugador, float tiempoRestante) {
        System.out.println("✓ Conectado como Jugador " + numeroJugador);
        this.miNumeroJugador = numeroJugador;
        this.tiempoRestante = tiempoRestante;

        manejoInput = new ManejoDeInputCliente(hiloCliente, numeroJugador);
    }

    @Override
    public void onIniciarJuego() {
        System.out.println("✓ Juego iniciado");
        esperandoConexion = false;
        juegoIniciado = true;
    }

    @Override
    public void onActualizarPosicionSerpiente(int numeroJugador, float x, float y) {
        // Actualizar cabeza solamente (para movimientos rápidos)
        if (numeroJugador == 1 && !serpiente1.isEmpty()) {
            serpiente1.get(0).set(x, y);
        } else if (numeroJugador == 2 && !serpiente2.isEmpty()) {
            serpiente2.get(0).set(x, y);
        }
    }

    @Override
    public void onActualizarCuerpoSerpiente(int numeroJugador, String cuerpo) {
        // Formato: "x1,y1;x2,y2;x3,y3..."
        ArrayList<Vector2> nuevoCuerpo = new ArrayList<>();
        String[] segmentos = cuerpo.split(";");

        for (String segmento : segmentos) {
            String[] coords = segmento.split(",");
            float x = Float.parseFloat(coords[0]);
            float y = Float.parseFloat(coords[1]);
            nuevoCuerpo.add(new Vector2(x, y));
        }

        if (numeroJugador == 1) {
            serpiente1 = nuevoCuerpo;
        } else if (numeroJugador == 2) {
            serpiente2 = nuevoCuerpo;
        }
    }

    @Override
    public void onActualizarPosicionManzana(float x, float y) {
        posicionManzana.set(x, y);
    }

    @Override
    public void onActualizarPuntaje(int puntaje1, int puntaje2) {
        this.puntaje1 = puntaje1;
        this.puntaje2 = puntaje2;
    }

    @Override
    public void onActualizarTiempo(float tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    @Override
    public void onManzanaComida(int numeroJugador) {
        System.out.println("Jugador " + numeroJugador + " comió una manzana");
    }

    @Override
    public void onColision(int numeroJugador) {
        System.out.println("Jugador " + numeroJugador + " chocó");
    }

    @Override
    public void onFinalizarJuego(int ganador, String razon) {
        System.out.println("Juego terminado. Ganador: " + ganador + " | Razón: " + razon);
        juegoTerminado = true;
    }

    @Override
    public void onVolverAlMenu() {
        volverAlMenu();
    }

    private void volverAlMenu() {
        if (hiloCliente != null) {
            hiloCliente.enviarMensaje("Desconectar");
            hiloCliente.terminar();
            try { hiloCliente.join(1000); } catch (Exception ignored) {}
        }
        juego.setScreen(new MenuPrincipal(juego));
        dispose();
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {
        camara.setToOrtho(false, ANCHO_PANTALLA, ALTO_PANTALLA);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        fuente.dispose();
        fuenteHUD.dispose();
        fuenteGameOver.dispose();

        if (texturaManzana != null) texturaManzana.dispose();
        if (texturaCabezaJ1 != null) texturaCabezaJ1.dispose();
        if (texturaCuerpoJ1 != null) texturaCuerpoJ1.dispose();
    }
}