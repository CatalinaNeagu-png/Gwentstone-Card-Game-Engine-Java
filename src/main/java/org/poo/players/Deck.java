package org.poo.players;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Deck {

    private int nrCardsInDeck;
    private int deckIdx;
    private ArrayList<Card> cards;
    private int size;

    public Deck(final int nrCardsInDeck, final int deckIdx, final ArrayList<Card> cards) {
        this.nrCardsInDeck = nrCardsInDeck;
        this.deckIdx = deckIdx;
        this.cards = cards;
        this.size = nrCardsInDeck;
    }
}
