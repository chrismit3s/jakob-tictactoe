public class ConsolePlayer implements Player {
    private final int MAX_TRIES = 5;
    private int tryCounter = 0;
    private char playerMark;

    public ConsolePlayer(char playerMark) {
        this.playerMark = playerMark;
    }

    // This method is supposed to take an input from the Player, decide wether it's legal or not
    // and depending on that return a move to the TicTacToe class, which then uses this information
    // to place the mark on the canvas.
    @Override
    public Move makeMove(TicTacToe game) {
        // Getting the Player's input
        System.out.println("It's Player " + playerMark + "'s turn! Make a move: ");
        String s = System.console().readLine();
        Integer input = Integer.parseInt(s);
        
        if (game.checkMoveViability(input)) {
            // Making a move if the playerNr given to this object matches 1 or 2, exiting otherwise.
            if (playerMark == 'X' || playerMark == 'O') {
                Move move = new Move(playerMark, input);
                return move;
            } else {
                System.out.println("There are less than 1 Players or more than 2. Exiting now...");
                System.exit(0);
            }
        } else {
            // Giving the Player another chance to make a correct input if a faulty one has been inputted. Max 5 tries.
            if (tryCounter != MAX_TRIES) {
                System.out.println("You either made a faulty input or the sector you're\ntrying to mark has already been marked. Please try again!");
                tryCounter++;
                return makeMove(game);
            } else {
                System.out.println("Are you square brain? Just start a new game...");
                System.exit(0);
            }

        }
        return null;
    }

    public char getPlayerMark() {
        return this.playerMark;
    }

    public void setPlayerNr(char playerMark) {
        this.playerMark = playerMark;
    }
}
