package nl.quintor.solitaire.ui.cli;

import nl.quintor.solitaire.models.deck.Deck;
import nl.quintor.solitaire.models.state.GameState;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * {@link GameState} parser for terminal printing. The class is not instantiable, all constructors are private.
 */
class GameStateParser {
    private final static int COLUMN_WIDTH = 8; // 8 columns in 64 char width (80 char width is Windows default)
    private final static int FIRST_COLUMN_WIDTH = 3;

    protected GameStateParser(){}

    /**
     * Parses {@link GameState} to a String representation for terminal printing.
     *
     * <pre>{@code
     * Example:
     *
     * 0 moves played in 00:29 for 0 points
     *
     *     O                      SA      SB      SC      SD
     *    ♤ 9                     _ _     _ _     _ _     _ _
     *
     *     A       B       C       D       E       F       G
     *  0 ♦ 6     ? ?     ? ?     ? ?     ? ?     ? ?     ? ?
     *  1         ♤ 8     ? ?     ? ?     ? ?     ? ?     ? ?
     *  2                 ♦ 7     ? ?     ? ?     ? ?     ? ?
     *  3                         ♤ 6     ? ?     ? ?     ? ?
     *  4                                 ♤ K     ? ?     ? ?
     *  5                                         ♧ 2     ? ?
     *  6                                                 ♥ 6
     *  7
     *  }</pre>
     *
     *  @param gameState a representation of the current state of the game
     *  @return a visual representation of the gameState (for monospace terminal printing)
     */
    static String parseGameState(GameState gameState){
        //TODO refactor
        StringBuilder builder = new StringBuilder();

        // moves play time points
        var moves = Integer.toString(gameState.getMoves().size());

        if(gameState.getEndTime() == null){
            gameState.setEndTime(LocalDateTime.now());
        }

        LocalDateTime startTime = gameState.getStartTime();
        LocalDateTime endTime = gameState.getEndTime();
        Duration duration = Duration.between(startTime, endTime);

        long totalTime = duration.getSeconds();

        String hours = Long.toString(totalTime/3600L);
        if(hours.length() == 1){
            hours = "0"+hours;
        }

        String minutes = Long.toString((totalTime/60L)%60L);
        if(minutes.length() == 1){
            minutes = "0"+minutes;
        }

        String seconds = Long.toString(totalTime%60L);
        if(seconds.length() == 1){
            seconds = "0"+seconds;
        }

        String timeScore = hours + ":" + minutes + ":" + seconds;

        var score = Long.toString(gameState.getScore());

        builder.append(moves + " move(s) played in " + timeScore + " for " + score + " points\n");

        //space
        builder.append("\n");

        //O and stack names
        padNAdd(builder,"",3);
        padNAdd(builder,"O (" + Integer.toString(gameState.getWaste().size()+1) + ")",24);
        padNAdd(builder,"SA",8);
        padNAdd(builder,"SB",8);
        padNAdd(builder,"SC",8);
        padNAdd(builder,"SD",8);
        builder.append("\n");

        //stock stacks
        padNAdd(builder,"",3);
        var stock = gameState.getStock();
        padNAdd(builder,getCardStringOrNull(stock,stock.size()-1),24);

        var stacks = gameState.getStackPiles();
        String[] stackNames = {"SA","SB","SC","SD"};
        for(int stack = 0; stack < stacks.size();stack++){
            var deck = stacks.get(stackNames[stack]);

            String cardString = getCardStringOrNull(deck,deck.size()-1);

            if(cardString == null){

                cardString = "_ _";

            }

            padNAdd(builder,cardString,8);

        }
        builder.append("\n");

        //space
        builder.append("\n");

        //column names
        padNAdd(builder,"",3);

        String[] columnNames = {"A","B","C","D","E","F","G"};
        for(var a : columnNames){
            padNAdd(builder,a,8);
        }
        builder.append("\n");

        //columns with row nrs
        int row = 0;
        int max = 0;
        var columns = gameState.getColumns();
        while(row <= max) {
            padNAdd(builder, Integer.toString(row), 3);
            for (var a : columnNames) {
                if (columns.get(a).size() > max) {
                    max = columns.get(a).size();
                }
            }
            printRow(builder, columns.values(), row);
            builder.append("\n");
            row++;
        }

        return builder.toString();
    }

    /**
     * Add a String representation of the requested row of all provided columns to the provided StringBuilder. If the
     * requested row did not contain any cards, return false, else true.
     * This method uses the padAndAdd @see{{@link #padNAdd(StringBuilder, String, int)}}
     * Invisible cards should be printed as "? ?"
     *
     * @param builder contains the visualization of the game state
     * @param columns the columns of which the row is printed
     * @param row the row of the columns to be printed
     * @return did the row contain any cards
     */
    protected static boolean printRow(StringBuilder builder, Collection<Deck> columns, int row){

        boolean notAllNull = false;
        int maxSize = 0;

        for (var c : columns){
            if(maxSize <= c.size()){
                maxSize = c.size();
            }

            if(c.size() != 0) {
                notAllNull = true;
            }

            String s = getCardStringOrNull(c,row);

            if(s == null){
                s = "";
            }else if(c.getInvisibleCards()>row){
                s = "? ?";
            }

            padNAdd(builder,s,8);
        }

        if(maxSize < row){
            return false;
        }

        return notAllNull;
    }

    /**
     * Attempts to get the specified card from the deck, and returns null if the requested index is out of bounds.
     *
     * @param deck deck to get the card from
     * @param index index of the card to get
     * @return the requested card or null
     */
    protected static String getCardStringOrNull(Deck deck, int index){
        if(deck == null || deck.size()<=index || index < 0){
            return null;
        }

        return deck.get(index).toShortString();
    }

    /**
     * Add a space to the left of the string if it is of length 1, then add spaces to the right until it is of size
     * totalLength. Append the result to the StringBuilder.
     *
     * @param builder StringBuilder to append the result to
     * @param string String to pad and append
     * @param totalLength The total length that the String must become
     */
    protected static void padNAdd(StringBuilder builder, String string, int totalLength){
        // TODO: Write implementation
        if(string.length() == 1){
            string = " " + string;
            builder.append(string);
            for (int i=0 ; i<totalLength - string.length() ; i++){
                builder.append(" ");
            }
        }

        else{
            builder.append(string);
            for (int i=0 ; i<totalLength - string.length() ; i++){
                builder.append(" ");
            }
        }
    }
}
