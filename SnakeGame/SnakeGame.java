package SnakeGame;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SnakeGame extends Canvas implements Runnable, KeyListener {

    private static JFrame frame;
    public static Random r;
    private static final String TITLE = "Snake";
    private static final int MAP_WIDTH = 20;
    private static final int MAP_HEIGHT = 15;
    private static final int TILE_SIZE = 24;
    private static final int SIDE_BORDER = 35;
    private static final int BOTTOM_BORDER = SIDE_BORDER;
    private static final int TOP_BORDER_SCALE = 3;
    private static final int TOP_BORDER = BOTTOM_BORDER * TOP_BORDER_SCALE;
    private static final int SCREEN_WIDTH = MAP_WIDTH * TILE_SIZE + SIDE_BORDER * 2;
    private static final int SCREEN_HEIGHT = MAP_HEIGHT * TILE_SIZE + TOP_BORDER + BOTTOM_BORDER;
    private static final int MENU_WIDTH = 400;
    private static final int MENU_HEIGHT = 350;
    private static final double TARGET_FPS = 60.0;
    private static final double TIME_BETWEEN_TICKS = 1000000000 / TARGET_FPS;

    private static boolean running;
    private static boolean paused;
    private static boolean gameOver;
    private static boolean atMenu;
    private static int score;
    private static int speed;
    private static int ticks = 0;
    private static int gameUpdateRate;
    private static HashMap<Integer, int[]> gameUpdateRates;

    private BufferedImage display;
    private boolean keys[];
    private List<SnakeCell> snakeCells = new ArrayList<>();
    private SnakeHead head;
    private Food food;

    private void start() {
        requestFocus();
        running = true;
        new Thread(this).start();
    }

    private void init() {
        display = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        r = new Random();
        keys = new boolean[256];

        gameUpdateRates = new HashMap<>();
        gameUpdateRates.put(0, new int[]{12, 1});
        gameUpdateRates.put(5, new int[]{10, 2});
        gameUpdateRates.put(10, new int[]{9, 3});
        gameUpdateRates.put(15, new int[]{8, 4});
        gameUpdateRates.put(20, new int[]{7, 5});
        gameUpdateRates.put(30, new int[]{6, 6});
        gameUpdateRates.put(45, new int[]{5, 7});
        gameUpdateRates.put(60, new int[]{4, 8});
        gameUpdateRates.put(80, new int[]{3, 9});
        gameUpdateRates.put(100, new int[]{2, 10});

        atMenu = true;
        addKeyListener(this);
    }

    private void newGame() {
        gameOver = false;
        paused = false;
        atMenu = false;
        score = 0;
        snakeCells.clear();
        gameUpdateRate = gameUpdateRates.get(score)[0];
        speed = gameUpdateRates.get(score)[1];

        int xStart = MAP_WIDTH / 4 + r.nextInt(MAP_WIDTH / 2);
        int yStart = MAP_HEIGHT / 4 + r.nextInt(MAP_HEIGHT / 2);
        head = new SnakeHead(xStart, yStart);
        snakeCells.add(head);

        food = new Food(r.nextInt(MAP_WIDTH), r.nextInt(MAP_HEIGHT));
    }

    private void stop() {
        running = false;
    }

    @Override
    public void run() {
        init();
        double lastUpdateTime = System.nanoTime();

        while (running) {
            double time = System.nanoTime();
            while (time - lastUpdateTime > TIME_BETWEEN_TICKS) {
                tick();
                render();
                lastUpdateTime += TIME_BETWEEN_TICKS;
            }
            Thread.yield();
        }

        System.exit(0);
    }

    private void tick() {
        ticks++;
        getInput();
        if (ticks >= gameUpdateRate && !gameOver && !paused && !atMenu) {
            ticks = 0;
            head.move();
            for (int i = 1; i < snakeCells.size(); i++) {
                snakeCells.get(i).setX(snakeCells.get(i - 1).getOldX());
                snakeCells.get(i).setY(snakeCells.get(i - 1).getOldY());
                if (head.collides(snakeCells.get(i))) gameOver();
            }

            if (head.collides(food)) {
                snakeCells.add(new SnakeCell(snakeCells.get(snakeCells.size() - 1).getOldX(), snakeCells.get(snakeCells.size() - 1).getOldY()));
                food.spawnNewFood(snakeCells);
                score++;
                if (gameUpdateRates.containsKey(score)) {
                    gameUpdateRate = gameUpdateRates.get(score)[0];
                    speed = gameUpdateRates.get(score)[1];
                }
            }
        }
    }

    private void getInput() {
        if (!paused && !gameOver) {
            if (keys[KeyEvent.VK_UP]) head.setNextDir(0, -1);
            else if (keys[KeyEvent.VK_DOWN]) head.setNextDir(0, 1);
            else if (keys[KeyEvent.VK_LEFT]) head.setNextDir(-1, 0);
            else if (keys[KeyEvent.VK_RIGHT]) head.setNextDir(1, 0);
            else if (keys[KeyEvent.VK_ESCAPE]) {
                keys[KeyEvent.VK_ESCAPE] = false;
                pause();
            }
        }

        if (atMenu) {
            if (keys[KeyEvent.VK_ENTER]) newGame();
            if (keys[KeyEvent.VK_ESCAPE]) stop();
        }

        if (paused) {
            if (keys[KeyEvent.VK_ENTER]) unPause();
            if (keys[KeyEvent.VK_ESCAPE]) stop();
        }

        if (gameOver) {
            if (keys[KeyEvent.VK_ENTER]) newGame();
            if (keys[KeyEvent.VK_ESCAPE]) stop();
        }
    }

    public static void gameOver() { 
        gameOver = true;
    }
    private static void pause() { paused = true; }
    private static void unPause() { paused = false; }


    private void render() {
        Graphics2D g = (Graphics2D) display.getGraphics();
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g.setColor(Color.GRAY);
        g.fillRect(SIDE_BORDER, TOP_BORDER, SCREEN_WIDTH - SIDE_BORDER * 2, SCREEN_HEIGHT - TOP_BORDER - BOTTOM_BORDER);

        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
                    g.fillRect(SIDE_BORDER + x * TILE_SIZE, TOP_BORDER + y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        g.setColor(Color.GREEN);
        for (SnakeCell snakeCell : snakeCells) {
            g.fillRect(SIDE_BORDER + snakeCell.getX() * TILE_SIZE, TOP_BORDER + snakeCell.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        if (!atMenu) {
            g.setColor(Color.ORANGE);
            g.fillRect(SIDE_BORDER + food.getX() * TILE_SIZE, TOP_BORDER + food.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("Score: " + score, 50, 65);
            g.drawString("Speed: " + speed, SCREEN_WIDTH - 250, 65);
        }

        if (paused) {
            g.setColor(Color.BLACK);
            g.fillRect((SCREEN_WIDTH - MENU_WIDTH) / 2, (SCREEN_HEIGHT - MENU_HEIGHT) / 2, MENU_WIDTH, MENU_HEIGHT);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            xCenterText(g, "PAUSED", -50);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            xCenterText(g, "Press ESCAPE to quit", 40);
            xCenterText(g, "Press ENTER to resume", 85);
        }

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.fillRect((SCREEN_WIDTH - MENU_WIDTH) / 2, (SCREEN_HEIGHT - MENU_HEIGHT) / 2, MENU_WIDTH, MENU_HEIGHT);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            xCenterText(g, "Game over!", -100);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            xCenterText(g, "You score " + score + " points", -60);
            xCenterText(g, "Press ESCAPE to quit", 40);
            xCenterText(g, "Press ENTER to play again", 85);
        }

        if (atMenu) {
            g.setColor(Color.BLACK);
            g.fillRect((SCREEN_WIDTH - MENU_WIDTH) / 2, (SCREEN_HEIGHT - MENU_HEIGHT) / 2, MENU_WIDTH, MENU_HEIGHT);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            xCenterText(g, "Snake by Nate", -50);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            xCenterText(g, "Press ENTER to play", 40);
            xCenterText(g, "Press ESCAPE to quit", 85);
        }

        getGraphics().drawImage(display, 0, 0, this);
    }

    private void xCenterText(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;

        g.drawString(text, x, getHeight() / 2 + y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static int getMapWidth() { return MAP_WIDTH; }
    public static int getMapHeight() { return MAP_HEIGHT; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeGame snakeGame = new SnakeGame();
            snakeGame.setMinimumSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
            snakeGame.setMaximumSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
            snakeGame.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

            frame = new JFrame(TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(snakeGame);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setIgnoreRepaint(true);
            snakeGame.start();
        });
    }
}
