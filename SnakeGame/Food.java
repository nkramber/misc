package SnakeGame;

import java.util.List;

public class Food {
    int x, y;

    public Food(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void spawnNewFood(List<SnakeCell> snakeCells) {
        boolean validLocation = true;
        int newX, newY;

        do {
            newX = SnakeGame.r.nextInt(SnakeGame.getMapWidth());
            newY = SnakeGame.r.nextInt(SnakeGame.getMapHeight());

            for (SnakeCell cell : snakeCells) {
                if (cell.getX() == newX && cell.getY() == newY) validLocation = false;
            }
        } while (!validLocation);

        setX(newX);
        setY(newY);
    }

    private void setX(int x) { this.x = x; }
    private void setY(int y) { this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
}