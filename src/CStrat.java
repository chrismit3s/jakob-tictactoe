import java.lang.IllegalStateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.Console;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Board {
    public static final byte X = 1, DRAW = 2, O = 3;
    public static final byte EMPTY = 0, MASK = 3;
    public static final char[] MARK2SYMBOL = new char[] { ' ', 'X', '-', 'O' };
    public static final byte[] INT2MARK = new byte[] { EMPTY, X, O };
    public static final int THREE = 3;
    public static final int THREE_SQ = THREE * THREE;

    private static final int NUM_BOARDS = 1 << (2 * THREE_SQ + 1);
    private static final byte NOT_COMPUTED = 0;
    private static byte[] minimaxLookup = null;
    private static boolean didPrecompute = false;

    // WAYS TO WIN
    // 0 1 2
    // 3 4 5
    // 6 7 8
    //
    // ROWS
    // 0 +1 = 0 1 2
    // 3 +1 = 3 4 5
    // 6 +1 = 6 7 8
    //
    // UP DIAGONAL
    // 2 +2 = 2 4 6
    //
    // COLUMNS
    // 0 +3 = 0 3 6
    // 1 +3 = 1 4 7
    // 2 +3 = 2 5 8
    //
    // DOWN DIAGONAL
    // 0 +4 = 0 4 8
    private static int[] winTemplates = new int[] {
        // 8 7 6 5 4 3 2 1 0
        0b000000000000010101, // row0
        0b000000010101000000, // row1
        0b010101000000000000, // row2
        0b000001000100010000, // up diag
        0b000001000001000001, // col0
        0b000100000100000100, // col1
        0b010000010000010000, // col2
        0b010000000100000001, // down diag
    };
    private static int drawTemplate = 0b010101010101010101; // every cell

    private int field = 0;


    public Board() {
        Board.ensureLookup();
    }

    public Board(int field) {
        Board.ensureLookup();
        this.field = field;
    }

    public Board(TicTacToe game) {
        Board.ensureLookup();

        for (int x : game.getXPos()) {
            if (x == -1) continue;
            this.field |= Board.X << (2 * x);
        }
        for (int o : game.getOPos()) {
            if (o == -1) continue;
            this.field |= Board.O << (2 * o);
        }
    }


    public static Board fromInt(int x) {
        Board b = new Board();
        for (int i = 0; i < Board.THREE_SQ; i++) {
            b.set(i, Board.INT2MARK[x % Board.INT2MARK.length]);
            x /= Board.INT2MARK.length;
        }
        return b;
    }

    public static Board random() {
        return Board.fromInt(new Random().nextInt() & Integer.MAX_VALUE);
    }

    public static Board[] genRandom(int n) {
        Board[] boards = new Board[n];
        for (int i = 0; i < boards.length; i++) {
            boards[i] = Board.random();
        }
        return boards;
    }

    public static Board[] genAll() {
        int numBoards = 1;
        for (int i = 0; i < Board.THREE_SQ; i++) {
            numBoards *= Board.INT2MARK.length;
        }

        Board[] boards = new Board[numBoards];
        for (int i = 0; i < boards.length; i++) {
            boards[i] = Board.fromInt(i);
        }
        return boards;
    }


    private static void ensureLookup() {
        if (Board.minimaxLookup != null) {
            return;
        }

        Board.minimaxLookup = new byte[Board.NUM_BOARDS];
        Board.clear();
    }

    public static void precompute() {
        if (!Board.didPrecompute) {
            Board.didPrecompute = true;
            new Board().minimax(false);
            new Board().minimax(true);
        }
    }

    public static void clear() {
        Board.didPrecompute = false;
        Arrays.fill(Board.minimaxLookup, Board.NOT_COMPUTED);
    }

    private static byte better(boolean isX, byte a, byte b) {
        return (a < b == isX) ? a : b;
    }

    public static char symbol(byte mark) {
        return Board.MARK2SYMBOL[mark];
    }


    public void set(int pos, byte mark) {
        this.field &= ~(Board.MASK << (2 * pos));
        this.field |= mark << (2 * pos);
    }

    public void set(int row, int col, byte mark) {
        this.set(row * Board.THREE + col, mark);
    }

    public byte get(int pos) {
        return (byte)((this.field >> (2 * pos)) & Board.MASK);
    }

    public byte get(int row, int col) {
        return this.get(row * Board.THREE + col);
    }

    public byte checkWin() {
        for (int template : Board.winTemplates) {
            int winMask = template * Board.MASK;
            int xWin = template * Board.X;
            int oWin = template * Board.O;
            if ((this.field & winMask) == xWin) {
                return Board.X;
            }
            if ((this.field & winMask) == oWin) {
                return Board.O;
            }
        }
        int drawMask = Board.drawTemplate * (Board.X & Board.O);
        return ((this.field & drawMask) == drawMask) ? Board.DRAW : Board.EMPTY;
    }

    public List<Integer> bestMoves(boolean isX) {
        List<Integer> moves = new ArrayList<>(9);
        byte bestRet = this.minimax(isX);
        byte mark = isX ? Board.X : Board.O;
        for (int i = 0; i < Board.THREE_SQ; i++) {
            if (this.get(i) != Board.EMPTY) {
                continue;
            }

            this.set(i, mark);
            byte ret = this.minimax(!isX);
            if (ret == bestRet) {
                moves.add(i);
            }
            this.set(i, Board.EMPTY);
        }

        return moves;
    }

    public byte minimax(boolean isX) {
        int key = (this.field << 1) | (isX ? 1 : 0);
        byte minimax = Board.minimaxLookup[key];
        if (minimax == Board.NOT_COMPUTED) {
            minimax = this.minimaxRec(isX);
            Board.minimaxLookup[key] = minimax;
        }
        return minimax;
    }

    private byte minimaxRec(boolean isX) {
        byte winner = this.checkWin();
        if (winner != Board.EMPTY) {
            return winner;
        }

        byte mark = isX ? Board.X : Board.O;
        byte best = isX ? Board.O : Board.X;
        for (int i = 0; i < Board.THREE_SQ; i++) {
            if (this.get(i) != Board.EMPTY) {
                continue;
            }

            this.set(i, mark);
            best = Board.better(isX, this.minimax(!isX), best);
            this.set(i, Board.EMPTY);
        }

        return best;
    }

    public byte minimaxUncached(boolean isX) {
        byte winner = this.checkWin();
        if (winner != Board.EMPTY) {
            return winner;
        }

        byte mark = isX ? Board.X : Board.O;
        byte best = isX ? Board.O : Board.X;
        for (int i = 0; i < Board.THREE_SQ; i++) {
            if (this.get(i) != Board.EMPTY) {
                continue;
            }

            this.set(i, mark);
            best = Board.better(isX, this.minimaxUncached(!isX), best);
            this.set(i, Board.EMPTY);
        }

        return best;
    }

    public String toString() {
        String s = "[" + this.field + "]\n";

        for (int row = 0; row < Board.THREE; row++) {
            for (int col = 0; col < Board.THREE; col++) {
                s += Board.symbol(this.get(row, col));
                s += col + 1 == Board.THREE ? "\n" : " \u2502 ";
            }
            s += row + 1 == Board.THREE ? "" : "\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\n";
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
        long start = System.nanoTime();
        Board.precompute();
        long elapsed = System.nanoTime() - start;
        System.out.println("[CStrat] done - took " + (float)elapsed / 1000.0 + " us");
    }

    public Move makeMove(TicTacToe game) {
        List<Integer> moves = new Board(game).bestMoves(this.mark == 'X');
        int i = (this.rng.nextInt() & Integer.MAX_VALUE) % moves.size();
        return new Move(this.mark, moves.get(i));
    }


    public static void main(String[] args) {
        // javac -Xdiags:verbose -d build src/* && java -cp build CStrat
        if (args.length >= 1 && args[0].equals("perf")) {
            Board[] boards;
            if (args.length >= 2 && args[1].equals("full")) {
                boards = Board.genAll();
            } else {
                int n = (args.length >= 2) ? Integer.parseInt(args[1]) : 10000;
                boards = Board.genRandom(n);
            }
            perf(boards);
        } else if (args.length >= 1 && args[0].equals("test")) {
            Console c = System.console();
            for (;;) {
                Board b = Board.random();
                System.out.println(b);
                System.out.println("checkWin: " + Board.symbol(b.checkWin()));
                c.readLine("");
            }
        } else {
            Board b = new Board();
            b.set(0, Board.X);
            b.set(1, Board.X);
            b.set(2, Board.O);
            b.set(3, Board.O);
            b.set(4, Board.O);
            b.set(5, Board.X);
            b.set(6, Board.X);
            b.set(7, Board.X);
            System.out.println(b);
            System.out.println("Best outcome: " + b.minimax(false));
        }
    }

    public static void perf(Board[] boards) {
        long start, elapsed;

        // test checkWin
        start = System.currentTimeMillis();
        for (int i = 0; i < boards.length; i++) {
            boards[i].checkWin();
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("checkWin: " + elapsed + " ms");

        // test minimaxUncached
        Board.clear();
        start = System.currentTimeMillis();
        for (int i = 0; i < boards.length; i++) {
            boards[i].minimaxUncached(true);
            boards[i].minimaxUncached(false);
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("minimaxUncached: " + elapsed + " ms");

        // test minimax
        Board.clear();
        start = System.currentTimeMillis();
        for (int i = 0; i < boards.length; i++) {
            boards[i].minimax(true);
            boards[i].minimax(false);
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("minimax: " + elapsed + " ms");

        // test minimax
        Board.precompute();
        start = System.currentTimeMillis();
        for (int i = 0; i < boards.length; i++) {
            boards[i].minimax(true);
            boards[i].minimax(false);
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("minimax (precomputed): " + elapsed + " ms");
    }
}
