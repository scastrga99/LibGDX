package com.samue.game.pyramid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PyramidRenderer {
    private final ShapeRenderer shapes;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final GlyphLayout glyph = new GlyphLayout();

    public PyramidRenderer(ShapeRenderer shapes, SpriteBatch batch, BitmapFont font) {
        this.shapes = shapes;
        this.batch = batch;
        this.font = font;
    }

    public void render(OrthographicCamera camera, PyramidBoard board, PyramidLayout layout, Selection sel) {
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        for (int r = 0; r < PyramidBoard.ROWS; r++) {
            int cols = r + 1;
            float y = layout.rowY(r);
            float startX = layout.rowStartX(r);

            for (int c = 0; c < cols; c++) {
                float x = startX + c * (layout.cardW + layout.gapX);
                var card = board.get(r, c);
                if (card == null) continue;

                boolean exposed = board.isExposed(r, c);
                shapes.setColor(exposed ? Color.valueOf("#FFFFFF") : Color.valueOf("#E8E8E8"));
                shapes.rect(x, y, layout.cardW, layout.cardH);

                shapes.setColor(Color.valueOf("#222222"));
                float t = 2f;
                shapes.rectLine(x, y, x + layout.cardW, y, t);
                shapes.rectLine(x, y, x, y + layout.cardH, t);
                shapes.rectLine(x + layout.cardW, y, x + layout.cardW, y + layout.cardH, t);
                shapes.rectLine(x, y + layout.cardH, x + layout.cardW, y + layout.cardH, t);

                if (sel.r == r && sel.c == c) {
                    shapes.setColor(Color.YELLOW);
                    float ht = 3f;
                    shapes.rectLine(x - 1, y - 1, x + layout.cardW + 1, y - 1, ht);
                    shapes.rectLine(x - 1, y - 1, x - 1, y + layout.cardH + 1, ht);
                    shapes.rectLine(x + layout.cardW + 1, y - 1, x + layout.cardW + 1, y + layout.cardH + 1, ht);
                    shapes.rectLine(x - 1, y + layout.cardH + 1, x + layout.cardW + 1, y + layout.cardH + 1, ht);
                }
            }
        }

        // Stock
        shapes.setColor(board.stock.isEmpty() ? Color.valueOf("#555555") : Color.valueOf("#CCCCCC"));
        shapes.rect(layout.stockBounds.x, layout.stockBounds.y, layout.stockBounds.width, layout.stockBounds.height);
        shapes.setColor(Color.valueOf("#222222"));
        drawRectBorder(layout.stockBounds.x, layout.stockBounds.y, layout.cardW, layout.cardH, 2f);

        // Waste
        shapes.setColor(board.topWaste() == null ? Color.valueOf("#555555") : Color.valueOf("#FFFFFF"));
        shapes.rect(layout.wasteBounds.x, layout.wasteBounds.y, layout.wasteBounds.width, layout.wasteBounds.height);
        shapes.setColor(sel.waste ? Color.YELLOW : Color.valueOf("#222222"));
        float wt = sel.waste ? 3f : 2f;
        drawRectBorder(layout.wasteBounds.x, layout.wasteBounds.y, layout.cardW, layout.cardH, wt);

        // Discard
        shapes.setColor(board.discard.isEmpty() ? Color.valueOf("#555555") : Color.valueOf("#DDDDDD"));
        shapes.rect(layout.discardBounds.x, layout.discardBounds.y, layout.discardBounds.width, layout.discardBounds.height);
        shapes.setColor(Color.valueOf("#222222"));
        drawRectBorder(layout.discardBounds.x, layout.discardBounds.y, layout.cardW, layout.cardH, 2f);

        // Nuevo: botón Reiniciar (arriba-derecha)
        shapes.setColor(Color.valueOf("#CCCCCC"));
        shapes.rect(layout.restartBounds.x, layout.restartBounds.y, layout.restartBounds.width, layout.restartBounds.height);
        shapes.setColor(Color.valueOf("#222222"));
        drawRectBorder(layout.restartBounds.x, layout.restartBounds.y, layout.restartBounds.width, layout.restartBounds.height, 2f);


        shapes.end();

        // Textos
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int r = 0; r < PyramidBoard.ROWS; r++) {
            int cols = r + 1;
            float y = layout.rowY(r);
            float startX = layout.rowStartX(r);

            for (int c = 0; c < cols; c++) {
                float x = startX + c * (layout.cardW + layout.gapX);
                var card = board.get(r, c);
                if (card == null) continue;
                String label = card.rank.label() + card.suit.symbol();
                font.setColor(card.suit.isRed() ? Color.RED : Color.BLACK);
                font.draw(batch, label, x + 8f, y + layout.cardH - 8f);
            }
        }
        if (board.topWaste() != null) {
            var top = board.topWaste();
            String label = top.rank.label() + top.suit.symbol();
            font.setColor(top.suit.isRed() ? Color.RED : Color.BLACK);
            font.draw(batch, label, layout.wasteBounds.x + 8f, layout.wasteBounds.y + layout.wasteBounds.height - 8f);
        }
        font.setColor(Color.BLACK);
        font.draw(batch, String.valueOf(board.stock.size()),
                layout.stockBounds.x + layout.stockBounds.width * 0.5f - 6f,
                layout.stockBounds.y + layout.stockBounds.height * 0.5f);
        font.draw(batch, String.valueOf(board.waste.size()),
                layout.wasteBounds.x + layout.wasteBounds.width * 0.5f - 6f,
                layout.wasteBounds.y + layout.wasteBounds.height * 0.5f);
        font.draw(batch, String.valueOf(board.discard.size()),
                layout.discardBounds.x + layout.discardBounds.width * 0.5f - 6f,
                layout.discardBounds.y + layout.discardBounds.height * 0.5f);

    // Nuevo: etiqueta del botón (centrada)
    String restartText = "Reiniciar";
    font.setColor(Color.BLACK);
    glyph.setText(font, restartText);
    float tx = layout.restartBounds.x + (layout.restartBounds.width - glyph.width) * 0.5f;
    float ty = layout.restartBounds.y + (layout.restartBounds.height + glyph.height) * 0.5f - 4f; // ajuste fino
    font.draw(batch, glyph, tx, ty);
        batch.end();
    }


    private void drawRectBorder(float x, float y, float w, float h, float t) {
        shapes.rectLine(x, y, x + w, y, t);
        shapes.rectLine(x, y, x, y + h, t);
        shapes.rectLine(x + w, y, x + w, y + h, t);
        shapes.rectLine(x, y + h, x + w, y + h, t);
    }
}