public class ConsolePlayer implements Player {
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

        // Making a move if the playerNr given to this object matches 1 or 2, exiting otherwise.
        if (playerMark == 'X' || playerMark == 'O') {
            Move move = new Move(playerMark, input);
            return move;
        } else {
            System.out.println("There are less than 1 Players or more than 2. Exiting now...");
            System.exit(0);
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
