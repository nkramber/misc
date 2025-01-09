package SnakeGame;

public class SnakeCell {
    private int x, y, oldX, oldY;

    public SnakeCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getOldX() { return oldX; }
    public int getOldY() { return oldY; }

    public void setX(int x) { 
        oldX = this.x;
        this.x = x;
    }

    public void setY(int y) {
        oldY = this.y;
        this.y = y;
    }
}