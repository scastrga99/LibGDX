package com.samue.game.pyramid;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PyramidLayout {
    public final float baseCardW, baseCardH, baseGapX, baseGapY;
    public final float marginTop, marginBottom, marginSides;
    public final float bottomUIHeight;

    public float worldW, worldH;
    public float cardW, cardH, gapX, gapY;

    public final Rectangle stockBounds = new Rectangle();
    public final Rectangle wasteBounds = new Rectangle();
    public final Rectangle discardBounds = new Rectangle();
    // Nuevo: botón de reinicio (arriba-derecha)
    public final Rectangle restartBounds = new Rectangle();

    public PyramidLayout(float baseCardW, float baseCardH, float baseGapX, float baseGapY,
                         float marginTop, float marginBottom, float marginSides) {
        this.baseCardW = baseCardW;
        this.baseCardH = baseCardH;
        this.baseGapX = baseGapX;
        this.baseGapY = baseGapY;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        this.marginSides = marginSides;
        this.bottomUIHeight = baseCardH + 2f * baseGapY;
    }

    public float designWidth() {
        int rows = PyramidBoard.ROWS;
        return rows * baseCardW + (rows - 1) * baseGapX + 2f * marginSides;
    }

    public float designHeight() {
        int rows = PyramidBoard.ROWS;
        return rows * baseCardH + (rows - 1) * baseGapY + marginTop + marginBottom + bottomUIHeight;
    }

    public void update(Viewport viewport) {
        viewport.apply();
        worldW = viewport.getWorldWidth();
        worldH = viewport.getWorldHeight();

        float scale = 1f; // FitViewport fija la escala
        cardW = baseCardW * scale;
        cardH = baseCardH * scale;
        gapX  = baseGapX  * scale;
        gapY  = baseGapY  * scale;

        updateBottomAreas();
        // Nuevo: actualizar botón reinicio
        updateRestartArea();
    }

    public float rowStartX(int r) {
        int cols = r + 1;
        float rowWidth = cols * cardW + (cols - 1) * gapX;
        return (worldW - rowWidth) * 0.5f;
    }

    public float rowY(int r) {
        float topY = worldH - marginTop - cardH;
        return topY - r * (cardH + gapY);
    }

    private void updateBottomAreas() {
        float y = marginBottom;
        stockBounds.set(marginSides, y, cardW, cardH);
        wasteBounds.set((worldW - cardW) * 0.5f, y, cardW, cardH);
        discardBounds.set(worldW - marginSides - cardW, y, cardW, cardH);
    }

    // Nuevo: calcula el área del botón de reinicio
    private void updateRestartArea() {
        // Queremos un botón consistente que quepa el texto "Reiniciar" (~8-9 chars)
        // Usamos un ancho mínimo fijo basado en el ancho de carta pero con límite mínimo absoluto.
        float btnW = Math.max(120f, cardW * 1.2f);
        float btnH = Math.max(36f, cardH * 0.33f);
        // Padding a márgenes: 8 px respecto al borde superior y lateral derecho
        float padding = 8f;
        float x = worldW - padding - btnW;
        float y = worldH - padding - btnH;
        restartBounds.set(x, y, btnW, btnH);
    }

}