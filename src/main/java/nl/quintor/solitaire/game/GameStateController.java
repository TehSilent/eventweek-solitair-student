package nl.quintor.solitaire.game;

import nl.quintor.solitaire.models.card.Card;
import nl.quintor.solitaire.models.card.Rank;
import nl.quintor.solitaire.models.card.Suit;
import nl.quintor.solitaire.models.deck.Deck;
import nl.quintor.solitaire.models.deck.DeckType;
import nl.quintor.solitaire.models.state.GameState;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Library class for GameState initiation and status checks that are called from {@link nl.quintor.solitaire.Main}.
 * The class is not instantiable, all constructors are private and all methods are static.
 */
public class GameStateController {
    private GameStateController(){}

    /**
     * Creates and initializes a new GameState object. The newly created GameState is populated with shuffled cards. The
     * stack pile and column maps are filled with headers and Deck objects. The column decks have an appropriate number
     * of invisible cards set.
     *
     * @return a new GameState object, ready to go
     */
    public static GameState init(){

        //Create a default deck with all 52 cards (no jokers)
        List<Card> allCards = new ArrayList<Card>();
        for (int suit = 0; suit < 4; suit++) {
            Suit s = Suit.CLUBS;
            switch (suit) {
                case 0:
                    s = Suit.SPADES;
                    break;
                case 1:
                    s = Suit.HEARTS;
                    break;
                case 2:
                    s = Suit.CLUBS;
                    break;
                case 3:
                    s = Suit.DIAMONDS;
                    break;
            }

            //A = 0 J = 10 Q = 11 K = 12
            for (int rank = 0; rank < 13; rank++) {
                Rank r = Rank.ACE;
                switch (rank) {
                    case 0:
                        r = Rank.ACE;
                        break;
                    case 1:
                        r = Rank.TWO;
                        break;
                    case 2:
                        r = Rank.THREE;
                        break;
                    case 3:
                        r = Rank.FOUR;
                        break;
                    case 4:
                        r = Rank.FIVE;
                        break;
                    case 5:
                        r = Rank.SIX;
                        break;
                    case 6:
                        r = Rank.SEVEN;
                        break;
                    case 7:
                        r = Rank.EIGHT;
                        break;
                    case 8:
                        r = Rank.NINE;
                        break;
                    case 9:
                        r = Rank.TEN;
                        break;
                    case 10:
                        r = Rank.JACK;
                        break;
                    case 11:
                        r = Rank.QUEEN;
                        break;
                    case 12:
                        r = Rank.KING;
                        break;
                }
                allCards.add(new Card(s, r));
            }
        }

        //shuffle the cards by switching 2 random ones 200 times
        allCards = Shuffle(allCards,200);

        //Create GameState instance
        GameState state = new GameState();

        //Create columns
        int cardIndex = 0;
        int column = 0;
        String[] arr = {"A","B","C","D","E","F","G"};
        for (var a : arr) {
            Deck c = new Deck();
            c.setInvisibleCards(column);
            for(int card = 0; card < column+1;card++){
                c.add(card,allCards.get(cardIndex));
                cardIndex++;
            }
            c.setDeckType(DeckType.COLUMN);
            state.getColumns().put(a,c);
            column++;
        }

        //create stock
        state.getStock().add(0,allCards.get(cardIndex));
        cardIndex++;

        //create waste
        for (int w = 0 ; w < allCards.size()-cardIndex ; w++){
            state.getWaste().add(w,allCards.get(cardIndex+w));
        }

        //Create stack
        String[] arrr = {"SA","SB","SC","SD"};
        for (var a: arrr ) {
            var deck = new Deck();
            deck.setDeckType(DeckType.STACK);
            state.getStackPiles().put(a,deck);
        }

        //return the state
        return state;
    }

    private static List<Card> Shuffle(List<Card> deck, int iterations){
        Random r = new Random();

        for(int i = 0; i < iterations;i++){

            var ind1 = r.nextInt(deck.size());
            var ind2 = r.nextInt(deck.size());

            Card c = deck.get(ind1);
            deck.set(ind1,deck.get(ind2));
            deck.set(ind2,c);

        }

        return deck;
    }

    /**
     * Applies a score penalty to the provided GameState object based on the amount of time passed.
     * The following formula is applied : "duration of game in seconds" / 10 * -2
     *
     * @param gameState GameState object that the score penalty is applied to
     */
    public static void applyTimePenalty(GameState gameState){
        LocalDateTime startTime = gameState.getStartTime();
        LocalDateTime endTime = gameState.getEndTime();
        Duration duration = Duration.between(startTime, endTime);
        int totalTime = (int)duration.getSeconds();
        int timePenalty = totalTime / 10 * -2;
        gameState.setTimeScore(gameState.getTimeScore() + timePenalty);

        return;
    }

    /**
     * Applies a score bonus to the provided GameState object based on the amount of time passed. Assumes the game is won.
     * When the duration of the game is more than 30 seconds then apply : 700000 / "duration of game in seconds"
     *
     * @param gameState GameState object that the score penalty is applied to
     */
    public static void applyBonusScore(GameState gameState){
        LocalDateTime startTime = gameState.getStartTime();
        LocalDateTime endTime = gameState.getEndTime();
        Duration duration = Duration.between(startTime, endTime);
        int totalTime = (int)duration.getSeconds();
        int bonus;

        if (totalTime > 30){
            bonus = 700000 / totalTime;
            gameState.setTimeScore(gameState.getTimeScore() + bonus);

            return;
        }

        else {
            return;
        }
    }

    /**
     * Detects if the game has been won, and if so, sets the gameWon flag in the GameState object.
     * The game is considered won if there are no invisible cards left in the GameState object's columns and the stock
     * is empty.
     *
     * @param gameState GameState object of which it is determined if the game has been won
     */
    public static void detectGameWin(GameState gameState){
        Map<String, Deck> map = gameState.getColumns();
        String[] arr = {"A","B","C","D","E","F","G"};
        int totalInvisible = 0;
        for(int i = 0; i < arr.length;i++){
            totalInvisible += map.get(arr[i]).getInvisibleCards();
        }

        if(totalInvisible == 0 && gameState.getWaste().size() == 0 && gameState.getStock().size() == 0){
            gameState.setGameWon(true);
        }
    }
}
