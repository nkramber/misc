import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class RaycastingGame extends JFrame implements KeyListener {
    private static final int SCREEN_WIDTH = 640;
    private static final int SCREEN_HEIGHT = 480;
    private static final int MAP_SIZE = 10;
    
    private BufferedImage display;
    private double playerX, playerY;     // Player position
    private double dirX, dirY;           // Player direction vector
    private double planeX, planeY;       // Camera plane vector
    private boolean[] keys;              // Track pressed keys
    
    // Game map (1 = wall, 0 = empty space)
    private static final int[][] MAP = {
        {1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,1,0,0,1},
        {1,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,1,0,1,0,0,1},
        {1,0,0,1,0,0,1,0,0,1},
        {1,0,0,1,0,0,0,0,0,1},
        {1,0,0,1,0,0,1,0,0,1},
        {1,0,0,0,0,0,1,0,0,1},
        {1,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1}
    };
    
    public RaycastingGame() {
        setTitle("Raycasting Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setResizable(false);
        
        // Initialize game state
        display = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        keys = new boolean[256];
        
        // Set initial player position and direction
        playerX = 2;
        playerY = 2;
        dirX = 1;
        dirY = 0;
        planeX = 0;
        planeY = 0.66; // Roughly tan(FOV/2)
        
        addKeyListener(this);
        setLocationRelativeTo(null);
    }
    
    public double[] castRay(int screenX) {
        // Calculate ray position and direction
        double cameraX = 2 * screenX / (double)SCREEN_WIDTH - 1;
        double rayDirX = dirX + planeX * cameraX;
        double rayDirY = dirY + planeY * cameraX;
        
        // Initialize DDA algorithm variables
        int mapX = (int)playerX;
        int mapY = (int)playerY;
        
        // Calculate ray step and initial sideDist
        double deltaDistX = Math.abs(1 / rayDirX);
        double deltaDistY = Math.abs(1 / rayDirY);
        double sideDistX, sideDistY;
        int stepX, stepY;
        
        // Calculate step direction and initial sideDist
        if (rayDirX < 0) {
            stepX = -1;
            sideDistX = (playerX - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1.0 - playerX) * deltaDistX;
        }
        if (rayDirY < 0) {
            stepY = -1;
            sideDistY = (playerY - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1.0 - playerY) * deltaDistY;
        }
        
        // Perform DDA
        boolean hit = false;
        boolean side = false; // NS or EW wall hit
        while (!hit) {
            // Jump to next square
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = false;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = true;
            }
            
            // Check if ray hit a wall
            if (mapX >= 0 && mapX < MAP_SIZE && mapY >= 0 && mapY < MAP_SIZE) {
                if (MAP[mapX][mapY] > 0) hit = true;
            }
        }
        
        // Calculate distance to wall
        double perpWallDist;
        if (!side) {
            perpWallDist = (mapX - playerX + (1 - stepX) / 2) / rayDirX;
        } else {
            perpWallDist = (mapY - playerY + (1 - stepY) / 2) / rayDirY;
        }
        
        return new double[]{perpWallDist, side ? 1.0 : 0.0};
    }
    
    public void movePlayer(double angle, double distance) {
        double newX = playerX + Math.cos(angle) * distance;
        double newY = playerY + Math.sin(angle) * distance;
        
        // Basic collision detection
        if (newX >= 0 && newX < MAP_SIZE && MAP[(int)newX][(int)playerY] == 0) {
            playerX = newX;
        }
        if (newY >= 0 && newY < MAP_SIZE && MAP[(int)playerX][(int)newY] == 0) {
            playerY = newY;
        }
    }
    
    public void rotatePlayer(double angle) {
        // Rotate direction vector
        double oldDirX = dirX;
        dirX = dirX * Math.cos(angle) - dirY * Math.sin(angle);
        dirY = oldDirX * Math.sin(angle) + dirY * Math.cos(angle);
        
        // Rotate camera plane
        double oldPlaneX = planeX;
        planeX = planeX * Math.cos(angle) - planeY * Math.sin(angle);
        planeY = oldPlaneX * Math.sin(angle) + planeY * Math.cos(angle);
    }
    
    private void gameLoop() {
        while (true) {
            handleInput();
            render();
            try {
                Thread.sleep(16); // Approximately 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void handleInput() {
        double moveSpeed = 0.05;
        double rotateSpeed = 0.03;
        
        if (keys[KeyEvent.VK_W]) {
            double angle = Math.atan2(dirY, dirX);
            movePlayer(angle, moveSpeed);
        }
        if (keys[KeyEvent.VK_S]) {
            double angle = Math.atan2(dirY, dirX) + Math.PI;
            movePlayer(angle, moveSpeed);
        }
        if (keys[KeyEvent.VK_A]) {
            double angle = Math.atan2(dirY, dirX) - Math.PI/2;
            movePlayer(angle, moveSpeed);
        }
        if (keys[KeyEvent.VK_D]) {
            double angle = Math.atan2(dirY, dirX) + Math.PI/2;
            movePlayer(angle, moveSpeed);
        }
        if (keys[KeyEvent.VK_LEFT]) {
            rotatePlayer(-rotateSpeed);
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            rotatePlayer(rotateSpeed);
        }
    }
    
    private void render() {
        // Clear screen
        Graphics2D g = (Graphics2D) display.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        
        // Draw ceiling and floor
        g.setColor(new Color(50, 50, 100));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT/2);
        g.setColor(new Color(100, 100, 100));
        g.fillRect(0, SCREEN_HEIGHT/2, SCREEN_WIDTH, SCREEN_HEIGHT/2);
        
        // Cast rays and draw walls
        for (int x = 0; x < SCREEN_WIDTH; x++) {
            double[] rayResult = castRay(x);
            double distance = rayResult[0];
            boolean side = rayResult[1] == 1.0;
            
            // Calculate wall height
            int lineHeight = (int)(SCREEN_HEIGHT / distance);
            int drawStart = -lineHeight / 2 + SCREEN_HEIGHT / 2;
            if (drawStart < 0) drawStart = 0;
            int drawEnd = lineHeight / 2 + SCREEN_HEIGHT / 2;
            if (drawEnd >= SCREEN_HEIGHT) drawEnd = SCREEN_HEIGHT - 1;
            
            // Choose wall color based on side (darker for NS walls)
            Color wallColor = side ? new Color(150, 150, 150) : new Color(200, 200, 200);
            g.setColor(wallColor);
            g.drawLine(x, drawStart, x, drawEnd);
        }
        
        // Update screen
        getGraphics().drawImage(display, 0, 0, this);
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RaycastingGame game = new RaycastingGame();
            game.setVisible(true);
            new Thread(() -> game.gameLoop()).start();
        });
    }
}