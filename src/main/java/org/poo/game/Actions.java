package org.poo.game;

import org.poo.hero.HeroCard;
import org.poo.players.Card;
import org.poo.players.Deck;
import org.poo.players.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.Coordinates;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Actions {
    private final String command;
    private final int handIdx;
    private final Coordinates cardAttacker;
    private final Coordinates cardAttacked;
    private final int affectedRow;
    private final int playerIdx;
    private final int x;
    private final int y;
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int FOUR = 4;
    public static final int FIVE = 5;

    public Actions(final String command, final int handIdx, final Coordinates cardAttacker,
                   final Coordinates cardAttacked, final int affectedRow, final int playerIdx,
                   final int x, final int y) {
        this.command = command;
        this.handIdx = handIdx;
        this.cardAttacker = cardAttacker;
        this.cardAttacked = cardAttacked;
        this.affectedRow = affectedRow;
        this.playerIdx = playerIdx;
        this.x = x;
        this.y = y;
    }

    /**
     * Executes the command given by the player
     * @param commandToExecute the command given by the player
     * @param action the action to be executed
     * @param player the player that executes the command
     * @param output the output of the command
     * @param board the board of the game
     */
    public void executeCommand(final String commandToExecute, final Actions action,
                               final Player player, final ArrayNode output,
                               final ArrayList<ArrayList<Card>> board) {
        switch (commandToExecute) {
            case "getPlayerDeck":
                getPlayerDeck(player, output);
                break;
            case "getPlayerHero":
                getPlayerHero(player, output);
                break;
            case "getPlayerTurn":
                getPlayerTurn(player, output);
                break;
            case "getCardsInHand":
                getCardsInHand(player, output);
                break;
            case "getPlayerMana":
                getPlayerMana(player, output);
                break;
            case "getCardsOnTable":
                getCardsOnTable(output, board);
                break;
            case "placeCard":
                placeCard(action, player, output, board);
                break;
            case "cardUsesAttack":
                cardUsesAttack(action, player, output, board);
                break;
            case "getCardAtPosition":
                getCardAtPosition(action, output, board);
                break;
            case "cardUsesAbility":
                cardUsesAbility(action, player, output, board);
                break;
            case "useAttackHero":
                useAttackHero(action, player, output, board);
                break;
            case "useHeroAbility":
                useHeroAbility(action, player, output, board);
                break;
            case "getFrozenCardsOnTable":
                getFrozenCardsOnTable(output, board);
                break;

            default:
                break;
        }
    }

    /**
     * Outputs the player's deck
     * @param player the player that has the deck
     * @param output the output of the command
     */
    public void getPlayerDeck(final Player player, final ArrayNode output) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        result.put("command", "getPlayerDeck");
        result.put("playerIdx", player.getIdx());

        ArrayNode cardsArray = result.putArray("output");
        Deck playerDeck = player.getDeck();

        for (Card card : playerDeck.getCards()) {
            cardsArray.add(outputCards(objectMapper, card));
        }
        output.add(result);
    }

    /**
     * Outputs the player's hero
     * @param player the player that has the hero
     * @param output the output of the command
     */
    public void getPlayerHero(final Player player, final ArrayNode output) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        result.put("command", "getPlayerHero");
        result.put("playerIdx", player.getIdx());

        ObjectNode heroDetails = objectMapper.createObjectNode();

        HeroCard playerHero = player.getPlayerHero();

        heroDetails.put("mana", playerHero.getMana());
        heroDetails.put("description", playerHero.getDescription());

        ArrayNode colors = heroDetails.putArray("colors");
        for (String color : playerHero.getColors()) {
            colors.add(color);
        }

        heroDetails.put("name", playerHero.getName());
        heroDetails.put("health", playerHero.getHealth());

        result.set("output", heroDetails);

        output.add(result);
    }

    /**
     * Outputs the player's turn
     * @param player the player that has the turn
     * @param output the output of the command
     */
    public void getPlayerTurn(final Player player, final ArrayNode output) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        result.put("command", "getPlayerTurn");
        result.put("output", player.getIdx());
        output.add(result);
    }

    /**
     * Outputs the cards in the player's hand
     * @param player the player that has the cards in hand
     * @param output the output of the command
     */
    public void getCardsInHand(final Player player, final ArrayNode output) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        result.put("command", "getCardsInHand");
        result.put("playerIdx", player.getIdx());

        ArrayNode cardsArray = result.putArray("output");

        for (Card card : player.getHand()) {
            cardsArray.add(outputCards(objectMapper, card));
        }
        output.add(result);
    }

    /**
     * Outputs the player's mana
     * @param player the player that has the mana
     * @param output the output of the command
     */
    public void getPlayerMana(final Player player, final ArrayNode output) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        result.put("command", "getPlayerMana");
        result.put("playerIdx", player.getIdx());
        result.put("output", player.getMana());
        output.add(result);
    }

    /**
     * Outputs the cards on the table
     * @param output the output of the command
     * @param board the board of the game
     */
    public void getCardsOnTable(final ArrayNode output, final ArrayList<ArrayList<Card>> board) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        result.put("command", "getCardsOnTable");
        ArrayNode cardsOnTable = objectMapper.createArrayNode();

        for (int i = ZERO; i < FOUR; i++) {
                ArrayNode row = objectMapper.createArrayNode();
                for (int j = ZERO; j < FIVE; j++) {
                    Card card = board.get(i).get(j);

                    if (card != null) {
                        if (card.getName() != null) {
                            row.add(outputCards(objectMapper, card));
                        }
                    }
                }
                cardsOnTable.add(row);
        }

        result.set("output", cardsOnTable);
        output.add(result);
    }

    /**
     * Outputs the frozen cards on the table
     * @param output the output of the command
     * @param board the board of the game
     */
    public void getFrozenCardsOnTable(final ArrayNode output,
                                      final ArrayList<ArrayList<Card>> board) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        result.put("command", "getFrozenCardsOnTable");
        ArrayNode cardsOnTable = objectMapper.createArrayNode();

        for (int i = ZERO; i < FOUR; i++) {
            for (int j = ZERO; j < FIVE; j++) {
                Card card = board.get(i).get(j);
                if (card != null) {
                    if (card.getName() != null) {
                        if (card.isFrozen()) {
                            cardsOnTable.add(outputCards(objectMapper, card));
                        }
                    }
                }
            }
        }
        result.set("output", cardsOnTable);
        output.add(result);
    }

    /**
     * Places a card on the table
     * @param action the action to be executed
     * @param player the player that executes the command
     * @param output the output of the command
     * @param board the board of the game
     */
    public void placeCard(final Actions action, final Player player,
                          final ArrayNode output, final ArrayList<ArrayList<Card>> board) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        int actionHandIdx = action.getHandIdx();
        Card card = new Card();
        Card emptyCard = new Card();

        result.put("command", "placeCard");
        result.put("handIdx", action.getHandIdx());

        if (actionHandIdx >= ZERO) {
            card = player.getHand().get(actionHandIdx);
        }

        if (card.getName() == null) {
            return;
        }

        if (player.getMana() < card.getMana()) {
            result.put("error", "Not enough mana to place card on table.");
            output.add(result);
            return;
        }

        if (card.getName().equals("Sentinel")
                || card.getName().equals("Berserker")
                || card.getName().equals("Disciple")
                || card.getName().equals("The Cursed One")) {

                for (int i = ZERO; i < FIVE; i++) {
                    if (board.get(player.getBackRow()).get(i) == emptyCard
                            || board.get(player.getBackRow()).get(i).getName() == null) {
                        board.get(player.getBackRow()).set(i, card);
                        player.getHand().remove(action.getHandIdx());
                        player.setMana(player.getMana() - card.getMana());
                        break;
                    }
            }
        } else if (card.getName().equals("Goliath")
                || card.getName().equals("Warden")
                || card.getName().equals("The Ripper")
                || card.getName().equals("Miraj")) {

                for (int i = ZERO; i < FIVE; i++) {
                    if (board.get(player.getFrontRow()).get(i) == emptyCard
                            || board.get(player.getFrontRow()).get(i).getName() == null) {
                        board.get(player.getFrontRow()).set(i, card);
                        player.getHand().remove(action.getHandIdx());
                        player.setMana(player.getMana() - card.getMana());
                        break;
                    }
            }

        }
    }

    /**
     * Executes the attack command
     * @param action the action to be executed
     * @param player the player that executes the command
     * @param output the output of the command
     * @param board the board of the game
     */
    public void cardUsesAttack(final Actions action, final Player player,
                               final ArrayNode output, final ArrayList<ArrayList<Card>> board) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        int enemyFrontRow;

        Coordinates attacker = action.getCardAttacker();
        Coordinates attacked = action.getCardAttacked();

        Card attackerCard = board.get(attacker.getX()).get(attacker.getY());
        Card attackedCard = board.get(attacked.getX()).get(attacked.getY());

        if (attackerCard == null || attackedCard == null) {
            return;
        }

        if (player.getBackRow() == ZERO) {
            enemyFrontRow = TWO;
        } else {
            enemyFrontRow = ONE;
        }

        if (allyCard(player, objectMapper, result, attacker, attacked, output)) {
            result.put("command", "cardUsesAttack");
            return;
        }

        if (hasCardAttacked(attackerCard, objectMapper, result, attacker, attacked, output)) {
            result.put("command", "cardUsesAttack");
            return;
        }

        if (isCardFrozen(attackerCard, objectMapper, result, attacker, attacked, output)) {
            result.put("command", "cardUsesAttack");
            return;
        }

        if (checkTank(enemyFrontRow, objectMapper, result, attacker, attacked, output, board)) {
            result.put("command", "cardUsesAttack");
            return;
        }


        attackedCard.setHealth(attackedCard.getHealth() - attackerCard.getAttackDamage());
        attackerCard.setHasAttacked(true);
        if (attackedCard.getHealth() <= ZERO) {
            eliminateCard(board, attacked);
        }

    }

    /**
     * Outputs the card at a given position
     * @param action the action to be executed
     * @param output the output of the command
     * @param board the board of the game
     */
    public void getCardAtPosition(final Actions action, final ArrayNode output,
                                  final ArrayList<ArrayList<Card>> board)  {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        int xCoord = action.getX();
        int yCoord = action.getY();

        result.put("command", "getCardAtPosition");
        result.put("x", xCoord);
        result.put("y", yCoord);

        if (board.get(xCoord).get(yCoord) == null
                || board.get(xCoord).get(yCoord).getName() == null) {
            result.put("output", "No card available at that position.");
            output.add(result);
            return;
        }

        Card card = board.get(xCoord).get(yCoord);

        ObjectNode cardOutput = outputCards(objectMapper, card);
        result.set("output", cardOutput);
        output.add(result);
    }

    /**
     * Executes the ability of a card
     * @param action the action to be executed
     * @param player the player that executes the command
     * @param output the output of the command
     * @param board the board of the game
     */
    public void cardUsesAbility(final Actions action, final Player player,
                                final ArrayNode output, final ArrayList<ArrayList<Card>> board) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        int enemyFrontRow;

        Coordinates attacker = action.getCardAttacker();
        Coordinates attacked = action.getCardAttacked();

        Card attackerCard = board.get(attacker.getX()).get(attacker.getY());
        Card attackedCard = board.get(attacked.getX()).get(attacked.getY());

        if (attackerCard == null
                || attackedCard == null
                || attackedCard.getName() == null
                || attackerCard.getName() == null) {
            return;
        }

        if (player.getBackRow() == ZERO) {
            enemyFrontRow = TWO;
        } else {
            enemyFrontRow = ONE;
        }

        if (isCardFrozen(attackerCard, objectMapper, result, attacker, attacked, output)) {
            result.put("command", "cardUsesAbility");
            return;
        }

        if (hasCardAttacked(attackerCard, objectMapper, result, attacker, attacked, output)) {
            result.put("command", "cardUsesAbility");
            return;
        }

        if (attackerCard.getName().equals("Disciple")) {
            if (attacked.getX() != player.getFrontRow() && attacked.getX() != player.getBackRow()) {
                result.put("command", "cardUsesAbility");
                outputCoordinates(objectMapper, result, attacker, attacked);

                result.put("error", "Attacked card does not belong to the current player.");
                output.add(result);
                return;
            }
        } else if (allyCard(player, objectMapper, result, attacker, attacked, output)) {
            result.put("command", "cardUsesAbility");
            return;
        }

        if (!attackerCard.getName().equals(("Disciple"))) {
            if (checkTank(enemyFrontRow, objectMapper, result, attacker, attacked, output, board)) {
                result.put("command", "cardUsesAbility");
                return;
            }
        }

        if (attackerCard.getName().equals("The Ripper")) {
            if (attackedCard.getAttackDamage() < 2) {
                board.get(attacked.getX()).get(attacked.getY()).setAttackDamage(0);
            } else {
                int damage = attackedCard.getAttackDamage();
                board.get(attacked.getX()).get(attacked.getY()).setAttackDamage(damage - 2);
            }
            attackerCard.setHasAttacked(true);
        }

        if (attackerCard.getName().equals("Miraj")) {
            int health = attackerCard.getHealth();
            attackerCard.setHealth(attackedCard.getHealth());
            attackedCard.setHealth(health);
            attackerCard.setHasAttacked(true);
        }

        if (attackerCard.getName().equals("The Cursed One")) {
            if (attackedCard.getAttackDamage() == ZERO) {
                eliminateCard(board, attacked);
            } else {
                int health = attackedCard.getHealth();
                attackedCard.setHealth(attackedCard.getAttackDamage());
                attackedCard.setAttackDamage(health);
            }
            attackerCard.setHasAttacked(true);
        }

        if (attackerCard.getName().equals("Disciple")) {
            attackedCard.setHealth(attackedCard.getHealth() + TWO);
            attackerCard.setHasAttacked(true);
        }
    }

    /**
     * Executes the attack command on the hero
     * @param action the action to be executed
     * @param player the player that executes the command
     * @param output the output of the command
     * @param board the board of the game
     */
    public void useAttackHero(final Actions action, final Player player,
                              final ArrayNode output, final ArrayList<ArrayList<Card>> board) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        int enemyFrontRow;

        Coordinates attacker = action.getCardAttacker();

        Card attackerCard = board.get(attacker.getX()).get(attacker.getY());
        Card attackedCard = player.getPlayerHero();

        if (attackerCard == null || attackedCard == null) {
            return;
        }

        if (player.getBackRow() == ZERO) {
            enemyFrontRow = ONE;
        } else {
            enemyFrontRow = TWO;
        }

        if (attackerCard.isFrozen()) {
            result.put("command", "useAttackHero");
            outputCoordinatesAttacker(objectMapper, result, attacker);
            result.put("error", "Attacker card is frozen.");
            output.add(result);
            return;
        }

        if (attackerCard.isHasAttacked()) {
            result.put("command", "useAttackHero");
            outputCoordinatesAttacker(objectMapper, result, attacker);
            result.put("error", "Attacker card has already attacked this turn.");
            output.add(result);
            return;
        }

        for (int i = ZERO; i < FIVE; i++) {
            Card card = board.get(enemyFrontRow).get(i);
            if (card != null && card.getName() != null) {
                if (card.getName().equals("Goliath") || card.getName().equals("Warden")) {
                    result.put("command", "useAttackHero");
                    outputCoordinatesAttacker(objectMapper, result, attacker);
                    result.put("error", "Attacked card is not of type 'Tank'.");
                    output.add(result);
                    return;
                }
            }
        }

        int heroHealth = player.getPlayerHero().getHealth();
        int damage = attackerCard.getAttackDamage();

        player.getPlayerHero().setHealth(heroHealth - damage);
        if (player.getPlayerHero().getHealth() <= ZERO) {
            player.getPlayerHero().setDead(true);
        }
        attackerCard.setHasAttacked(true);
    }

    /**
     * Executes the hero's ability
     * @param action the action to be executed
     * @param player the player that executes the command
     * @param output the output of the command
     * @param board the board of the game
     */
    public void useHeroAbility(final Actions action, final Player player,
                               final ArrayNode output, final ArrayList<ArrayList<Card>> board) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        int actionAffectedRow = action.getAffectedRow();
        Card attackerCard = player.getPlayerHero();

        if (attackerCard == null) {
            return;
        }

        if (player.getMana() < attackerCard.getMana()) {
            result.put("affectedRow", actionAffectedRow);
            result.put("command", "useHeroAbility");
            result.put("error", "Not enough mana to use hero's ability.");
            output.add(result);
            return;
        }

        if (attackerCard.isHasAttacked()) {
            result.put("affectedRow", actionAffectedRow);
            result.put("command", "useHeroAbility");
            result.put("error", "Hero has already attacked this turn.");
            output.add(result);
            return;
        }

        if (attackerCard.getName().equals("Lord Royce")
                || attackerCard.getName().equals("Empress Thorina")) {
            if (actionAffectedRow == player.getFrontRow()
                    || actionAffectedRow == player.getBackRow()) {
                result.put("command", "useHeroAbility");
                result.put("affectedRow", actionAffectedRow);
                result.put("error", "Selected row does not belong to the enemy.");
                output.add(result);
                return;
            }

            if (attackerCard.getName().equals("Lord Royce")) {

                for (int i = ZERO; i < FIVE; i++) {
                    Card card = board.get(actionAffectedRow).get(i);
                    if (card != null && card.getName() != null) {
                        board.get(actionAffectedRow).get(i).setFrozen(true);
                    }
                }
            }

            if (attackerCard.getName().equals("Empress Thorina")) {
                int maxHealthCard = ZERO;
                Coordinates coordinates = new Coordinates();
                for (int i = ZERO; i < FIVE; i++) {
                    Card card = board.get(actionAffectedRow).get(i);
                    if (card != null && card.getName() != null) {
                        if (maxHealthCard < card.getHealth()) {
                            maxHealthCard = card.getHealth();
                            coordinates.setX(actionAffectedRow);
                            coordinates.setY(i);
                        }
                    }
                }
                eliminateCard(board, coordinates);
            }
        } else if (attackerCard.getName().equals("General Kocioraw")
                || attackerCard.getName().equals("King Mudface")) {
            if (actionAffectedRow != player.getFrontRow()
                    && actionAffectedRow != player.getBackRow()) {
                result.put("command", "useHeroAbility");
                result.put("affectedRow", actionAffectedRow);
                result.put("error", "Selected row does not belong to the current player.");
                output.add(result);
                return;
            }

            if (attackerCard.getName().equals("General Kocioraw")) {
                for (int i = ZERO; i < FIVE; i++) {
                    Card card = board.get(actionAffectedRow).get(i);
                    if (card != null && card.getName() != null) {
                        card.setAttackDamage(card.getAttackDamage() + ONE);
                    }
                }
            }

            if (attackerCard.getName().equals("King Mudface")) {
                for (int i = ZERO; i < FIVE; i++) {
                    Card card = board.get(actionAffectedRow).get(i);
                    if (card != null && card.getName() != null) {
                        card.setHealth(card.getHealth() + ONE);
                    }
                }
            }
        }
        player.setMana(player.getMana() - attackerCard.getMana());
        attackerCard.setHasAttacked(true);
    }

    /**
     * Checks if the card is frozen
     * @param card the card to be checked
     * @param objectMapper the object mapper
     * @param result the result of the command
     * @param attacker the attacker's coordinates
     * @param attacked the attacked's coordinates
     * @param output the output of the command
     * @return true if the card is frozen, false otherwise
     */
    public boolean isCardFrozen(final Card card, final ObjectMapper objectMapper,
                                final ObjectNode result, final Coordinates attacker,
                                final Coordinates attacked, final ArrayNode output) {
        if (card.isFrozen()) {
            outputCoordinates(objectMapper, result, attacker, attacked);
            result.put("error", "Attacker card is frozen.");
            output.add(result);
            return true;
        }

        return false;

    }

    /**
     * Checks if the card has attacked
     * @param card the card to be checked
     * @param objectMapper the object mapper
     * @param result the result of the command
     * @param attacker the attacker's coordinates
     * @param attacked the attacked's coordinates
     * @param output the output of the command
     * @return true if the card has attacked, false otherwise
     */
    public boolean hasCardAttacked(final Card card, final ObjectMapper objectMapper,
                                   final ObjectNode result, final Coordinates attacker,
                                   final Coordinates attacked, final ArrayNode output) {
        if (card.isHasAttacked()) {
            outputCoordinates(objectMapper, result, attacker, attacked);
            result.put("error", "Attacker card has already attacked this turn.");
            output.add(result);
            return true;
        }

        return false;
    }

    /**
     * Checks if the card is an ally card
     * @param player the player that executes the command
     * @param objectMapper the object mapper
     * @param result the result of the command
     * @param attacker the attacker's coordinates
     * @param attacked the attacked's coordinates
     * @param output the output of the command
     * @return true if the card is an ally card, false otherwise
     */
    public boolean allyCard(final Player player, final ObjectMapper objectMapper,
                            final ObjectNode result, final Coordinates attacker,
                            final Coordinates attacked, final ArrayNode output) {
        if (attacked.getX() == player.getFrontRow() || attacked.getX() == player.getBackRow()) {
            outputCoordinates(objectMapper, result, attacker, attacked);

            result.put("error", "Attacked card does not belong to the enemy.");
            output.add(result);
            return true;
        }
        return false;
    }

    /**
     * Checks if the enemy has a tank card
     * @param enemyFrontRow the enemy's front row
     * @param objectMapper the object mapper
     * @param result the result of the command
     * @param attacker the attacker's coordinates
     * @param attacked the attacked's coordinates
     * @param output the output of the command
     * @param board the board of the game
     * @return true if the enemy has a tank card, false otherwise
     */
    public boolean checkTank(final int enemyFrontRow, final ObjectMapper objectMapper,
                             final ObjectNode result, final Coordinates attacker,
                             final Coordinates attacked, final ArrayNode output,
                             final ArrayList<ArrayList<Card>> board) {
        boolean checkTank = false;

        for (int i = ZERO; i < FIVE; i++) {
            Card card = board.get(enemyFrontRow).get(i);
            if (card != null && card.getName() != null) {
                if (card.getName().equals("Goliath")
                        || card.getName().equals("Warden")) {
                    checkTank = true;
                    break;
                }
            }
        }

        if (checkTank) {
            Card attackedCard = board.get(attacked.getX()).get(attacked.getY());
            if (board.get(attacked.getX()).get(attacked.getY()).getName() != null) {
                if (!attackedCard.getName().equals("Goliath")
                        && !attackedCard.getName().equals("Warden")) {
                    outputCoordinates(objectMapper, result, attacker, attacked);
                    result.put("error", "Attacked card is not of type 'Tank'.");
                    output.add(result);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Eliminates a card from the board
     * @param board the board of the game
     * @param attacked the attacked's coordinates
     */
    private void eliminateCard(final ArrayList<ArrayList<Card>> board, final Coordinates attacked) {
        board.get(attacked.getX()).get(attacked.getY()).setDead(true);
        Card emptyCard = new Card();
        board.get(attacked.getX()).set(attacked.getY(), emptyCard);
        if (attacked.getY() != FOUR) {
            if (board.get(attacked.getX()).get(attacked.getY() + ONE) != emptyCard) {
                for (int i = attacked.getY() + ONE; i < FIVE; i++) {
                    if (board.get(attacked.getX()).get(i) != emptyCard) {
                        board.get(attacked.getX()).set(i - ONE, board.get(attacked.getX()).get(i));
                    } else {
                        board.get(attacked.getX()).set(i - ONE, emptyCard);
                        break;
                    }
                }
            } else {
                board.get(attacked.getX()).set(attacked.getY(), emptyCard);
            }
        }
    }

    /**
     * Outputs the cards that are attacking and attacked
     * @param objectMapper the object mapper
     */
    private ObjectNode outputCards(final ObjectMapper objectMapper, final Card card) {
        if (card == null) {
            return null;
        }

        ObjectNode cards = objectMapper.createObjectNode();
        cards.put("mana", card.getMana());
        cards.put("attackDamage", card.getAttackDamage());
        cards.put("health", card.getHealth());
        cards.put("description", card.getDescription());

        ArrayNode colors = cards.putArray("colors");
        if (card.getColors() != null) {
            for (String color : card.getColors()) {
                colors.add(color);
            }
        }

        cards.put("name", card.getName());
        return cards;
    }

    /**
     * Outputs the coordinates of the cards that are attacking and attacked
     * @param objectMapper the object mapper
     * @param result the result of the command
     * @param attacker the attacker's coordinates
     * @param attacked the attacked's coordinates
     */
    private void outputCoordinates(final ObjectMapper objectMapper, final ObjectNode result,
                                   final Coordinates attacker, final Coordinates attacked) {
        ObjectNode attackerCoord = objectMapper.createObjectNode();
        attackerCoord.put("x", attacker.getX());
        attackerCoord.put("y", attacker.getY());
        result.set("cardAttacker", attackerCoord);

        ObjectNode attackedCoord = objectMapper.createObjectNode();
        attackedCoord.put("x", attacked.getX());
        attackedCoord.put("y", attacked.getY());
        result.set("cardAttacked", attackedCoord);
    }

    /**
     * Outputs the coordinates of the attacker
     * @param objectMapper the object mapper
     * @param result the result of the command
     * @param attacker the attacker's coordinates
     */
    private void outputCoordinatesAttacker(final ObjectMapper objectMapper,
                                           final ObjectNode result, final Coordinates attacker) {
        ObjectNode attackerOutput = objectMapper.createObjectNode();
        attackerOutput.put("x", attacker.getX());
        attackerOutput.put("y", attacker.getY());
        result.set("cardAttacker", attackerOutput);
    }
}
