# Gwentstone: Card Game Engine (Java)

**Gwentstone** is a strategic card game engine implemented in Java that blends core mechanics from *Gwent* and *Hearthstone*. The game features a dynamic 4x5 board where two players deploy minions with unique abilities, manage mana resources, and use hero powers to eliminate the opponent's hero.

## Game Mechanics

* **Board Structure**: A 4x5 grid. Each player controls two rows: a **Front Row** (for melee/tanks) and a **Back Row** (for ranged/supports).
* **Goal**: Reduce the opponent's hero health from 30 to zero.
* **Mana System**: Players receive mana at the start of each round (Round 1 = 1 mana, Round 2 = 2 mana, etc., up to a maximum of 10).
* **Turn Logic**:
    * Players draw a card at the start of each round.
    * Cards can attack or use abilities once per turn.
    * "Frozen" cards skip their next turn.

## Card & Hero Directory

### Minion Abilities
Cards have specific placement rules and battlefield effects:

| Card Type | Row | Special Ability |
| :--- | :--- | :--- |
| **Sentinel / Berserker** | Back | Standard minions. |
| **Goliath / Warden** | Front | **Tank**: Opponents MUST destroy these before attacking any other cards. |
| **The Ripper** | Any | Reduces an opponent minion's attack damage by 2. |
| **Miraj** | Any | Swaps health values between a friendly and an opponent minion. |
| **The Cursed One** | Any | Swaps an opponent minion's attack and health values. |
| **Disciple** | Any | Increases a friendly minion's health by 2 (Heal). |

### Hero Powers
Each player has a hero with a unique ability that costs mana:
* **Lord Royce**: Freezes an entire row on the opponent's board.
* **Empress Thorina**: Eliminates the card with the highest health in an opponent's row.
* **King Mudface**: Buffs all friendly minions in a row with +1 health.
* **General Kocioraw**: Buffs all friendly minions in a row with +1 attack.

## Project Architecture

The project is built using strict **Object-Oriented Programming (OOP)** principles, organized into logical packages:

### Package Overview
* **`players`**: Manages the `Card`, `Deck`, and `Player` entities. Handles card states such as `isFrozen` or `hasAttacked`.
* **`hero`**: Implements specialized `HeroCard` logic, extending the base card class.
* **`game`**: The core engine.
    * `Actions`: Parses and executes commands like `placeCard`, `attack`, or `useAbility`.
    * `Game`: Manages the main loop, turn switching, mana distribution, and board maintenance (shifting cards when one dies).
* **`main`**: The entry point. Handles JSON input reading, game initialization, and global statistics (total games, win counts).

## How to Run

1.  **Input**: The game reads input from JSON files via the `fileio` package.
2.  **Simulation**: The engine shuffles decks using a specific seed and executes the sequence of actions.
3.  **Output**: Results for each command and game statistics are written to a JSON output file.