package com.samue.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import com.samue.game.cards.Deck;
import com.samue.game.pyramid.PyramidBoard;
import com.samue.game.pyramid.PyramidLayout;
import com.samue.game.pyramid.PyramidRenderer;
import com.samue.game.pyramid.PyramidInput;
import com.samue.game.pyramid.Selection;

public class MyGdxGame extends ApplicationAdapter {
    private final Color bg = Color.valueOf("#310e58cc");

    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private BitmapFont font;

    private PyramidBoard board;
    private OrthographicCamera camera;
    private Viewport viewport;

    private PyramidLayout layout;
    private PyramidRenderer renderer;
    private Selection selection;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DejaVuSans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = Math.round(96f * 0.28f);
        p.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "♣♦♥♠";
        font = gen.generateFont(p);
        gen.dispose();

        layout = new PyramidLayout(64f, 96f, 8f, 8f, 32f, 32f, 32f);

        camera = new OrthographicCamera();
        viewport = new FitViewport(layout.designWidth(), layout.designHeight(), camera);
        viewport.apply(true);

        renderer = new PyramidRenderer(shapes, batch, font);

        board = new PyramidBoard();
        selection = new Selection();
        // Inicia primera partida
        startNewGame();

        // Input con callback de reinicio
        Gdx.input.setInputProcessor(new PyramidInput(board, layout, selection, viewport, this::startNewGame));
    }

    // Nuevo: reinicia el estado de la partida
    private void startNewGame() {
        Deck deck = new Deck();
        board.dealNewGame(deck, System.currentTimeMillis());
        if (selection != null) selection.clear();
    }

    @Override
    public void render() {
        layout.update(viewport);

        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render(camera, board, layout, selection);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapes != null) shapes.dispose();
        if (font != null) font.dispose();
    }
}