public class Move {
    private String mark = "";
    private int sector;
    
    public Move(String mark, int sector) {
        this.mark = mark;
        this.sector = sector;
    }

    public String getMark() {
        return this.mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getSector() {
        return this.sector;
    }

    public void setSector(int sector) {
        this.sector = sector;
    }
}
