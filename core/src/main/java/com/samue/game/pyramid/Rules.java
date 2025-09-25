package com.samue.game.pyramid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modelo simple para gestionar reglas mostradas en pantalla.
 * Permite agregar, eliminar y reemplazar reglas dinámicamente.
 */
public class Rules {
    private final List<String> rules = new ArrayList<>();

    public Rules() {}

    public Rules add(String text) {
        if (text != null && !text.isBlank()) rules.add(text.trim());
        return this;
    }

    public void remove(int index) {
        if (index >= 0 && index < rules.size()) rules.remove(index);
    }

    public void set(int index, String newText) {
        if (index >= 0 && index < rules.size() && newText != null && !newText.isBlank()) {
            rules.set(index, newText.trim());
        }
    }

    public List<String> list() { return Collections.unmodifiableList(rules); }

    public int size() { return rules.size(); }
}
