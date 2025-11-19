package com.snakeinfinite.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.snakeinfinite.game.utiles.ManejoDeAudio;
import com.snakeinfinite.game.utiles.Principal;

public class MenuPrincipal implements Screen {

    private Principal juego;
    private Stage stage;
    private BitmapFont fuente;

    // Items del menú
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

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fuente;
        estilo.fontColor = Color.WHITE;

        // ----- TÍTULO -----
        Label titulo = new Label("MENU PRINCIPAL", estilo);
        titulo.setFontScale(2);
        titulo.setPosition(100, 400);
        stage.addActor(titulo);

        // ----- OPCIONES -----
        jugar = new Label("JUGAR", estilo);
        jugar.setFontScale(1.4f);
        jugar.setPosition(100, 300);

        opciones = new Label("OPCIONES", estilo);
        opciones.setFontScale(1.4f);
        opciones.setPosition(100, 240);

        salir = new Label("SALIR", estilo);
        salir.setFontScale(1.4f);
        salir.setPosition(100, 180);

        // Guardar para navegación circular
        items = new Label[]{jugar, opciones, salir};

        stage.addActor(jugar);
        stage.addActor(opciones);
        stage.addActor(salir);

        actualizarColores();
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

    @Override public void resize(int w, int h) {}
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
