import java.util.ArrayList;

public class JStrat implements Player {
    private char playerMark;
    private char enemyMark;

    public JStrat(char playerMark) {
        this.playerMark = playerMark;
        if (playerMark == 'X') {
            this.enemyMark = 'O';
        } else {
            this.enemyMark = 'X';
        }
    }

    @Override
    public Move makeMove(TicTacToe game) {

        return null;
    }

    private ArrayList<WinBy> potentialWinBy(TicTacToe game) {
        ArrayList<Integer> xPos = game.getXPos();
        ArrayList<Integer> oPos = game.getOPos();
        ArrayList<WinBy> winBy = new ArrayList<WinBy>();

        char[] xAndO = new char[9];
        for (int i = 0; i < 9; i++) {
            if (xPos.indexOf(i) != -1) {
                xAndO[i] = 'X';
            } else if (oPos.indexOf(i) != -1) {
                xAndO[i] = 'O';
            } else {
                xAndO[i] = ' ';
            }
        }

        if (hasTwoDiag(xAndO, playerMark) != Lines.NONE && hasTwoHori(xAndO, playerMark) != Lines.NONE && hasTwoVert(xAndO, playerMark) != Lines.NONE) {
            winBy.add(WinBy.FRIENDLY);
        }
        if (hasTwoDiag(xAndO, enemyMark) != Lines.NONE && hasTwoHori(xAndO, enemyMark) != Lines.NONE && hasTwoVert(xAndO, enemyMark) != Lines.NONE) {
            winBy.add(WinBy.ENEMY);
        }

        if (winBy.size() == 0) {
            winBy.add(WinBy.NONE);
            return winBy;
        } else {
            return winBy;
        }
    }

    private Lines hasTwoDiag(char[] xAndO, char mark) {
        if (xAndO[0] == mark && xAndO[4] == mark|| xAndO[4] == mark && xAndO[8] == mark || xAndO[0] == mark && xAndO[8] == mark) {
            return Lines.TL_BR_DIAG;
        } else if(xAndO[2] == mark && xAndO[4] == mark || xAndO[4] == mark && xAndO[6] == mark || xAndO[2] == mark && xAndO[6] == mark) {
            return Lines.TR_BL_DIAG;
        }
        return Lines.NONE;
    }

    private Lines hasTwoHori(char[] xAndO, char mark) {
        if (xAndO[0] == mark && xAndO[1] == mark || xAndO[1] == mark && xAndO[2] == mark || xAndO[0] == mark && xAndO[2] == mark) {
            return Lines.TOP_HOR;
        } else if (xAndO[3] == mark && xAndO[4] == mark || xAndO[4] == mark && xAndO[5] == mark || xAndO[3] == mark && xAndO[5] == mark) {
            return Lines.MID_HOR;
        } else if (xAndO[6] == mark && xAndO[7] == mark || xAndO[7] == mark && xAndO[8] == mark || xAndO[6] == mark && xAndO[8] == mark) {
            return Lines.BOT_HOR;
        }
        return Lines.NONE;
    }

    private Lines hasTwoVert(char[] xAndO, char mark) {
        if (xAndO[0] == mark && xAndO[3] == mark || xAndO[3] == mark && xAndO[6] == mark || xAndO[0] == mark && xAndO[6] == mark) {
            return Lines.LEFT_VERT;
        } else if (xAndO[1] == mark && xAndO[4] == mark || xAndO[4] == mark && xAndO[7] == mark || xAndO[1] == mark && xAndO[7] == mark) {
            return Lines.MID_VERT;
        } else if (xAndO[2] == mark && xAndO[5] == mark || xAndO[5] == mark && xAndO[8] == mark || xAndO[2] == mark && xAndO[8] == mark) {
            return Lines.RIGHT_VERT;
        }
        return Lines.NONE;
    }
}
