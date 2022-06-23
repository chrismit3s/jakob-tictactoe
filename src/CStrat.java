import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Bits {
    private static final int THREE = 3;
    private static final int EMPTY = 0, MASK = 3;
    public static final int X = 1, DRAW = 2, O = 3;

    private static boolean didPrecompute = false;
    private static final Map<Integer, Integer> winLookup = new HashMap<>();
    private static final Map<Integer, Integer> minimaxLookup = new HashMap<>();

    private int field = 0;


    public Bits() {}

    public Bits(TicTacToe game) {
        for (int x : game.getXPos()) {
            this.field |= Bits.X << (2 * x);
        }
        for (int o : game.getOPos()) {
            this.field |= Bits.O << (2 * o);
        }
    }


    private static int better(boolean isX, int a, int b) {
        return (a < b == isX) ? a : b;
    }

    public static char symbol(int mark) {
        switch (mark) {
            case Bits.X:     return 'X';
            case Bits.O:     return 'O';
            case Bits.EMPTY: return ' ';
            default:         return '!';
        }
    }

    public static void precompute() {
        if (!Bits.didPrecompute) {
            Bits.didPrecompute = true;
            new Bits().minimax(false);
            new Bits().minimax(true);
        }
    }


    public void set(int pos, int mark) {
        this.field &= ~(Bits.MASK << (2 * pos));
        this.field |= mark << (2 * pos);
    }

    public void set(int row, int col, int mark) {
        this.set(row * Bits.THREE + col, mark);
    }

    public int get(int pos) {
        return (int)((this.field >> (2 * pos)) & Bits.MASK);
    }

    public int get(int row, int col) {
        return this.get(row * Bits.THREE + col);
    }

    public int checkWin() {
        Integer win = Bits.winLookup.get(this.field);
        if (win == null) {
            win = this.checkWin_();
            Bits.winLookup.put(this.field, win);
        }
        return win;
    }

    private int checkWin_() {
        for (int i = 0; i < Bits.THREE; i++) {
            int markRow = this.get(i, 0);
            if (markRow == this.get(i, 1) && markRow == this.get(i, 2)) {
                return markRow;
            }

            int markCol = this.get(0, i);
            if (markCol == this.get(1, i) && markCol == this.get(2, i)) {
                return markCol;
            }
        }

        int markDiagUp = this.get(0, 0);
        if (markDiagUp == this.get(1, 1) && markDiagUp == this.get(2, 2)) {
            return markDiagUp;
        }

        int markDiagDown = this.get(0, 2);
        if (markDiagDown == this.get(1, 1) && markDiagDown == this.get(2, 0)) {
            return markDiagDown;
        }

        int field = this.field;
        for (int i = 0; i < Bits.THREE * Bits.THREE; i++) {
            if ((field & Bits.MASK) == Bits.EMPTY) {
                return Bits.EMPTY;
            }
            field >>= 2;
        }

        return Bits.DRAW;
    }

    public List<Integer> bestMoves(boolean isX) {
        List<Integer> moves = new ArrayList<>(9);
        int bestRet = this.minimax(isX);
        int mark = isX ? Bits.X : Bits.O;
        for (int i = 0; i < Bits.THREE * Bits.THREE; i++) {
            if (this.get(i) != Bits.EMPTY) {
                continue;
            }

            this.set(i, mark);
            int ret = this.minimax(!isX);
            if (ret == bestRet) {
                moves.add(i);
            }
            this.set(i, Bits.EMPTY);
        }

        return moves;
    }

    public int minimax(boolean isX) {
        int key = (this.field << 1) | (isX ? 1 : 0);
        Integer minimax = Bits.minimaxLookup.get(key);
        if (minimax == null) {
            minimax = this.minimax_(isX);
            Bits.minimaxLookup.put(key, minimax);
        }
        return minimax;
    }

    private int minimax_(boolean isX) {
        int winner = this.checkWin();
        if (winner != Bits.EMPTY) {
            return winner;
        }

        int mark = isX ? Bits.X : Bits.O;
        int best = isX ? Bits.O : Bits.X;
        for (int i = 0; i < Bits.THREE * Bits.THREE; i++) {
            if (this.get(i) != Bits.EMPTY) {
                continue;
            }

            this.set(i, mark);
            best = Bits.better(isX, this.minimax(!isX), best);
            this.set(i, Bits.EMPTY);
        }

        return best;
    }

    public String toString() {
        String s = "";

        for (int row = 0; row < Bits.THREE; row++) {
            for (int col = 0; col < Bits.THREE; col++) {
                s += Bits.symbol(this.get(row, col));
                s += col + 1 == Bits.THREE ? "\n" : " \u2502 ";
            }
            s += row + 1 == Bits.THREE ? "" : "\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\n";
        }

        return s;
    }
}

public class CStrat implements Player {
    private Random rng = new Random();
    private char mark;

    public CStrat(char mark) {
        this.mark = mark;
        System.out.println("[CStrat] Starting precompute...");
        Bits.precompute();
        System.out.println("[CStrat] done");
    }

    public Move makeMove(TicTacToe game) {
        List<Integer> moves = new Bits(game).bestMoves(this.mark == 'X');
        int i = (this.rng.nextInt() & Integer.MAX_VALUE) % moves.size();
        return new Move(this.mark, moves.get(i));
    }

    public static void main(String[] args) {
        // javac -Xdiags:verbose -d build src/* && java -cp build CStrat
        Bits b = new Bits();
        b.set(1, 1, Bits.X);
        System.out.println(b);
        List<Integer> moves = b.bestMoves(false);
        System.out.print("[ ");
        for (int m : moves) {
            System.out.print(m);
            System.out.print(" ");
        }
        System.out.println("]");
    }
}

