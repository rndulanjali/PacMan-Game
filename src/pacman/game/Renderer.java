/*The Renderer class is responsible for drawing all visual components of the game. 
 It loads the game images such as Pacman and ghosts, and uses the current GameState to render 
 the maze, dots, ghosts, Pacman, score, and timer onto the screen using the Graphics2D object.*/

package pacman.game;

import javax.swing.*;
import java.awt.*;

public class Renderer {
    private final Image ghost;
    private final Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private final Image pacman3up, pacman3left, pacman3right, pacman3down;
    private final Image pacman4up, pacman4left, pacman4right, pacman4down;
    private final Color dotColor = new Color(192, 192, 0);
    private final Color mazeColor = new Color(5, 100, 5); //maze wall color into dark green
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

    //Renterer constructor. Constructor runs when Board creates the Renderer.
    public Renderer() {
        // Load images. Convert it into ImageIcon, then extracact image
        ghost = new ImageIcon("ghost.png").getImage();
        pacman1 = new ImageIcon("pacman.png").getImage();
        pacman2up = new ImageIcon("up1.png").getImage();
        pacman3up = new ImageIcon("up2.png").getImage();
        pacman4up = new ImageIcon("up3.png").getImage();
        pacman2down = new ImageIcon("down1.png").getImage();
        pacman3down = new ImageIcon("down2.png").getImage();
        pacman4down = new ImageIcon("down3.png").getImage();
        pacman2left = new ImageIcon("left1.png").getImage();
        pacman3left = new ImageIcon("left2.png").getImage();
        pacman4left = new ImageIcon("left3.png").getImage();
        pacman2right = new ImageIcon("right1.png").getImage();
        pacman3right = new ImageIcon("right2.png").getImage();
        pacman4right = new ImageIcon("right3.png").getImage();
    }

    //Main drwaing method
    public void draw(Graphics2D g2d, GameState state, int pacAnimPos) {//So,renderer use gameState to know position of pacman & ghost,score,timer
        drawTimer(g2d, state);   //Draw the timer. It shows, Next:30s       
        drawMaze(g2d, state);    //Drwa maze 1.Maze wall 2.Dots
        drawScore(g2d, state);   //Drwa score
        if (state.isInGame()) {  //If game is running,
            drawGhosts(g2d, state);  //Draw all ghosts. Postion come from state.getGhostX() and state.getGhostY()
            drawPacman(g2d, state, pacAnimPos); //Drwa pacman with direction and animation frame
        } else {
            drawIntroScreen(g2d);    //else go to Intro scrren. 'Press S to start'
        }
    }

    //Timer drawing
    private void drawTimer(Graphics2D g2d, GameState state) {
        g2d.setFont(smallFont);      //set font
        g2d.setColor(Color.YELLOW);  //set color
        String timerText = "Next: " + state.getSecondsLeft() + "s"; //Create timer TEXT
        g2d.drawString(timerText, 10, 20);                          //Draw text on screen in Top-left corner
    }

    private void drawMaze(Graphics2D g2d, GameState state) {
        short[] screenData = state.getScreenData();
        int blockSize = GameState.BLOCK_SIZE;
        int screenSize = GameState.SCREEN_SIZE;

        g2d.setColor(mazeColor);
        g2d.setStroke(new BasicStroke(2));

        int i = 0;
        for (int y = 0; y < screenSize; y += blockSize) {
            for (int x = 0; x < screenSize; x += blockSize) {
                if ((screenData[i] & 1) != 0)
                    g2d.drawLine(x, y, x, y + blockSize - 1);
                if ((screenData[i] & 2) != 0)
                    g2d.drawLine(x, y, x + blockSize - 1, y);
                if ((screenData[i] & 4) != 0)
                    g2d.drawLine(x + blockSize - 1, y, x + blockSize - 1, y + blockSize - 1);
                if ((screenData[i] & 8) != 0)
                    g2d.drawLine(x, y + blockSize - 1, x + blockSize - 1, y + blockSize - 1);
                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                    g2d.setColor(mazeColor);
                }
                i++;
            }
        }
    }

    private void drawScore(Graphics2D g2d, GameState state) {
        g2d.setFont(smallFont);
        g2d.setColor(new Color(96, 128, 255));
        String s = "Score: " + state.getScore();
        g2d.drawString(s, GameState.SCREEN_SIZE / 2 + 96, GameState.SCREEN_SIZE + 16);

        for (int i = 0; i < state.getPacsLeft(); i++) {
            g2d.drawImage(pacman3left, i * 28 + 8, GameState.SCREEN_SIZE + 1, null);
        }
    }

    private void drawGhosts(Graphics2D g2d, GameState state) {
        int n = state.getNGhosts();
        int[] x = state.getGhostX();
        int[] y = state.getGhostY();
        for (int i = 0; i < n; i++) {
            g2d.drawImage(ghost, x[i] + 1, y[i] + 1, null);
        }
    }

    private void drawPacman(Graphics2D g2d, GameState state, int pacAnimPos) {
        int x = state.getPacmanX();
        int y = state.getPacmanY();
        int dx = state.getViewDx();
        int dy = state.getViewDy();

        if (dx == -1) drawPacmanLeft(g2d, x, y, pacAnimPos);
        else if (dx == 1) drawPacmanRight(g2d, x, y, pacAnimPos);
        else if (dy == -1) drawPacmanUp(g2d, x, y, pacAnimPos);
        else drawPacmanDown(g2d, x, y, pacAnimPos);
    }

    private void drawPacmanUp(Graphics2D g2d, int x, int y, int anim) {
        switch (anim) {
            case 1: g2d.drawImage(pacman2up, x + 1, y + 1, null); break;
            case 2: g2d.drawImage(pacman3up, x + 1, y + 1, null); break;
            case 3: g2d.drawImage(pacman4up, x + 1, y + 1, null); break;
            default: g2d.drawImage(pacman1, x + 1, y + 1, null);
        }
    }

    private void drawPacmanDown(Graphics2D g2d, int x, int y, int anim) {
        switch (anim) {
            case 1: g2d.drawImage(pacman2down, x + 1, y + 1, null); break;
            case 2: g2d.drawImage(pacman3down, x + 1, y + 1, null); break;
            case 3: g2d.drawImage(pacman4down, x + 1, y + 1, null); break;
            default: g2d.drawImage(pacman1, x + 1, y + 1, null);
        }
    }

    private void drawPacmanLeft(Graphics2D g2d, int x, int y, int anim) {
        switch (anim) {
            case 1: g2d.drawImage(pacman2left, x + 1, y + 1, null); break;
            case 2: g2d.drawImage(pacman3left, x + 1, y + 1, null); break;
            case 3: g2d.drawImage(pacman4left, x + 1, y + 1, null); break;
            default: g2d.drawImage(pacman1, x + 1, y + 1, null);
        }
    }

    private void drawPacmanRight(Graphics2D g2d, int x, int y, int anim) {
        switch (anim) {
            case 1: g2d.drawImage(pacman2right, x + 1, y + 1, null); break;
            case 2: g2d.drawImage(pacman3right, x + 1, y + 1, null); break;
            case 3: g2d.drawImage(pacman4right, x + 1, y + 1, null); break;
            default: g2d.drawImage(pacman1, x + 1, y + 1, null);
        }
    }

    private void drawIntroScreen(Graphics2D g2d) {
        int screenSize = GameState.SCREEN_SIZE;
        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, screenSize / 2 - 30, screenSize - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, screenSize / 2 - 30, screenSize - 100, 50);

        String s = "Press s to Start.";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = g2d.getFontMetrics(small);
        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (screenSize - metr.stringWidth(s)) / 2, screenSize / 2);
    }
}