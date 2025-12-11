package com.snakeinfinite.game.mundo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Clase que maneja el mapa infinito y la generación dinámica de manzanas
 */
public class MundoInfinito {

    // Configuración del mundo
    private static final int TAMAÑO_CELDA = 20;
    private static final float RADIO_PANTALLA = 20; // tiles de radio visible

    // Colores del césped
    private static final Color COLOR_CESPED = new Color(0.2f, 0.6f, 0.2f, 1f);
    private static final Color COLOR_CESPED_CLARO = new Color(0.25f, 0.65f, 0.25f, 1f);

    // Una única manzana visible
    private Vector2 manzanaActual;

    // Textura de la manzana
    private Texture texturaManzana;

    public MundoInfinito() {
        this.manzanaActual = null;

        // Cargar textura de la manzana
        try {
            this.texturaManzana = new Texture(Gdx.files.internal("imagenes/manzana.png"));
            System.out.println("Textura de manzana cargada exitosamente");
        } catch (Exception e) {
            System.err.println("Error al cargar textura de manzana: " + e.getMessage());
            this.texturaManzana = null;
        }
    }

    /**
     * Inicializa el mundo generando la primera manzana
     */
    public void inicializar(Vector2 posicionInicial) {
        // Generar la primera manzana cerca de la serpiente
        generarNuevaManzana(posicionInicial, null);

        System.out.println("Mundo infinito inicializado");
        System.out.println("Primera manzana en: " + manzanaActual);
    }

    /**
     * Actualiza el mundo: verifica si la manzana está fuera del radio visible
     */
    public void actualizar(Vector2 posicionSerpiente, ArrayList<Vector2> cuerpoSerpiente) {
        // Si no hay manzana, generar una nueva
        if (manzanaActual == null) {
            generarNuevaManzana(posicionSerpiente, cuerpoSerpiente);
            return;
        }

        // Verificar si la manzana está fuera del radio visible
        float distancia = posicionSerpiente.dst(manzanaActual);

        if (distancia > RADIO_PANTALLA) {
            System.out.println("Manzana fuera del radio visible. Generando nueva...");
            generarNuevaManzana(posicionSerpiente, cuerpoSerpiente);
        }
    }

    /**
     * Genera una nueva manzana dentro del radio visible de la serpiente
     */
    private void generarNuevaManzana(Vector2 posicionReferencia, ArrayList<Vector2> cuerpoSerpiente) {
        int intentos = 0;
        Vector2 nuevaManzana;

        do {
            // Generar dentro del radio visible (entre 5 y 15 tiles de distancia)
            int distanciaMin = 5;
            int distanciaMax = 15;

            // Generar ángulo aleatorio
            float angulo = MathUtils.random(0f, MathUtils.PI2);
            float distancia = MathUtils.random(distanciaMin, distanciaMax);

            // Convertir a coordenadas cartesianas
            int offsetX = (int)(Math.cos(angulo) * distancia);
            int offsetY = (int)(Math.sin(angulo) * distancia);

            nuevaManzana = new Vector2(
                    (int)posicionReferencia.x + offsetX,
                    (int)posicionReferencia.y + offsetY
            );

            intentos++;
            if (intentos > 100) {
                // Si no se encuentra posición válida, poner algo cerca
                nuevaManzana = new Vector2(
                        (int)posicionReferencia.x + 10,
                        (int)posicionReferencia.y + 10
                );
                break;
            }

        } while (cuerpoSerpiente != null && colisionConSerpiente(nuevaManzana, cuerpoSerpiente));

        manzanaActual = nuevaManzana;
        System.out.println("Nueva manzana generada en: (" + (int)nuevaManzana.x + ", " + (int)nuevaManzana.y + ")");
    }

    /**
     * Verifica si hay colisión con el cuerpo de la serpiente
     */
    private boolean colisionConSerpiente(Vector2 posicion, ArrayList<Vector2> cuerpoSerpiente) {
        for (Vector2 segmento : cuerpoSerpiente) {
            if (segmento.equals(posicion)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si la serpiente comió la manzana y genera una nueva
     * @return true si comió la manzana
     */
    public boolean verificarColisionManzana(Vector2 posicionCabeza, ArrayList<Vector2> cuerpoSerpiente) {
        if (manzanaActual != null && posicionCabeza.equals(manzanaActual)) {
            System.out.println("¡Manzana comida!");
            // Generar nueva manzana inmediatamente
            generarNuevaManzana(posicionCabeza, cuerpoSerpiente);
            return true;
        }
        return false;
    }

    /**
     * Dibuja el patrón de césped infinito alrededor de la serpiente
     */
    public void dibujarCesped(ShapeRenderer shapeRenderer, Vector2 posicionSerpiente,
                              int anchoPantalla, int altoPantalla) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Calcular el rango de tiles visibles
        int tilesVisiblesX = (anchoPantalla / TAMAÑO_CELDA) + 2;
        int tilesVisiblesY = (altoPantalla / TAMAÑO_CELDA) + 2;

        int startX = (int)posicionSerpiente.x - tilesVisiblesX / 2;
        int startY = (int)posicionSerpiente.y - tilesVisiblesY / 2;

        for (int x = startX; x < startX + tilesVisiblesX; x++) {
            for (int y = startY; y < startY + tilesVisiblesY; y++) {
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

    /**
     * Dibuja la manzana actual usando el sprite o un cuadrado de respaldo
     */
    public void dibujarManzanas(SpriteBatch batch) {
        if (manzanaActual == null) return;

        batch.begin();

        if (texturaManzana != null) {
            // Dibujar sprite de la manzana
            batch.draw(
                    texturaManzana,
                    manzanaActual.x * TAMAÑO_CELDA,
                    manzanaActual.y * TAMAÑO_CELDA,
                    TAMAÑO_CELDA,
                    TAMAÑO_CELDA
            );
        } else {
            // Si no hay textura, no podemos dibujar con batch
            // Este caso se manejará con ShapeRenderer en PantallaJuego
        }

        batch.end();
    }

    /**
     * Dibuja la manzana con ShapeRenderer (respaldo si no hay textura)
     */
    public void dibujarManzanasRespaldo(ShapeRenderer shapeRenderer) {
        if (manzanaActual == null || texturaManzana != null) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Manzana roja
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(
                manzanaActual.x * TAMAÑO_CELDA,
                manzanaActual.y * TAMAÑO_CELDA,
                TAMAÑO_CELDA,
                TAMAÑO_CELDA
        );

        // Brillo
        shapeRenderer.setColor(1, 1, 1, 0.5f);
        shapeRenderer.rect(
                manzanaActual.x * TAMAÑO_CELDA + 3,
                manzanaActual.y * TAMAÑO_CELDA + TAMAÑO_CELDA - 6,
                5,
                4
        );

        shapeRenderer.end();
    }

    /**
     * Libera los recursos
     */
    public void dispose() {
        if (texturaManzana != null) {
            texturaManzana.dispose();
        }
    }

    // Getters
    public Vector2 obtenerManzanaActual() {
        return manzanaActual;
    }

    public static int obtenerTamañoCelda() {
        return TAMAÑO_CELDA;
    }
}