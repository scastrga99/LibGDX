package com.samue.game.cards;

import java.util.Objects;

public class Card {
    public final Suit suit;
    public final Rank rank;
    public boolean faceUp = true; // visible en mesa (Pyramid muestra las 28), útil para futuras reglas

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public int value() { return rank.value(); }

    @Override public String toString() { return rank.label() + suit.symbol(); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card c = (Card) o;
        return suit == c.suit && rank == c.rank;
    }

    @Override public int hashCode() { return Objects.hash(suit, rank); }
}