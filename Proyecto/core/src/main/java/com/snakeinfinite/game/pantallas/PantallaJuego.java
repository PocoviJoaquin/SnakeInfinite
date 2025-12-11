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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.snakeinfinite.game.mundo.MundoInfinito;
import com.snakeinfinite.game.pantallas.MenuPrincipal;
import com.snakeinfinite.game.utiles.ManejoDeAudio;
import com.snakeinfinite.game.utiles.Principal;

import java.util.ArrayList;

public class PantallaJuego implements Screen {

    private Principal juego;
    private OrthographicCamera camara;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont fuente;
    private BitmapFont fuenteHUD;
    private BitmapFont fuenteGameOver;
    private GlyphLayout layout;
    private Texture texCabeza;
    private Texture texCuerpo;


    // Configuración de pantalla
    private static final int ANCHO_PANTALLA = 800;
    private static final int ALTO_PANTALLA = 600;
    private static final int ALTURA_HUD = 60;
    private static final int ALTO_AREA_JUEGO = ALTO_PANTALLA - ALTURA_HUD;
    private static final int TAMAÑO_CELDA = 20;

    // Color del HUD
    private static final Color COLOR_HUD = new Color(0.1f, 0.1f, 0.15f, 1f);

    // Mundo infinito
    private MundoInfinito mundo;

    // Serpiente
    private ArrayList<Vector2> cuerpoSerpiente;
    private Vector2 direccion;
    private Vector2 direccionPendiente;
    private float tiempoMovimiento;
    private static final float VELOCIDAD = 0.15f;

    // Estado del juego
    private int manzanasObtenidas;
    private float tiempoJuego;
    private boolean juegoTerminado;
    private boolean pausado;

    public PantallaJuego(Principal juego) {
        this.juego = juego;

        // Inicializar cámara
        this.camara = new OrthographicCamera();
        this.viewport = new StretchViewport(ANCHO_PANTALLA, ALTO_PANTALLA, camara);
        this.viewport.apply(true);

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

        // Inicializar mundo infinito
        this.mundo = new MundoInfinito();

        texCabeza = new Texture("imagenes/serpiente_cabeza.png");
        texCuerpo = new Texture("imagenes/serpiente_cuerpo.png");


        inicializarJuego();
    }

    private void inicializarJuego() {
        // Crear serpiente (3 segmentos iniciales en el centro del mundo)
        cuerpoSerpiente = new ArrayList<>();
        cuerpoSerpiente.add(new Vector2(0, 0));
        cuerpoSerpiente.add(new Vector2(-1, 0));
        cuerpoSerpiente.add(new Vector2(-2, 0));

        // Dirección inicial (derecha)
        direccion = new Vector2(1, 0);
        direccionPendiente = new Vector2(1, 0);

        // Inicializar mundo con manzanas
        mundo.inicializar(cuerpoSerpiente.get(0));

        // Estado inicial
        manzanasObtenidas = 0;
        tiempoJuego = 0;
        tiempoMovimiento = 0;
        juegoTerminado = false;
        pausado = false;

        System.out.println("=== JUEGO INICIALIZADO (MAPA INFINITO) ===");
        System.out.println("Serpiente en: " + cuerpoSerpiente.get(0));
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

            // Actualizar mundo (generar/eliminar manzanas)
            mundo.actualizar(cuerpoSerpiente.get(0), cuerpoSerpiente);
        }

        // Actualizar posición de la cámara para seguir a la serpiente
        Vector2 cabeza = cuerpoSerpiente.get(0);
        camara.position.set(
                cabeza.x * TAMAÑO_CELDA + TAMAÑO_CELDA / 2f,
                cabeza.y * TAMAÑO_CELDA + TAMAÑO_CELDA / 2f + ALTURA_HUD / 2f,
                0
        );
    }

    private void moverSerpiente() {
        Vector2 cabeza = cuerpoSerpiente.get(0);
        Vector2 nuevaCabeza = new Vector2(
                cabeza.x + direccion.x,
                cabeza.y + direccion.y
        );

        // Verificar colisión con cuerpo
        if (colisionConSerpiente(nuevaCabeza)) {
            juegoTerminado = true;
            System.out.println("GAME OVER: Colisión con cuerpo");
            return;
        }

        // Agregar nueva cabeza
        cuerpoSerpiente.add(0, nuevaCabeza);

        // Verificar si comió manzana usando el mundo
        boolean comioManzana = mundo.verificarColisionManzana(nuevaCabeza, cuerpoSerpiente);

        if (comioManzana) {
            manzanasObtenidas++;

            // 🔊 reproducir sonido
            ManejoDeAudio.reproducirSonido("sonido_manzana");

            System.out.println("¡MANZANA COMIDA! Total: " + manzanasObtenidas +
                    " | Longitud: " + cuerpoSerpiente.size());
        }
        else {
            // Remover cola (movimiento normal)
            cuerpoSerpiente.remove(cuerpoSerpiente.size() - 1);
        }
    }

    private boolean colisionConSerpiente(Vector2 posicion) {
        for (Vector2 segmento : cuerpoSerpiente) {
            if (segmento.equals(posicion)) {
                return true;
            }
        }
        return false;
    }

    private void dibujar() {
        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Aplicar viewport
        viewport.apply();
        camara.update();
        shapeRenderer.setProjectionMatrix(camara.combined);
        batch.setProjectionMatrix(camara.combined);

        // 1. Dibujar césped infinito (delegado al mundo)
        mundo.dibujarCesped(shapeRenderer, cuerpoSerpiente.get(0), ANCHO_PANTALLA, ALTO_AREA_JUEGO);

        // 2. Dibujar serpiente
        dibujarSerpiente();

        // 3. Dibujar manzanas con sprite (delegado al mundo)
        mundo.dibujarManzanas(batch);

        // 4. Dibujar manzanas de respaldo si no hay textura
        mundo.dibujarManzanasRespaldo(shapeRenderer);

        // 5. Dibujar HUD
        dibujarHUD();

        // 6. Dibujar mensajes (pausa/game over)
        if (pausado || juegoTerminado) {
            dibujarMensajes();
        }
    }

    private void dibujarSerpiente() {

        // --- DIBUJO CON SPRITES ---
        batch.begin();

        for (int i = 0; i < cuerpoSerpiente.size(); i++) {

            Vector2 segmento = cuerpoSerpiente.get(i);

            float x = segmento.x * TAMAÑO_CELDA;
            float y = segmento.y * TAMAÑO_CELDA;

            if (i == 0) {
                // Cabeza
                batch.draw(
                        texCabeza,
                        x, y,
                        TAMAÑO_CELDA, TAMAÑO_CELDA
                );
            } else {
                // Cuerpo
                batch.draw(
                        texCuerpo,
                        x, y,
                        TAMAÑO_CELDA, TAMAÑO_CELDA
                );
            }
        }

        batch.end();
    }

    private void dibujarHUD() {
        // Crear cámara fija para el HUD
        OrthographicCamera camaraHUD = new OrthographicCamera();
        camaraHUD.setToOrtho(false, ANCHO_PANTALLA, ALTO_PANTALLA);
        camaraHUD.update();

        shapeRenderer.setProjectionMatrix(camaraHUD.combined);
        batch.setProjectionMatrix(camaraHUD.combined);

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

        // Restaurar cámara de juego
        shapeRenderer.setProjectionMatrix(camara.combined);
        batch.setProjectionMatrix(camara.combined);
    }

    private void dibujarMensajes() {
        // Usar cámara fija para mensajes
        OrthographicCamera camaraHUD = new OrthographicCamera();
        camaraHUD.setToOrtho(false, ANCHO_PANTALLA, ALTO_PANTALLA);
        camaraHUD.update();

        shapeRenderer.setProjectionMatrix(camaraHUD.combined);
        batch.setProjectionMatrix(camaraHUD.combined);

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

        // Restaurar cámara de juego
        shapeRenderer.setProjectionMatrix(camara.combined);
        batch.setProjectionMatrix(camara.combined);
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        mundo.dispose();
        texCabeza.dispose();
        texCuerpo.dispose();
// Liberar recursos del mundo
    }
}