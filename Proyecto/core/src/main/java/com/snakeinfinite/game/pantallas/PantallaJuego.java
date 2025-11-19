package com.snakeinfinite.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.snakeinfinite.game.pantallas.MenuPrincipal;
import com.snakeinfinite.game.utiles.Principal;

import java.util.ArrayList;

public class PantallaJuego implements Screen {

    private Principal juego;
    private OrthographicCamera camara;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont fuente;
    private BitmapFont fuenteHUD;
    private BitmapFont fuenteGameOver;
    private GlyphLayout layout;

    // Configuración del juego
    private static final int ANCHO_PANTALLA = 800;
    private static final int ALTO_PANTALLA = 600;
    private static final int ALTURA_HUD = 60; // Espacio para el HUD
    private static final int ALTO_AREA_JUEGO = ALTO_PANTALLA - ALTURA_HUD;

    private static final int TAMAÑO_CELDA = 20;
    private static final int GRID_ANCHO = ANCHO_PANTALLA / TAMAÑO_CELDA;
    private static final int GRID_ALTO = ALTO_AREA_JUEGO / TAMAÑO_CELDA;

    // Color del césped (verde oscuro)
    private static final Color COLOR_CESPED = new Color(0.2f, 0.6f, 0.2f, 1f);
    private static final Color COLOR_CESPED_CLARO = new Color(0.25f, 0.65f, 0.25f, 1f);
    private static final Color COLOR_HUD = new Color(0.1f, 0.1f, 0.15f, 1f);

    // Serpiente
    private ArrayList<Vector2> cuerpoSerpiente;
    private Vector2 direccion;
    private Vector2 direccionPendiente;
    private float tiempoMovimiento;
    private static final float VELOCIDAD = 0.15f; // Segundos entre movimientos

    // Manzana
    private Vector2 posicionManzana;

    // Estado del juego
    private int manzanasObtenidas;
    private float tiempoJuego;
    private boolean juegoTerminado;
    private boolean pausado;

    public PantallaJuego(Principal juego) {
        this.juego = juego;

        // Inicializar cámara
        this.camara = new OrthographicCamera();
        this.camara.setToOrtho(false, ANCHO_PANTALLA, ALTO_PANTALLA);

        // Inicializar renderizadores
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();

        // Fuentes
        this.fuente = new BitmapFont();
        this.fuente.getData().setScale(1.5f);
        this.fuente.setColor(Color.WHITE);

        this.fuenteHUD = new BitmapFont();
        this.fuenteHUD.getData().setScale(2f);
        this.fuenteHUD.setColor(Color.WHITE);

        this.fuenteGameOver = new BitmapFont();
        this.fuenteGameOver.getData().setScale(3f);

        this.layout = new GlyphLayout();

        inicializarJuego();
    }

    private void inicializarJuego() {
        // Crear serpiente (3 segmentos iniciales en el centro)
        cuerpoSerpiente = new ArrayList<>();
        int centroX = GRID_ANCHO / 2;
        int centroY = GRID_ALTO / 2;

        cuerpoSerpiente.add(new Vector2(centroX, centroY));
        cuerpoSerpiente.add(new Vector2(centroX - 1, centroY));
        cuerpoSerpiente.add(new Vector2(centroX - 2, centroY));

        // Dirección inicial (derecha)
        direccion = new Vector2(1, 0);
        direccionPendiente = new Vector2(1, 0);

        // Generar primera manzana
        generarManzana();

        // Estado inicial
        manzanasObtenidas = 0;
        tiempoJuego = 0;
        tiempoMovimiento = 0;
        juegoTerminado = false;
        pausado = false;

        System.out.println("=== JUEGO INICIALIZADO ===");
        System.out.println("Serpiente en: " + cuerpoSerpiente.get(0));
        System.out.println("Primera manzana en: " + posicionManzana);
    }

    private void generarManzana() {
        // Generar posición aleatoria que no colisione con la serpiente
        int intentos = 0;
        do {
            int x = MathUtils.random(0, GRID_ANCHO - 1);
            int y = MathUtils.random(0, GRID_ALTO - 1);
            posicionManzana = new Vector2(x, y);
            intentos++;

            if (intentos > 100) {
                System.out.println("ADVERTENCIA: Demasiados intentos generando manzana");
                break;
            }
        } while (colisionConSerpiente(posicionManzana));

        System.out.println("-> Manzana generada en: (" + (int)posicionManzana.x + ", " + (int)posicionManzana.y + ")");
    }

    private boolean colisionConSerpiente(Vector2 posicion) {
        for (Vector2 segmento : cuerpoSerpiente) {
            if (segmento.equals(posicion)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(float delta) {
        manejarEntrada();

        if (!juegoTerminado && !pausado) {
            actualizar(delta);
        }

        dibujar();
    }

    private void manejarEntrada() {
        // Controles de movimiento
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (direccion.y == 0) {
                direccionPendiente = new Vector2(0, 1);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (direccion.y == 0) {
                direccionPendiente = new Vector2(0, -1);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            if (direccion.x == 0) {
                direccionPendiente = new Vector2(-1, 0);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (direccion.x == 0) {
                direccionPendiente = new Vector2(1, 0);
            }
        }

        // Pausa
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            pausado = !pausado;
        }

        // Reiniciar
        if (juegoTerminado && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            inicializarJuego();
        }

        // Volver al menú
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (juegoTerminado) {
                juego.setScreen(new MenuPrincipal(juego));
                dispose();
            } else {
                pausado = !pausado;
            }
        }
    }

    private void actualizar(float delta) {
        tiempoJuego += delta;
        tiempoMovimiento += delta;

        if (tiempoMovimiento >= VELOCIDAD) {
            tiempoMovimiento = 0;
            direccion = direccionPendiente.cpy();
            moverSerpiente();
        }
    }

    private void moverSerpiente() {
        Vector2 cabeza = cuerpoSerpiente.get(0);
        Vector2 nuevaCabeza = new Vector2(
            cabeza.x + direccion.x,
            cabeza.y + direccion.y
        );

        // Verificar colisión con bordes
        if (nuevaCabeza.x < 0 || nuevaCabeza.x >= GRID_ANCHO ||
            nuevaCabeza.y < 0 || nuevaCabeza.y >= GRID_ALTO) {
            juegoTerminado = true;
            System.out.println("GAME OVER: Colisión con borde");
            return;
        }

        // Verificar colisión con cuerpo
        if (colisionConSerpiente(nuevaCabeza)) {
            juegoTerminado = true;
            System.out.println("GAME OVER: Colisión con cuerpo");
            return;
        }

        // Agregar nueva cabeza
        cuerpoSerpiente.add(0, nuevaCabeza);

        // Verificar si comió manzana
        if (nuevaCabeza.equals(posicionManzana)) {
            manzanasObtenidas++;
            System.out.println("¡MANZANA COMIDA! Total: " + manzanasObtenidas + " | Longitud: " + cuerpoSerpiente.size());
            generarManzana();
            // NO remover la cola (la serpiente crece)
        } else {
            // Remover cola (movimiento normal)
            cuerpoSerpiente.remove(cuerpoSerpiente.size() - 1);
        }
    }

    private void dibujar() {
        // Limpiar pantalla
        Gdx.gl.glClearColor(COLOR_HUD.r, COLOR_HUD.g, COLOR_HUD.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camara.update();
        shapeRenderer.setProjectionMatrix(camara.combined);
        batch.setProjectionMatrix(camara.combined);

        // 1. Dibujar césped
        dibujarCesped();

        // 2. Dibujar manzana
        dibujarManzana();

        // 3. Dibujar serpiente
        dibujarSerpiente();

        // 4. Dibujar HUD
        dibujarHUD();

        // 5. Dibujar mensajes (pausa/game over)
        if (pausado || juegoTerminado) {
            dibujarMensajes();
        }
    }

    private void dibujarCesped() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int x = 0; x < GRID_ANCHO; x++) {
            for (int y = 0; y < GRID_ALTO; y++) {
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

    private void dibujarManzana() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Manzana roja
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(
            posicionManzana.x * TAMAÑO_CELDA,
            posicionManzana.y * TAMAÑO_CELDA,
            TAMAÑO_CELDA,
            TAMAÑO_CELDA
        );

        // Brillo
        shapeRenderer.setColor(1, 1, 1, 0.5f);
        shapeRenderer.rect(
            posicionManzana.x * TAMAÑO_CELDA + 3,
            posicionManzana.y * TAMAÑO_CELDA + TAMAÑO_CELDA - 6,
            5,
            4
        );

        shapeRenderer.end();
    }

    private void dibujarSerpiente() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < cuerpoSerpiente.size(); i++) {
            Vector2 segmento = cuerpoSerpiente.get(i);

            if (i == 0) {
                shapeRenderer.setColor(Color.LIME);
            } else {
                shapeRenderer.setColor(Color.GREEN);
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
        for (Vector2 segmento : cuerpoSerpiente) {
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
        // Fondo del HUD
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(COLOR_HUD);
        shapeRenderer.rect(0, ALTO_AREA_JUEGO, ANCHO_PANTALLA, ALTURA_HUD);

        // Línea separadora
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rectLine(0, ALTO_AREA_JUEGO, ANCHO_PANTALLA, ALTO_AREA_JUEGO, 3);
        shapeRenderer.end();

        // Texto del HUD
        batch.begin();

        fuenteHUD.setColor(Color.WHITE);

        // Manzanas (izquierda)
        String textoManzanas = "Manzanas: " + manzanasObtenidas;
        fuenteHUD.draw(batch, textoManzanas, 20, ALTO_PANTALLA - 18);

        // Longitud (centro)
        String textoLongitud = "Longitud: " + cuerpoSerpiente.size();
        layout.setText(fuenteHUD, textoLongitud);
        fuenteHUD.draw(batch, textoLongitud, (ANCHO_PANTALLA - layout.width) / 2, ALTO_PANTALLA - 18);

        // Tiempo (derecha)
        int minutos = (int)(tiempoJuego / 60);
        int segundos = (int)(tiempoJuego % 60);
        String textoTiempo = String.format("Tiempo: %02d:%02d", minutos, segundos);
        layout.setText(fuenteHUD, textoTiempo);
        fuenteHUD.draw(batch, textoTiempo, ANCHO_PANTALLA - layout.width - 20, ALTO_PANTALLA - 18);

        batch.end();
    }

    private void dibujarMensajes() {
        float centroX = ANCHO_PANTALLA / 2f;
        float centroY = ALTO_AREA_JUEGO / 2f;

        // Fondo semi-transparente
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.8f);
        shapeRenderer.rect(0, 0, ANCHO_PANTALLA, ALTO_AREA_JUEGO);
        shapeRenderer.end();

        batch.begin();

        if (pausado) {
            fuenteGameOver.setColor(Color.YELLOW);
            String textoPausa = "PAUSADO";
            layout.setText(fuenteGameOver, textoPausa);
            fuenteGameOver.draw(batch, textoPausa, centroX - layout.width / 2, centroY + 50);

            fuente.setColor(Color.WHITE);
            String textoInstruccion = "Presiona P o ESC para continuar";
            layout.setText(fuente, textoInstruccion);
            fuente.draw(batch, textoInstruccion, centroX - layout.width / 2, centroY - 20);
        }

        if (juegoTerminado) {
            fuenteGameOver.setColor(Color.RED);
            String textoGameOver = "GAME OVER";
            layout.setText(fuenteGameOver, textoGameOver);
            fuenteGameOver.draw(batch, textoGameOver, centroX - layout.width / 2, centroY + 100);

            fuenteHUD.setColor(Color.WHITE);
            String textoManzanas = "Manzanas obtenidas: " + manzanasObtenidas;
            layout.setText(fuenteHUD, textoManzanas);
            fuenteHUD.draw(batch, textoManzanas, centroX - layout.width / 2, centroY + 30);

            int minutos = (int)(tiempoJuego / 60);
            int segundos = (int)(tiempoJuego % 60);
            String textoTiempo = String.format("Tiempo: %02d:%02d", minutos, segundos);
            layout.setText(fuenteHUD, textoTiempo);
            fuenteHUD.draw(batch, textoTiempo, centroX - layout.width / 2, centroY - 10);

            fuente.setColor(Color.LIGHT_GRAY);
            String textoReiniciar = "Presiona R para reiniciar";
            layout.setText(fuente, textoReiniciar);
            fuente.draw(batch, textoReiniciar, centroX - layout.width / 2, centroY - 60);

            String textoMenu = "ESC para volver al menu";
            layout.setText(fuente, textoMenu);
            fuente.draw(batch, textoMenu, centroX - layout.width / 2, centroY - 90);
        }

        batch.end();
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {
        camara.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {
        pausado = true;
    }

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
    }
}
