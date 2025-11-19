package com.snakeinfinite.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.snakeinfinite.game.utiles.ManejoDeAudio;
import com.snakeinfinite.game.utiles.Principal;

public class MenuJugar implements Screen {

    private Principal juego;
    private SpriteBatch batch;
    private BitmapFont fuente;
    private BitmapFont fuenteTitulo;
    private GlyphLayout layout;
    private Texture fondoTexture;

    // Botones
    private Rectangle botonUnJugador;
    private Rectangle botonMultijugador;
    private Rectangle botonVolver;

    // Dimensiones de pantalla
    private static final int ANCHO_PANTALLA = 800;
    private static final int ALTO_PANTALLA = 600;

    // Dimensiones de botones
    private static final int ANCHO_BOTON = 350;
    private static final int ALTO_BOTON = 70;
    private static final int ESPACIO_ENTRE_BOTONES = 30;

    // Posiciones de botones (centradas)
    private static final int X_BOTONES = (ANCHO_PANTALLA - ANCHO_BOTON) / 2;
    private static final int Y_PRIMER_BOTON = 350;

    // Índice de selección con teclado
    private int opcionSeleccionada = 0;
    private static final int TOTAL_OPCIONES = 3;

    // Colores
    private Color colorNormal = new Color(0.7f, 0.7f, 0.7f, 1);
    private Color colorSeleccionado = new Color(1f, 0.9f, 0.2f, 1); // Amarillo dorado
    private Color colorHover = Color.GOLD;

    public MenuJugar(Principal juego) {
        this.juego = juego;
        this.batch = new SpriteBatch();

        // Fuentes
        this.fuente = new BitmapFont();
        this.fuente.getData().setScale(2f);

        this.fuenteTitulo = new BitmapFont();
        this.fuenteTitulo.getData().setScale(3.5f);

        this.layout = new GlyphLayout();

        // Cargar fondo (si existe)
        try {
            fondoTexture = new Texture(Gdx.files.internal("imagenes/fondo_menu.png"));
        } catch (Exception e) {
            fondoTexture = null; // Si no hay fondo, usaremos color sólido
        }

        // Crear botones
        int yActual = Y_PRIMER_BOTON;
        botonUnJugador = new Rectangle(X_BOTONES, yActual, ANCHO_BOTON, ALTO_BOTON);

        yActual -= (ALTO_BOTON + ESPACIO_ENTRE_BOTONES);
        botonMultijugador = new Rectangle(X_BOTONES, yActual, ANCHO_BOTON, ALTO_BOTON);

        yActual -= (ALTO_BOTON + ESPACIO_ENTRE_BOTONES);
        botonVolver = new Rectangle(X_BOTONES, yActual, ANCHO_BOTON, ALTO_BOTON);
    }

    @Override
    public void render(float delta) {
        manejarEntrada();
        dibujar();
    }

    private void manejarEntrada() {
        // Navegación con teclado
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            opcionSeleccionada--;
            if (opcionSeleccionada < 0) {
                opcionSeleccionada = TOTAL_OPCIONES - 1;
            }
            ManejoDeAudio.reproducirSonido("sonido_seleccion");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            opcionSeleccionada++;
            if (opcionSeleccionada >= TOTAL_OPCIONES) {
                opcionSeleccionada = 0;
            }
            ManejoDeAudio.reproducirSonido("sonido_seleccion");
        }

        // Confirmar selección con Enter o Espacio
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ejecutarOpcion(opcionSeleccionada);
        }

        // ESC para volver
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ManejoDeAudio.reproducirSonido("sonido_seleccion");
            juego.setScreen(new MenuPrincipal(juego));
            dispose();
        }

        // Detección de clic con mouse
        if (Gdx.input.justTouched()) {
            int mouseX = Gdx.input.getX();
            int mouseY = ALTO_PANTALLA - Gdx.input.getY(); // Invertir Y

            if (botonUnJugador.contains(mouseX, mouseY)) {
                ejecutarOpcion(0);
            } else if (botonMultijugador.contains(mouseX, mouseY)) {
                ejecutarOpcion(1);
            } else if (botonVolver.contains(mouseX, mouseY)) {
                ejecutarOpcion(2);
            }
        }

        // Detección de hover con mouse (para cambiar color)
        int mouseX = Gdx.input.getX();
        int mouseY = ALTO_PANTALLA - Gdx.input.getY();

        if (botonUnJugador.contains(mouseX, mouseY)) {
            opcionSeleccionada = 0;
        } else if (botonMultijugador.contains(mouseX, mouseY)) {
            opcionSeleccionada = 1;
        } else if (botonVolver.contains(mouseX, mouseY)) {
            opcionSeleccionada = 2;
        }
    }

    private void ejecutarOpcion(int opcion) {
        ManejoDeAudio.reproducirSonido("sonido_seleccion");

        switch (opcion) {
            case 0: // Un Jugador
                juego.setScreen(new PantallaJuego(juego));
                dispose();
                break;
            case 1: // Multijugador
                System.out.println("Multijugador - Próximamente");
                // TODO: juego.setScreen(new PantallaMultijugador(juego));
                break;
            case 2: // Volver
                juego.setScreen(new MenuPrincipal(juego));
                dispose();
                break;
        }
    }

    private void dibujar() {
        // Limpiar pantalla con color de fondo oscuro
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Dibujar fondo si existe
        if (fondoTexture != null) {
            batch.draw(fondoTexture, 0, 0, ANCHO_PANTALLA, ALTO_PANTALLA);
        }

        // Título
        String titulo = "SELECCIONA MODO";
        layout.setText(fuenteTitulo, titulo);
        float xTitulo = (ANCHO_PANTALLA - layout.width) / 2;
        fuenteTitulo.setColor(Color.WHITE);
        fuenteTitulo.draw(batch, titulo, xTitulo, 500);

        // Dibujar botones
        dibujarBoton(batch, "UN JUGADOR", botonUnJugador, opcionSeleccionada == 0);
        dibujarBoton(batch, "MULTIJUGADOR", botonMultijugador, opcionSeleccionada == 1);
        dibujarBoton(batch, "VOLVER", botonVolver, opcionSeleccionada == 2);

        // Instrucciones en la parte inferior
        fuente.getData().setScale(1f);
        fuente.setColor(new Color(0.6f, 0.6f, 0.6f, 1));

        String instruccion1 = "Usa FLECHAS o MOUSE para navegar";
        layout.setText(fuente, instruccion1);
        fuente.draw(batch, instruccion1, (ANCHO_PANTALLA - layout.width) / 2, 80);

        String instruccion2 = "ENTER o CLIC para seleccionar";
        layout.setText(fuente, instruccion2);
        fuente.draw(batch, instruccion2, (ANCHO_PANTALLA - layout.width) / 2, 50);

        String instruccion3 = "ESC para volver";
        layout.setText(fuente, instruccion3);
        fuente.draw(batch, instruccion3, (ANCHO_PANTALLA - layout.width) / 2, 20);

        batch.end();
    }

    private void dibujarBoton(SpriteBatch batch, String texto, Rectangle boton, boolean seleccionado) {
        // Determinar color del texto
        Color colorTexto = seleccionado ? colorSeleccionado : colorNormal;
        fuente.getData().setScale(2f);
        fuente.setColor(colorTexto);

        // Calcular posición centrada del texto
        layout.setText(fuente, texto);
        float xTexto = boton.x + (boton.width - layout.width) / 2;
        float yTexto = boton.y + (boton.height + layout.height) / 2;

        // Indicadores de selección (flechas a los lados)
        if (seleccionado) {
            fuente.draw(batch, ">", boton.x - 50, yTexto);
            fuente.draw(batch, "<", boton.x + boton.width + 30, yTexto);
        }

        // Dibujar texto del botón
        fuente.draw(batch, texto, xTexto, yTexto);

        // Línea decorativa debajo del botón seleccionado
        if (seleccionado) {
            fuente.getData().setScale(1.5f);
            String linea = "_________________________________";
            layout.setText(fuente, linea);
            float xLinea = boton.x + (boton.width - layout.width) / 2;
            fuente.draw(batch, linea, xLinea, boton.y - 5);
        }
    }

    @Override
    public void show() {
        System.out.println("MenuJugar mostrado");
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        fuente.dispose();
        fuenteTitulo.dispose();
        if (fondoTexture != null) {
            fondoTexture.dispose();
        }
    }
}
