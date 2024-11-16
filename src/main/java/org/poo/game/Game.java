package org.poo.game;

import org.poo.hero.HeroCard;
import org.poo.players.Card;
import org.poo.players.Deck;
import org.poo.players.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.CardInput;
import org.poo.fileio.GameInput;
import org.poo.fileio.DecksInput;
import org.poo.fileio.Input;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Game {

    private Input input;
    private GameInput gameInput;
    private ArrayList<ActionsInput> actionsInput;
    private int turns;
    private Player playerOne;
    private Player playerTwo;
    private ArrayList<ArrayList<Card>> board = new ArrayList<>();
    private ArrayNode output;
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;
    public static final int MAX_MANA = 10;
    private Random random;

    public Game(final Input input, final GameInput gameInput,
                final ArrayList<ActionsInput> actionsInput, final ArrayNode output) {
        this.input = input;
        this.gameInput = gameInput;
        this.actionsInput = actionsInput;
        this.turns = ZERO;
        this.output = output;
    }

    /**
     * @param playerIdx player index
     * @param deckIdx deck index
     * @return a list of cards
     */
    public ArrayList<Card> makeCards(final int playerIdx, final int deckIdx) {
        ArrayList<Card> cards = new ArrayList<>();
        DecksInput playerDecks;
        if (playerIdx == ONE) {
            playerDecks = input.getPlayerOneDecks();
        } else {
            playerDecks = input.getPlayerTwoDecks();
        }
        for (CardInput cardInput : playerDecks.getDecks().get(deckIdx)) {
            Card card = new Card(cardInput.getMana(),
                    cardInput.getAttackDamage(), cardInput.getHealth(),
                    cardInput.getDescription(), cardInput.getColors(), cardInput.getName());
            cards.add(card);
        }
        return cards;
    }

    /**
     * Starts the game
     */
    public void startGame() {

        for (int i = ZERO; i < FOUR; i++) {
            ArrayList<Card> row = new ArrayList<>();
            for (int j = ZERO; j < FIVE; j++) {
                row.add(new Card());
            }
            board.add(row);
        }
        ArrayList<Card> cardsOne = makeCards(ONE, gameInput.getStartGame().getPlayerOneDeckIdx());
        ArrayList<Card> cardsTwo = makeCards(TWO, gameInput.getStartGame().getPlayerTwoDeckIdx());
        Deck deck1 = new Deck(input.getPlayerOneDecks().getNrCardsInDeck(),
                gameInput.getStartGame().getPlayerOneDeckIdx(), cardsOne);
        Deck deck2 = new Deck(input.getPlayerTwoDecks().getNrCardsInDeck(),
                gameInput.getStartGame().getPlayerTwoDeckIdx(), cardsTwo);
        random = new Random(gameInput.getStartGame().getShuffleSeed());
        Collections.shuffle(deck1.getCards(), random);
        random = new Random(gameInput.getStartGame().getShuffleSeed());
        Collections.shuffle(deck2.getCards(), random);

        HeroCard playerOneHero = new HeroCard(gameInput.getStartGame().getPlayerOneHero().getMana(),
                gameInput.getStartGame().getPlayerOneHero().getDescription(),
                gameInput.getStartGame().getPlayerOneHero().getColors(),
                gameInput.getStartGame().getPlayerOneHero().getName());
        HeroCard playerTwoHero = new HeroCard(gameInput.getStartGame().getPlayerTwoHero().getMana(),
                gameInput.getStartGame().getPlayerTwoHero().getDescription(),
                gameInput.getStartGame().getPlayerTwoHero().getColors(),
                gameInput.getStartGame().getPlayerTwoHero().getName());

        playerOne = new Player(ONE, ONE, deck1, playerOneHero, TWO, THREE, ZERO);
        playerTwo = new Player(TWO, ONE, deck2, playerTwoHero, ONE, ZERO, ZERO);

        playerOne.getHand().add(playerOne.getDeck().getCards().get(ZERO));
        playerTwo.getHand().add(playerTwo.getDeck().getCards().get(ZERO));
        playerOne.setHandSize(playerOne.getHandSize() + ONE);
        playerTwo.setHandSize(playerTwo.getHandSize() + ONE);
        playerOne.getDeck().getCards().remove(ZERO);
        playerOne.getDeck().setSize(playerOne.getDeck().getSize() - ONE);
        playerTwo.getDeck().getCards().remove(ZERO);
        playerTwo.getDeck().setSize(playerTwo.getDeck().getSize() - ONE);

        if (gameInput.getStartGame().getStartingPlayer() == ONE) {
            playerOne.setTurn(true);
            playerTwo.setTurn(false);
        } else {
            playerOne.setTurn(false);
            playerTwo.setTurn(true);
        }
    }

    /**
     * @param winsOne number of wins for player one
     * @param winsTwo number of wins for player two
     */
    public void setPlayerWins(final int winsOne, final int winsTwo) {
        playerOne.setWins(winsOne);
        playerTwo.setWins(winsTwo);
    }

    /**
     * @param actions list of actions
     */
    public void makeActions(final ArrayList<ActionsInput> actions) {
        int round = 1;
        for (ActionsInput action : actions) {
            Actions newAction = new Actions(action.getCommand(), action.getHandIdx(),
                    action.getCardAttacker(), action.getCardAttacked(),
                    action.getAffectedRow(), action.getPlayerIdx(), action.getX(), action.getY());


            if (action.getCommand().equals("endPlayerTurn")) {
                this.turns++;
                if (this.turns % TWO == ZERO) {
                    round++;

                    playerOne.getPlayerHero().setHasAttacked(false);
                    playerTwo.getPlayerHero().setHasAttacked(false);

                    for (int i = ZERO; i < FOUR; i++) {
                        for (int j = ZERO; j < FIVE; j++) {
                            Card card = board.get(i).get(j);
                            if (card != null) {
                                if (card.getName() != null) {
                                    card.setHasAttacked(false);
                                }
                            }
                        }
                    }

                    if (round <= MAX_MANA) {
                        playerOne.setMana(playerOne.getMana() + (round));
                        playerTwo.setMana(playerTwo.getMana() + (round));
                    } else {
                        playerOne.setMana(playerOne.getMana() + MAX_MANA);
                        playerTwo.setMana(playerTwo.getMana() + MAX_MANA);
                    }

                    addCardToHand(playerOne);
                    addCardToHand(playerTwo);
                }

                if (playerOne.isTurn()) {
                    playerOne.setTurn(false);
                    playerTwo.setTurn(true);
                    for (int i = TWO; i < FOUR; i++) {
                        for (int j = ZERO; j < FIVE; j++) {
                            Card card = board.get(i).get(j);
                            if (card != null) {
                                if (card.getName() != null) {
                                    card.setFrozen(false);
                                }
                            }
                        }
                    }
                } else {
                    playerOne.setTurn(true);
                    playerTwo.setTurn(false);
                    for (int i = ZERO; i < TWO; i++) {
                        for (int j = ZERO; j < FIVE; j++) {
                            Card card = board.get(i).get(j);
                            if (card != null) {
                                if (card.getName() != null) {
                                    card.setFrozen(false);
                                }
                            }
                        }
                    }
                }
            }

            if (action.getCommand().equals("getTotalGamesPlayed")) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode result = objectMapper.createObjectNode();
                result.put("command", "getTotalGamesPlayed");
                result.put("output", playerOne.getWins() + playerTwo.getWins());
                output.add(result);
            }

            if (action.getCommand().equals("getPlayerOneWins")) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode result = objectMapper.createObjectNode();
                result.put("command", "getPlayerOneWins");
                result.put("output", playerOne.getWins());
                output.add(result);
            }

            if (action.getCommand().equals("getPlayerTwoWins")) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode result = objectMapper.createObjectNode();
                result.put("command", "getPlayerTwoWins");
                result.put("output", playerTwo.getWins());
                output.add(result);
            }

            if (action.getPlayerIdx() != ZERO) {
                if (action.getPlayerIdx() == ONE) {
                    newAction.executeCommand(action.getCommand(),
                            newAction, playerOne, output, board);
                } else if (action.getPlayerIdx() == TWO) {
                    newAction.executeCommand(action.getCommand(),
                            newAction, playerTwo, output, board);
                }
            } else if (playerOne.isTurn()) {
                if (action.getCommand().equals("useAttackHero")) {
                    newAction.executeCommand(action.getCommand(),
                            newAction, playerTwo, output, board);
                } else {
                    newAction.executeCommand(action.getCommand(),
                            newAction, playerOne, output, board);
                }
            } else if (action.getCommand().equals("useAttackHero")) {
                    newAction.executeCommand(action.getCommand(),
                            newAction, playerOne, output, board);
                } else {
                    newAction.executeCommand(action.getCommand(),
                            newAction, playerTwo, output, board);
                }

            if (playerOne.getPlayerHero().isDead()) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode result = objectMapper.createObjectNode();
                result.put("gameEnded", "Player two killed the enemy hero.");
                output.add(result);
                playerOne.getPlayerHero().setDead(false);
                playerTwo.setWins(playerTwo.getWins() + ONE);
            } else if (playerTwo.getPlayerHero().isDead()) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode result = objectMapper.createObjectNode();
                result.put("gameEnded", "Player one killed the enemy hero.");
                output.add(result);
                playerTwo.getPlayerHero().setDead(false);
                playerOne.setWins(playerOne.getWins() + ONE);
            }
        }
    }

    /**
     * @param player1 player one
     */
    private void addCardToHand(final Player player1) {
        if (player1.getDeck().getSize() > ZERO) {
            player1.getHand().add(player1.getDeck().getCards().get(ZERO));
            player1.setHandSize(player1.getHandSize() + ONE);
            player1.getDeck().getCards().remove(ZERO);
            player1.getDeck().setSize(player1.getDeck().getSize() - ONE);
        }
    }

}
