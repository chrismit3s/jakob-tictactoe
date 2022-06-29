import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.IllegalStateException;

class Bits {
    private static final int THREE = 3;
    private static final int EMPTY = 0, MASK = 3;
    public static final int X = 1, DRAW = 2, O = 3;

    private static boolean didPrecompute = false;
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

    public static Bits random() {
        Bits b = new Bits();
        Random rng = new Random();
        for (int row = 0; row < Bits.THREE; row++) {
            for (int col = 0; col < Bits.THREE; col++) {
                b.set(row, col, Bits.randomMark(rng));
            }
        }
        return b;
    }

    private static int randomMark(Random rng) {
        int i = rng.nextInt() & Integer.MAX_VALUE;
        switch (i % 3) {
            case 0: return Bits.EMPTY;
            case 1: return Bits.X;
            case 2: return Bits.O;
        }
        throw new IllegalStateException("unreachable");
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

    public static void clear() {
        Bits.didPrecompute = false;
        Bits.minimaxLookup.clear();
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
        // 0 1 2
        // 3 4 5
        // 6 7 8
        //
        // 0 +1
        // 3 +1
        // 6 +1
        //
        // 2 +2
        //
        // 0 +3
        // 1 +3
        // 2 +3
        //
        // 0 +4

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
            minimax = this.minimaxRec(isX);
            Bits.minimaxLookup.put(key, minimax);
        }
        return minimax;
    }

    private int minimaxRec(boolean isX) {
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

    public int minimaxUncached(boolean isX) {
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
            best = Bits.better(isX, this.minimaxUncached(!isX), best);
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

        // generate data
        final int N = 100000;
        Bits[] boards = new Bits[N];
        for (int i = 0; i < N; i++) {
            boards[i] = Bits.random();
        }

        long start, elapsed;

        // test checkWin
        start = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            boards[i].checkWin();
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("checkWin: " + elapsed + " ms");

        // test minimaxUncached
        Bits.clear();
        start = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            boards[i].minimaxUncached(true);
            boards[i].minimaxUncached(false);
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("minimaxUncached: " + elapsed + " ms");

        // test minimax
        Bits.clear();
        start = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            boards[i].minimax(true);
            boards[i].minimax(false);
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("minimax: " + elapsed + " ms");

        // test minimax
        Bits.precompute();
        start = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            boards[i].minimax(true);
            boards[i].minimax(false);
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("minimax (precomputed): " + elapsed + " ms");
    }
}

