package com.snakeinfinite.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.snakeinfinite.game.utiles.ManejoDeAudio;
import com.snakeinfinite.game.utiles.Principal;

public class MenuJugar implements Screen {

    private Principal juego;
    private Stage stage;
    private BitmapFont fuente;
    private GlyphLayout layout;

    // Items del menú
    private Label titulo;
    private Label unJugador;
    private Label multijugador;
    private Label volver;

    private Label[] items;
    private int seleccion = 0;

    public MenuJugar(Principal juego) {
        this.juego = juego;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        fuente = new BitmapFont();
        layout = new GlyphLayout();

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fuente;
        estilo.fontColor = Color.WHITE;

        // ----- TÍTULO -----
        titulo = new Label("SELECCIONA MODO", estilo);
        titulo.setFontScale(3.5f);
        stage.addActor(titulo);

        // ----- OPCIONES -----
        unJugador = new Label("UN JUGADOR", estilo);
        unJugador.setFontScale(2.5f);

        multijugador = new Label("MULTIJUGADOR", estilo);
        multijugador.setFontScale(2.5f);

        volver = new Label("VOLVER", estilo);
        volver.setFontScale(2.5f);

        // Guardar para navegación
        items = new Label[]{unJugador, multijugador, volver};

        stage.addActor(unJugador);
        stage.addActor(multijugador);
        stage.addActor(volver);

        // Posicionar elementos
        posicionarElementos();
        actualizarColores();
    }

    private void posicionarElementos() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Posicionar título centrado
        layout.setText(titulo.getStyle().font, titulo.getText().toString());
        float tituloWidth = layout.width * titulo.getFontScaleX();
        titulo.setPosition((screenWidth - tituloWidth) / 2, screenHeight * 0.7f);

        // Posicionar opciones centradas
        float espacioVertical = 80;
        float centroY = screenHeight * 0.45f;

        for (int i = 0; i < items.length; i++) {
            layout.setText(items[i].getStyle().font, items[i].getText().toString());
            float itemWidth = layout.width * items[i].getFontScaleX();
            float itemX = (screenWidth - itemWidth) / 2;
            float itemY = centroY - (i * espacioVertical);
            items[i].setPosition(itemX, itemY);
        }
    }

    private void actualizarColores() {
        for (int i = 0; i < items.length; i++) {
            if (i == seleccion)
                items[i].setColor(Color.YELLOW);
            else
                items[i].setColor(Color.WHITE);
        }
    }

    private void manejarInput() {
        // Mover arriba
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            seleccion = (seleccion - 1 + items.length) % items.length;
            ManejoDeAudio.reproducirSonido("sonido_seleccion");
            actualizarColores();
        }

        // Mover abajo
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            seleccion = (seleccion + 1) % items.length;
            ManejoDeAudio.reproducirSonido("sonido_seleccion");
            actualizarColores();
        }

        // Enter o Espacio para seleccionar
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ManejoDeAudio.reproducirSonido("sonido_seleccion");

            switch (seleccion) {
                case 0: // Un Jugador
                    juego.setScreen(new PantallaJuego(juego));
                    dispose();
                    break;

                 case 1: // Multijugador
                    juego.setScreen(new PantallaJuegoMultijugador(juego));
                    dispose();
                    break;

                case 2: // Volver
                    juego.setScreen(new MenuPrincipal(juego));
                    dispose();
                    break;
            }
        }

        // ESC para volver
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ManejoDeAudio.reproducirSonido("sonido_seleccion");
            juego.setScreen(new MenuPrincipal(juego));
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        manejarInput();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
        posicionarElementos();
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        fuente.dispose();
    }
}
