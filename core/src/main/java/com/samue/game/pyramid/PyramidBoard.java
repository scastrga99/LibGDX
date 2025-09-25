package com.samue.game.pyramid;

import com.samue.game.cards.Card;
import com.samue.game.cards.Deck;

import java.util.ArrayList;
import java.util.List;

public class PyramidBoard {
    public static final int ROWS = 7;

    public final List<List<Card>> pyramid = new ArrayList<>(ROWS); // cartas en mesa; eliminadas = null
    public final List<Card> stock  = new ArrayList<>();            // mazo (robar)
    public final List<Card> waste  = new ArrayList<>();            // volteadas del mazo (visible)
    public final List<Card> discard = new ArrayList<>();           // cartas eliminadas (sumaron 13)
    public int score = 0;                                          // puntuación acumulada

    public void dealNewGame(Deck deck, long seed) {
        deck.shuffle(seed);
        pyramid.clear();
        stock.clear();
        waste.clear();
        discard.clear();
        score = 0;

        for (int r = 0; r < ROWS; r++) {
            var row = new ArrayList<Card>(r + 1);
            for (int c = 0; c < r + 1; c++) row.add(deck.draw());
            pyramid.add(row);
        }
        while (!deck.isEmpty()) stock.add(deck.draw());
    }

    public boolean isExposed(int r, int c) {
        if (!inBounds(r, c)) return false;
        if (pyramid.get(r).get(c) == null) return false;
        if (r == ROWS - 1) return true;
        var below = pyramid.get(r + 1);
        return below.get(c) == null && below.get(c + 1) == null;
    }

    public boolean inBounds(int r, int c) {
        return r >= 0 && r < pyramid.size() && c >= 0 && c < pyramid.get(r).size();
    }

    public Card get(int r, int c) {
        return inBounds(r, c) ? pyramid.get(r).get(c) : null;
    }

    public Card removeAt(int r, int c) {
        if (!inBounds(r, c)) return null;
        return pyramid.get(r).set(c, null);
    }

    // Helpers de descarte
    public Card removeAndDiscardAt(int r, int c) {
        Card card = removeAt(r, c);
        if (card != null) {
            discard.add(card);
            score += card.value();
        }
        return card;
    }

    public Card drawFromStock() {
        if (stock.isEmpty()) return null;
        Card c = stock.remove(stock.size() - 1);
        c.faceUp = true;
        waste.add(c);
        return c;
    }

    public Card topWaste() {
        return waste.isEmpty() ? null : waste.get(waste.size() - 1);
    }

    public Card popWaste() {
        return waste.isEmpty() ? null : waste.remove(waste.size() - 1);
    }

    public Card discardWasteTop() {
        Card c = popWaste();
        if (c != null) {
            discard.add(c);
            score += c.value();
        }
        return c;
    }

    public int resetWasteToStock() {
        int n = waste.size();
        for (int i = waste.size() - 1; i >= 0; i--) stock.add(waste.remove(i));
        return n;
    }

    public int remainingInPyramid() {
        int count = 0;
        for (var row : pyramid) for (var c : row) if (c != null) count++;
        return count;
    }
}