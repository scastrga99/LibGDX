package com.samue.game.pyramid;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PyramidInput extends InputAdapter {
    private final PyramidBoard board;
    private final PyramidLayout layout;
    private final Selection sel;
    private final Viewport viewport;
    private final Vector2 tmp = new Vector2();
    // Nuevo: acción de reinicio
    private final Runnable onRestart;

    public PyramidInput(PyramidBoard board, PyramidLayout layout, Selection sel, Viewport viewport, Runnable onRestart) {
        this.board = board;
        this.layout = layout;
        this.sel = sel;
        this.viewport = viewport;
        this.onRestart = onRestart;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        viewport.unproject(tmp.set(screenX, screenY));

        // Nuevo: botón reiniciar
        if (layout.restartBounds.contains(tmp)) {
            if (onRestart != null) onRestart.run();
            return true;
        }

        // Stock
        if (layout.stockBounds.contains(tmp)) {
            if (!board.stock.isEmpty()) {
                board.drawFromStock();
            } else if (!board.waste.isEmpty()) {
                board.resetWasteToStock();
            }
            sel.clear();
            return true;
        }

        // Waste
        if (layout.wasteBounds.contains(tmp)) {
            if (board.topWaste() == null) { sel.clear(); return true; }
            if (board.topWaste().value() == 13) {
                board.discardWasteTop();
                sel.clear();
                return true;
            }
            if (sel.r >= 0) {
                var a = board.get(sel.r, sel.c);
                var b = board.topWaste();
                if (a != null && board.isExposed(sel.r, sel.c) && a.value() + b.value() == 13) {
                    board.removeAndDiscardAt(sel.r, sel.c);
                    board.discardWasteTop();
                    sel.clear();
                    return true;
                }
            }
            sel.waste = !sel.waste;
            sel.r = sel.c = -1;
            return true;
        }

        // Pirámide
        for (int r = 0; r < PyramidBoard.ROWS; r++) {
            int cols = r + 1;
            float y = layout.rowY(r);
            float startX = layout.rowStartX(r);
            for (int c = 0; c < cols; c++) {
                float x = startX + c * (layout.cardW + layout.gapX);
                if (tmp.x >= x && tmp.x <= x + layout.cardW && tmp.y >= y && tmp.y <= y + layout.cardH) {
                    if (!board.isExposed(r, c)) return true;
                    var card = board.get(r, c);
                    if (card == null) return true;

                    if (card.value() == 13) {
                        board.removeAndDiscardAt(r, c);
                        sel.clear();
                        return true;
                    }
                    if (sel.waste && board.topWaste() != null) {
                        if (card.value() + board.topWaste().value() == 13) {
                            board.removeAndDiscardAt(r, c);
                            board.discardWasteTop();
                            sel.clear();
                            return true;
                        }
                    }
                    if (sel.r >= 0) {
                        var other = board.get(sel.r, sel.c);
                        if (other != null && board.isExposed(sel.r, sel.c) && other.value() + card.value() == 13) {
                            board.removeAndDiscardAt(sel.r, sel.c);
                            board.removeAndDiscardAt(r, c);
                            sel.clear();
                            return true;
                        }
                    }
                    sel.r = r; sel.c = c; sel.waste = false;
                    return true;
                }
            }
        }
        return false;
    }
}