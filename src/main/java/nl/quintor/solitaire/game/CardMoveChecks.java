package nl.quintor.solitaire.game;

import nl.quintor.solitaire.game.moves.Help;
import nl.quintor.solitaire.game.moves.ex.MoveException;
import nl.quintor.solitaire.models.card.Card;
import nl.quintor.solitaire.models.card.Rank;
import nl.quintor.solitaire.models.card.Suit;
import nl.quintor.solitaire.models.deck.Deck;
import nl.quintor.solitaire.models.deck.DeckType;

import java.util.ArrayList;
import java.util.List;

/**
 * Library class for card move legality checks. The class is not instantiable, all constructors are private and all methods are
 * static. The class contains several private helper methods. All methods throw {@link MoveException}s, which can
 * contain a message that is fed to the {@link nl.quintor.solitaire.ui.UI}-implementation as error messages to be
 * shown to the user.
 */
public class CardMoveChecks {
    private CardMoveChecks(){}
    private final static String helpInstructions = new Help().toString();

    /**
     * Verifies that the player input for a CardMove is syntactically legal. Legal input consists of three parts:
     * the move command "M", the source location and the destination location.
     * The source location has to be the stock header, a stack header or a column coordinate.
     * The destination location has to be the stock header, a stack header or a column header (the column row is not
     * relevant because cards can only be added at the end of a column). The method verifies the syntax using regular
     * expressions.
     *
     * @param input the user input, split on the space character, cast to uppercase
     * @throws MoveException on syntax error
     */
    public static void checkPlayerInput(String[] input) throws MoveException{
        if (!isAllowedSource(input[1])){
            throw new MoveException("Invalid Move syntax. \"" + input[1] + "\" is not a valid source location.\nSee H̲elp for instructions.");
        }

        if(!isAllowedDestination(input[2])){
            throw new MoveException("Invalid Move syntax. \"" + input[2] + "\" is not a valid destination location.\nSee H̲elp for instructions.");
        }
    }

    /**
     * Check whether an input is a valid source
     * @param input string input
     * @return will return true if valid and false if not
     */
    static boolean isAllowedSource(String input) throws MoveException{
        //define stack and stock characters
        String[] st = {"SA","SB","SC","SD"};
        String stock = "O";

        //define stack list for easy comparison
        List<String> stacks = new ArrayList<>();
        for(var s : st){
            stacks.add(s);
        }

        boolean fine = false;

        //define regex for columns
        var regex = "[A-G][0-9]{1,2}";
        if(input.matches(regex)){
            fine = true;
        }


        //check if input is a stack?
        if(stacks.contains(input)){
            fine = true;
        }

        //check if the input is the stock
        if(stock.contains(input)){
            fine = true;

        }

        //return false bacuse its not a valid input
        return fine;
    }

    static boolean isAllowedDestination(String input){
        String[] co = {"A","B","C","D","E","F","G"};
        String[] st = {"SA","SB","SC","SD"};

        List<String> columns = new ArrayList<String>();
        List<String> stacks = new ArrayList<>();

        for (var c : co){
            columns.add(c);
        }

        for (var s : st){
            stacks.add(s);
        }

        if(columns.contains(input) || stacks.contains(input)){
            return true;
        }
        return false;
    }

    /**
     * Verifies that a card move is possible given the source deck, the source card index and the destination deck.
     * Assumes that the {@link #checkPlayerInput(String[])} checks have passed.
     * {@link Deck} objects have a {@link DeckType} that is used in this method. The rank and suit of the actual cards
     * are not taken into consideration here.
     *
     * @param sourceDeck deck that the card(s) originate from
     * @param sourceCardIndex index of the (first) card
     * @param destinationDeck deck that the card(s) will be transferred to
     * @throws MoveException on illegal move
     */
    public static void deckLevelChecks(Deck sourceDeck, int sourceCardIndex, Deck destinationDeck) throws MoveException {
        if(sourceDeck == destinationDeck){
            throw new MoveException("Move source and destination can't be the same");
        }

        var sourceType = sourceDeck.getDeckType();
        var destinationType = destinationDeck.getDeckType();

        if(sourceDeck.size() == 0){
            throw new MoveException("You can\'t move a card from an empty deck");
        }

        if(destinationType == DeckType.STOCK){
            throw new MoveException("You can\'t move cards to the stock");
        }

        if(sourceDeck.size()-sourceDeck.getInvisibleCards() < sourceDeck.size()-sourceCardIndex){
            throw new MoveException("You can\'t move an invisible card");
        }

        if(sourceCardIndex < sourceDeck.size()-1 && destinationType == DeckType.STACK){
            throw new MoveException("You can\'t move more than 1 card at a time to a Stack Pile");
        }
    }

    /**
     * Verifies that a card move is possible given the rank and suit of the card or first card to be moved. Assumes the
     * {@link #checkPlayerInput(String[])} and {@link #deckLevelChecks(Deck, int, Deck)} checks have passed. The checks
     * for moves to a stack pile or to a column are quite different, so the method calls one of two helper methods,
     * {@link #checkStackMove(Card, Card)} and {@link #checkColumnMove(Card, Card)}.
     *
     * @param targetDeck deck that the card(s) will be transferred to
     * @param cardToAdd (first) card
     * @throws MoveException on illegal move
     */
    public static void cardLevelChecks(Deck targetDeck, Card cardToAdd) throws MoveException {
        var targetType = targetDeck.getDeckType();

        if(targetType == DeckType.STACK){

            if(targetDeck.size() == 0){
                checkStackMove(null,cardToAdd);
            }else{
                checkStackMove(targetDeck.get(targetDeck.size()-1),cardToAdd);
            }

        }else if(targetType == DeckType.COLUMN) {

            if(targetDeck.size()==0){
                checkColumnMove(null,cardToAdd);
            }else{

                checkColumnMove(targetDeck.get(targetDeck.size()-1),cardToAdd);

            }

        }else{
            throw new MoveException("Target deck is neither Stack nor Column.");
        }
    }

    // Helper methods

    /**
     * Converts rank to an int for easy comparison
     *
     * @param r Rank to be converted
     * @return Converted rank
     */
    static int rankToInt(Rank r){
        int i = 0;

        switch (r) {
            case ACE:
                i = 0;
                break;
            case TWO:
                i = 1;
                break;
            case THREE:
                i = 2;
                break;
            case FOUR:
                i = 3;
                break;
            case FIVE:
                i = 4;
                break;
            case SIX:
                i = 5;
                break;
            case SEVEN:
                i = 6;
                break;
            case EIGHT:
                i = 7;
                break;
            case NINE:
                i = 8;
                break;
            case TEN:
                i = 9;
                break;
            case JACK:
                i = 10;
                break;
            case QUEEN:
                i = 11;
                break;
            case KING:
                i = 12;
                break;
        }

        return i;
    }

    /**
     * Verifies that the proposed move is legal given that the targetCard is the top of a stack pile.
     *
     * @param targetCard top card of a stack or null if the stack is empty
     * @param cardToAdd card to add to the stack
     * @throws MoveException on illegal move
     */
    static void checkStackMove(Card targetCard, Card cardToAdd) throws MoveException {
        if(targetCard == null){
            if(cardToAdd.getRank() != Rank.ACE){
                throw new MoveException("An Ace has to be the first card of a Stack Pile");
            }
        }else{
            if(rankToInt(targetCard.getRank()) + 1 != rankToInt(cardToAdd.getRank())){
                throw new MoveException("Stack Piles hold same-suit cards of increasing Rank from Ace to King");
            }else{
                if(targetCard.getSuit() != cardToAdd.getSuit()){
                    throw new MoveException("Stack Piles can only contain same-suit cards");
                }
            }
        }
    }

    /**
     * Verifies that the proposed move is legal given that the targetCard is the last card of a column.
     *
     * @param targetCard last card of a column or null if the column is empty
     * @param cardToAdd card to add to the column
     * @throws MoveException on illegal move
     */
    static void checkColumnMove(Card targetCard, Card cardToAdd) throws MoveException {
        if (targetCard == null) {
            if(cardToAdd.getRank() != Rank.KING){
                throw new MoveException("A King has to be the first card of a Column");
            }
        }else if(!opposingColor(targetCard,cardToAdd)){
            throw new MoveException("Column cards have te alternate colors (red and black)");
        }else{
            if(rankToInt(targetCard.getRank()) != rankToInt(cardToAdd.getRank()) + 1){
                throw new MoveException("Columns hold alternating-color cards of decreasing rank from King to Two");
            }
        }
    }

    /**
     * Helper method to determine if the provided cards are of opposing color (red versus black).
     *
     * @param card1 first card
     * @param card2 second card
     * @return true if the cards are of different colors
     */
    static boolean opposingColor(Card card1, Card card2){
        return redSuit(card1) != redSuit(card2);
    }

    /**
     * Helper method to determine if the card's suit is colored red (Diamonds or Hearts).
     *
     *
     * @param card card to be tested for red color
     * @return true if card is either of suit Diamonds or Hearts
     * @throws RuntimeException exception when Joker card is checked with message 'Method redSuit() should not be used with Jokers'
     */
    static boolean redSuit(Card card){
        if (card.getSuit() == Suit.JOKER) throw new RuntimeException("Method redSuit() should not be used with Jokers");
        return card.getSuit() == Suit.DIAMONDS || card.getSuit() == Suit.HEARTS;
    }
}
