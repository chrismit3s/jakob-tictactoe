public class Move {
    private char mark = ' ';
    private int sector;
    
    public Move(char mark, int sector) {
        this.mark = mark;
        this.sector = sector;
    }

    public char getMark() {
        return this.mark;
    }

    public void setMark(char mark) {
        this.mark = mark;
    }

    public int getSector() {
        return this.sector;
    }

    public void setSector(int sector) {
        this.sector = sector;
    }
}
