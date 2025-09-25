package com.samue.game.cards;

public enum Suit {
    CLUBS, DIAMONDS, HEARTS, SPADES;

    public boolean isRed() {
        return this == DIAMONDS || this == HEARTS;
    }

    public String symbol() {
        switch (this) {
            case CLUBS: return "♣";
            case DIAMONDS: return "♦";
            case HEARTS: return "♥";
            case SPADES: return "♠";
            default: return "?";
        }
    }
}