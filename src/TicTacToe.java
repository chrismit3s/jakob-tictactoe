import java.util.ArrayList;

public class TicTacToe {
    private ArrayList<Integer> xPos = new ArrayList<Integer>();
    private ArrayList<Integer> oPos = new ArrayList<Integer>();
    private final String X = "X";
    private final String O = "O";
    private int counter = 0;

    public static void main(String[] args) {
        TicTacToe game = new TicTacToe();
        game.init();
        game.gameloop(game);
    }

    public TicTacToe() {
        for (int i = 0; i < 9; i++) {
            xPos.add(-1);
            oPos.add(-1);
        }
    }

    private void init() {
        System.out.println("Welcome to this console-version of TicTacToe!");
        System.out.println("Player One will be 'X' and Player Two will be 'O'.");
        System.out.println("Player One will now start.\n ----------------------------------------------------");
    }

    private void gameloop(TicTacToe game) {
        while (!game.checkVictory()[0]) {
            if (counter % 2 == 0) {
                System.out.println("Player One's turn:");
            } else {
                System.out.println("Player Two's turn:");
            }

            System.out.println(game.toString());
            System.out.println("\nSelect a Sector between 0 and 8 to place your mark: ");
            String s = System.console().readLine();
            Integer input = Integer.parseInt(s);
            if (input < 0 || input > 8) {
                //throw new Exception("Your input(" + input + ") was faulty and did not match any of the sectors!");
            } else if (xPos.indexOf(input) != -1 || oPos.indexOf(input) != -1) {
                //throw new Exception("You tried to place a mark in an already marked sector!");
            }
            game.placeMark(input, counter);
            counter++;
        }
        if (game.checkVictory()[1]) {
            System.out.println("Player One won!!!");
            System.out.println("\n" + game.toString());
        } else {
            System.out.println("Player Two won!!!");
            System.out.println("\n" + game.toString());
        }
    }

    private boolean[] checkVictory() {
        String[] xAndO = new String[9];
        for (int i = 0; i < 9; i++) {
            if (xPos.indexOf(i) != -1) {
                xAndO[i] = X;
            } else if (oPos.indexOf(i) != -1) {
                xAndO[i] = O;
            } else {
                xAndO[i] = "";
            }
        }

        if (hasHorizontal(X, xAndO) || hasVertical(X, xAndO) || hasDiagonal(X, xAndO)) {
            return new boolean[]{true, true};
        } else if (hasHorizontal(O, xAndO) || hasVertical(O, xAndO) || hasDiagonal(O, xAndO)) {
            return new boolean[]{true, false};
        }
        return new boolean[]{false, true};
    }
    
    private boolean hasHorizontal(String x, String[] xAndO) {
        if (xAndO[0] == x && xAndO[1] == x && xAndO[2] == x || 
        xAndO[3] == x && xAndO[4] == x && xAndO[5] == x ||
        xAndO[6] == x && xAndO[7] == x && xAndO[8] == x) {
            return true;
        }
        return false;
    }

    private boolean hasVertical(String x, String[] xAndO) {
        if (xAndO[0] == x && xAndO[3] == x && xAndO[6] == x || 
        xAndO[1] == x && xAndO[4] == x && xAndO[7] == x ||
        xAndO[2] == x && xAndO[5] == x && xAndO[8] == x) {
            return true;
        }
        return false;
    }

    private boolean hasDiagonal(String x, String[] xAndO) {
        if (xAndO[0] == x && xAndO[4] == x && xAndO[8] == x ||
        xAndO[2] == x && xAndO[4] == x && xAndO[6] == x) {
            return true;
        }
        return false;
    }

    private void placeMark(int input, int counter) {
        if (counter % 2 == 0) {
            xPos.set(input, input);
        } else {
            oPos.set(input, input);
        }
    }

    public String toString() {
        String result = "";
        int sectorCounter = 0;
        for (; sectorCounter < 9; sectorCounter++) {
            result = result + " ";
            if (xPos.get(sectorCounter) == sectorCounter && oPos.get(sectorCounter) == sectorCounter) {
                //throw new Exception("'O' and 'X' are in the same spot!");
            } else if (xPos.get(sectorCounter) == sectorCounter) {
                result = result + X + " ";
            } else if (oPos.get(sectorCounter) == sectorCounter) {
                result = result + O + " ";
            } else {
                result = result + "  ";
            }

            if (sectorCounter == 8) {
                return result;
            } else if (sectorCounter % 3 == 2) {
                result = result + "\n---+---+---\n";
            } else {
                result = result + "|";
            }
        }
        return result;
    }
}