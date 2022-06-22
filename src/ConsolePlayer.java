import java.util.ArrayList;

public class ConsolePlayer implements Player {
    private final int MAX_TRIES = 5;
    private int tryCounter = 0;
    private int playerNr;

    public ConsolePlayer(int playerNr) {
        this.playerNr = playerNr;
    }

    // This method is supposed to take an input from the Player, decide wether it's legal or not
    // and depending on that return a move to the TicTacToe class, which then uses this information
    // to place the mark on the canvas.
    @Override
    public Move makeMove(TicTacToe game) {
        // Getting the Player's input
        System.out.println("It's P" + playerNr + "'s turn! Make a move: ");
        String s = System.console().readLine();
        Integer input = Integer.parseInt(s);
        
        if (checkMoveViability(game, input)) {
            // Making a move if the playerNr given to this object matches 1 or 2, exiting otherwise.
            if (playerNr == 1) {
                Move move = new Move("X", input);
                return move;
            } else if (playerNr == 2) {
                Move move = new Move("O", input);
                return move;
            } else {
                System.out.println("There are less than 1 Players or more than 2. Exiting now...");
                System.exit(0);
            }
        } else {
            // Giving the Player another chance to make a correct input if a faulty one has been inputted. Max 5 tries.
            if (tryCounter != MAX_TRIES) {
                System.out.println("You either made a faulty input or the sector you're\n trying to mark has already been marked. Please try again!");
                tryCounter++;
                return makeMove(game);
            } else {
                System.out.println("Are you square brain? Just start a new game...");
                System.exit(0);
            }

        }
        return null;
    }

    private boolean checkMoveViability(TicTacToe game, int input) {
        ArrayList<Integer> xPos = game.getXPos();
        ArrayList<Integer> oPos = game.getOPos();

        // Checking for faulty inputs(input /e [0, 8] or already taken).
        if (input < 0 || input > 8) {
            return false;
        } else if (xPos.indexOf(input) != -1 || oPos.indexOf(input) != -1) {
            return false;
        }
        return true;
    }

    public int getPlayerNr() {
        return this.playerNr;
    }

    public void setPlayerNr(int playerNr) {
        this.playerNr = playerNr;
    }
}
