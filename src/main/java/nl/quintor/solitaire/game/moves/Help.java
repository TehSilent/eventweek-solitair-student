package nl.quintor.solitaire.game.moves;

import nl.quintor.solitaire.models.state.GameState;

/**
 * Class that represents a player action to view the game instructions.
 */
public class Help implements Move{
    private final static String name = System.getProperty("os.name").contains("Windows") ? "H̲elp" : "H̲elp";

    /**
     * Returns the help information for the UI to show the player. Does not influence the {@link GameState}.
     *
     * @param gameState GameState object, which is ignored
     * @return help information
     */
    @Override
    public String apply(GameState gameState) {
        return "\n"+
                "You can give a command with the Capital letter followed by <Return>,\n" +
                "so in order to display these instructions, simply type \"H + <Return>\"!\n" +
                "You can simply hit <Return> to repeat the last command you entered.\n" +
                "The H̲elp command requires arguments. The syntax is: \n" +
                "\n" +
                "M Source Destination (case insensitive)\n" +
                "\n" +
                "For example:\n" +
                "\"M O SA\" moves the top card from the Stock to the top of Stack Pile A\n" +
                "\"M SB F\" moves the top card from Stack Pile B to the end of Column F\n" +
                "\n" +
                "Dutch Patience rules: http://www.patiencespel.nl/patiencespelregels.php\n" +
                "English Patience rules: http://digsolitaire.com/solitaire-rules.php\n";
    }

    @Override
    public Move createInstance(String playerInput) {
        return new Help();
    }

    @Override
    public String toString() {
        return name;
    }
}
