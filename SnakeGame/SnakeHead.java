package SnakeGame;

public class SnakeHead extends SnakeCell {
    private int xDir, yDir, newXDir, newYDir;

    public SnakeHead(int x, int y) {
        super(x, y);
        // if (r.nextBoolean()) xDir = r.nextBoolean() ? 1 : -1;
        // else yDir = r.nextBoolean() ? 1 : -1;

        xDir = -1;
        yDir = 0;

        newXDir = newYDir = 0;
    }

    public void move() {
        if (newXDir != 0 && xDir == 0) setDir(newXDir, 0);
        if (newYDir != 0 && yDir == 0) setDir(0, newYDir);

        int newX = getX() + getXDir();
        int newY = getY() + getYDir();

        if ((newX < 0 || newX >= SnakeGame.getMapWidth()) || (newY < 0 || newY >= SnakeGame.getMapHeight())) SnakeGame.gameOver();

        setX(newX);
        setY(newY);
    }

    public boolean collides(SnakeCell cell) {
        return (getX() == cell.getX() && getY() == cell.getY());
    }

    public boolean collides(Food food) {
        return (getX() == food.getX() && getY() == food.getY());
    }

    public void setDir(int xDir, int yDir) {
        this.xDir = xDir;
        this.yDir = yDir;
    }

    public void setNextDir(int newXDir, int newYDir) {
        this.newXDir = newXDir;
        this.newYDir = newYDir;
    }

    public int getXDir() { return xDir; }
    public int getYDir() { return yDir; }
}