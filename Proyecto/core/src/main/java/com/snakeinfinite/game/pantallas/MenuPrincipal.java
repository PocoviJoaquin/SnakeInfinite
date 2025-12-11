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

public class MenuPrincipal implements Screen {

    private Principal juego;
    private Stage stage;
    private BitmapFont fuente;
    private GlyphLayout layout;

    // Items del menú
    private Label titulo;
    private Label jugar;
    private Label opciones;
    private Label salir;

    private Label[] items;   // Para navegación circular
    private int seleccion = 0;

    public MenuPrincipal(Principal juego) {
        this.juego = juego;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        fuente = new BitmapFont();
        layout = new GlyphLayout();

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fuente;
        estilo.fontColor = Color.WHITE;

        // ----- TÍTULO -----
        titulo = new Label("MENU PRINCIPAL", estilo);
        titulo.setFontScale(3.5f);
        stage.addActor(titulo);

        // ----- OPCIONES -----
        jugar = new Label("JUGAR", estilo);
        jugar.setFontScale(2.5f);

        opciones = new Label("OPCIONES", estilo);
        opciones.setFontScale(2.5f);

        salir = new Label("SALIR", estilo);
        salir.setFontScale(2.5f);

        // Guardar para navegación circular
        items = new Label[]{jugar, opciones, salir};

        stage.addActor(jugar);
        stage.addActor(opciones);
        stage.addActor(salir);

        // Posicionar elementos centralizados
        posicionarElementos();

        actualizarColores();
    }

    private void posicionarElementos() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Calcular ancho del título
        layout.setText(titulo.getStyle().font, titulo.getText().toString());
        float tituloWidth = layout.width * titulo.getFontScaleX();
        titulo.setPosition((screenWidth - tituloWidth) / 2, screenHeight * 0.7f);

        // Calcular espaciado entre opciones
        float espacioVertical = 80;
        float centroY = screenHeight * 0.45f;

        // Posicionar cada item centrado
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            seleccion = (seleccion - 1 + items.length) % items.length;
            ManejoDeAudio.reproducirSonido("sonido_seleccion");
            actualizarColores();
        }

        // Mover abajo
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            seleccion = (seleccion + 1) % items.length;
            ManejoDeAudio.reproducirSonido("sonido_seleccion");
            actualizarColores();
        }

        // Enter para seleccionar
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            ManejoDeAudio.reproducirSonido("sonido_seleccion");

            switch (seleccion) {
                case 0:
                    juego.setScreen(new MenuJugar(juego));
                    break;

                case 1:
                    System.out.println("Opciones (falta implementar)");
                    break;

                case 2:
                    Gdx.app.exit();
                    break;
            }
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
        posicionarElementos(); // Recalcular posiciones al cambiar tamaño
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