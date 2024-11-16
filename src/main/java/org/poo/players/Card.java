package org.poo.players;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;
    private boolean isDead = false;
    private boolean isFrozen = false;
    private boolean hasAttacked = false;
    public static final int HERO_HEALTH = 30;
    public static final int ZERO = 0;

    public Card(final int mana, final int attackDamage, final int health,
                final String description, final ArrayList<String> colors, final String name) {
        this.mana = mana;
        this.attackDamage = attackDamage;
        this.health = health;
        this.description = description;
        this.colors = colors;
        this.name = name;
    }

    public Card() {
        this.mana = ZERO;
        this.attackDamage = ZERO;
        this.health = ZERO;
        this.description = null;
        this.colors = null;
        this.name = null;
    }

    public Card(final int mana, final String description,
                final ArrayList<String> colors, final String name) {
        this.mana = mana;
        this.health = HERO_HEALTH;
        this.description = description;
        this.colors = colors;
        this.name = name;
    }
}
