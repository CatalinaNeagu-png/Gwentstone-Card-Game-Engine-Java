package players;

import java.util.ArrayList;

import hero.HeroCard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private int idx;
    private boolean turn = false;
    private int mana;
    private int frontRow;
    private int backRow;
    private Deck deck;
    private ArrayList<Card> hand = new ArrayList<>();
    private int handSize;
    private HeroCard playerHero;
    private int wins;
    public static final int ZERO = 0;

    public Player(final int idx, final int mana, final Deck deck,
                   final HeroCard playerHero, final int frontRow,
                   final int backRow, final int wins) {
        this.idx = idx;
        this.mana = mana;
        this.deck = deck;
        this.handSize = ZERO;
        this.playerHero = playerHero;
        this.frontRow = frontRow;
        this.backRow = backRow;
        this.wins = wins;
    }
}
