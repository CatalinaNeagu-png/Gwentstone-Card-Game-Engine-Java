package hero;

import players.Card;

import java.util.ArrayList;

public class HeroCard extends Card {

    public HeroCard(final int mana, final String description,
                    final ArrayList<String> colors, final String name) {
        super(mana, description, colors, name);
    }

    public HeroCard() {
        super();
    }
}
