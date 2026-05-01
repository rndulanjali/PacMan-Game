/*This 'GameController'class manage the  main game logic  including movements, collision detection, timer and level progression. 
  This is the brain of game. It controls
  pacman & ghost movements
  collision
  timer
  puzzle
  gameover*/

package pacman.game;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class GameController implements ActionListener {
    // --- Callback interfaces ---
	//GameController uses callback  inteface to communicate with UI layer
    public interface PeriodicPuzzleCallback {
        void showPuzzlePeriodic();
    }

    public interface GameOverJokeCallback {
        void showJokeOnGameOver();
    }

    public interface GameOverListener { //Tis inteface sends the final score when the game ends.
        void onGameOver(int finalScore);
    }

    private final GameState state;
    private final GhostAI ghostAI;
    private final Timer timer;                 //Mian game loop timer. Game updates every 40 ms
    private Timer countdownTimer;               //This timer countdown seconds before puzzle apperas
    private static final int COUNTDOWN_START = 15;
    //animation variable
    private int pacAnimCount = 2;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    private boolean paused = false;
    private Runnable repaintCallback;
    private PeriodicPuzzleCallback periodicCallback;
    private GameOverJokeCallback jokeCallback;
    private GameOverListener gameOverListener;
    private boolean puzzlePending = false;      //also used for joke dialog

    //constructor
    public GameController(GameState state) {
        this.state = state;
        this.ghostAI = new GhostAI(state);
        this.timer = new Timer(40, this);
        timer.start();
        startCountdown();
    }

    //	Repaint callback
    public void setRepaintCallback(Runnable callback) {
        this.repaintCallback = callback;
    }

    public void setPeriodicCallback(PeriodicPuzzleCallback callback) {
        this.periodicCallback = callback;
    }

    public void setJokeCallback(GameOverJokeCallback callback) {
        this.jokeCallback = callback;
    }

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    //Countdown timer management
    private void startCountdown() {
        if (countdownTimer == null) {
            countdownTimer = new Timer(1000, e -> { //create timer.Timer runs every 1000ms
                if (!paused && state.isInGame() && !puzzlePending) {
                    int seconds = state.getSecondsLeft();
                    if (seconds > 0) {
                        state.setSecondsLeft(seconds - 1);
                    }
                    if (seconds == 1) { // after decrement it becomes 0 -> trigger puzzle
                        triggerPuzzle();
                    }
                }
                if (repaintCallback != null) repaintCallback.run();
            });
            countdownTimer.setRepeats(true);
        }
        if (!countdownTimer.isRunning() && state.isInGame() && !paused && !puzzlePending) {
            countdownTimer.start();
        }
    }

    private void stopCountdown() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }

    private void triggerPuzzle() {
        if (state.isInGame() && !puzzlePending) {
            puzzlePending = true;
            stopCountdown();      // stop countdown while puzzle shows
            timer.stop();         // stop game timer
            if (periodicCallback != null) {
                periodicCallback.showPuzzlePeriodic();
            }
        }
    }

    // Called after periodic puzzle is done
    public void continueAfterPeriodicPuzzle(boolean solved) {
        if (solved) {
            state.addScore(50); // bonus points
        }
        puzzlePending = false;
        state.setSecondsLeft(COUNTDOWN_START);  // reset countdown
        timer.start();
        startCountdown();
        if (repaintCallback != null) repaintCallback.run();
    }

    // Called after game over joke is shown
    public void continueAfterJoke() {
        puzzlePending = false;
        state.setInGame(false);  // go to intro screen
        if (repaintCallback != null) repaintCallback.run();
        // Do NOT restart timers – game is over until player presses S
    }

    // Called when game restarts via 'S'
    public void restartGameTimers() {
        if (state.isInGame()) {
            state.setSecondsLeft(COUNTDOWN_START);
            timer.start();
            startCountdown();
        }
    }

    /*Main Game loop. This method runs in every 40ms. Game loop steps 1.animate pacman, 2.move ghosts, 3.check collision 4.repaint screen*/
    @Override
    public void actionPerformed(ActionEvent e) {
        if (paused) return;//game stop if paused

        doAnimation(); //move  pacman mouth
        if (state.isInGame()) {
            if (state.isDying()) {
                death();
            } else {
                movePacman();
                moveAllGhosts();
                checkMaze();
            }
        }
        if (repaintCallback != null) repaintCallback.run();
    }

    //control pacman mouth opening
    private void doAnimation() {
        pacAnimCount--;
        if (pacAnimCount <= 0) {
            pacAnimCount = 2;
            pacmanAnimPos += pacAnimDir;
            if (pacmanAnimPos == 3 || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
    }

    //This method controls how pacman moves inside the Maze, move pacman, check walls, control dots
    private void movePacman() {
        int pos;
        short[] screenData = state.getScreenData(); //Maze data ARRAY
        int blockSize = GameState.BLOCK_SIZE;       //size of 1 maze square
        int nBlocks = GameState.N_BLOCKS;           //Total maze blocks in 1 row
        //These are requested directions from the keyboard
        int reqDx = state.getReqDx();
        int reqDy = state.getReqDy();
        //Current pacman direction.
        int pacmanDx = state.getPacmanDx();
        int pacmanDy = state.getPacmanDy();

        //Reverse direction. When player presssed the ooposite direction
        if (reqDx == -pacmanDx && reqDy == -pacmanDy) {
            state.setPacmanDx(reqDx);
            state.setPacmanDy(reqDy);
            state.setViewDx(reqDx);
            state.setViewDy(reqDy);
        }
        
      //Check if pacman is aligned with Grid
        if (state.getPacmanX() % blockSize == 0 && state.getPacmanY() % blockSize == 0) {
            pos = state.getPacmanX() / blockSize + nBlocks * (state.getPacmanY() / blockSize);
            short ch = screenData[pos]; //get block informations

            if ((ch & 16) != 0) {      //eat dots
                screenData[pos] = (short) (ch & 15); //this remove dot
                state.addScore(1);                   //Then score increase
            }
            //check requested direction
            if (reqDx != 0 || reqDy != 0) {
                boolean canMove = !((reqDx == -1 && reqDy == 0 && (ch & 1) != 0)
                                 || (reqDx == 1 && reqDy == 0 && (ch & 4) != 0)
                                 || (reqDx == 0 && reqDy == -1 && (ch & 2) != 0)
                                 || (reqDx == 0 && reqDy == 1 && (ch & 8) != 0));
                if (canMove) {   //if movements allowed then update directions
                    state.setPacmanDx(reqDx);
                    state.setPacmanDy(reqDy);
                    state.setViewDx(reqDx);
                    state.setViewDy(reqDy);
                }
            }
            //check if pacman hit the wall
            boolean blocked = ((pacmanDx == -1 && pacmanDy == 0 && (ch & 1) != 0)
                            || (pacmanDx == 1 && pacmanDy == 0 && (ch & 4) != 0)
                            || (pacmanDx == 0 && pacmanDy == -1 && (ch & 2) != 0)
                            || (pacmanDx == 0 && pacmanDy == 1 && (ch & 8) != 0));
            if (blocked) {
                state.setPacmanDx(0);
                state.setPacmanDy(0);
            }
        }

        int newX = state.getPacmanX() + GameState.PACMAN_SPEED * state.getPacmanDx();
        int newY = state.getPacmanY() + GameState.PACMAN_SPEED * state.getPacmanDy();
        state.movePacman(newX, newY);
    }

    //Controls ghost movements and collisions
    private void moveAllGhosts() {
        for (int i = 0; i < state.getNGhosts(); i++) {
            ghostAI.moveGhost(i);
            if (Math.abs(state.getPacmanX() - state.getGhostX()[i]) < 12 &&   //if distance<12pixel then pacman touch ghost and die
                Math.abs(state.getPacmanY() - state.getGhostY()[i]) < 12) {
                state.setDying(true);
            }
        }
    }

    //Check  if levels finished
    private void checkMaze() {
        if (state.allDotsEaten()) {   ///Check dots. If pacman ate all dots then,
            state.addScore(50);
            if (state.getNGhosts() < GameState.MAX_GHOSTS) {        //more ghosts will appera in next level
                state.setNGhosts(state.getNGhosts() + 1);
            }
            if (state.getCurrentSpeed() < GameState.MAX_SPEED) {    //Increase speed. Game become harder
                state.setCurrentSpeed(state.getCurrentSpeed() + 1);
            }
            state.initLevel(); //load new level
        }
    }

    //Handle pacman death
    private void death() {                
        state.decreaseLives();                //pacman lose one life
        if (state.getPacsLeft() == 0) {       //if no life left
            if (gameOverListener != null) {   //Send final score
                gameOverListener.onGameOver(state.getScore());
            }
            // Show joke dialog
            if (jokeCallback != null && !puzzlePending) {
                puzzlePending = true;
                timer.stop();
                stopCountdown();
                jokeCallback.showJokeOnGameOver();
            } else {
                state.setInGame(false);
            }
        } else {
            state.continueLevel();
        }
    }

    //Pause handling
    public void handleKey(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {   //if player presse 'P' game paused
            setPaused(!isPaused());
        }
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if (paused) {
            timer.stop();
            stopCountdown();
        } else {
            timer.start();
            startCountdown();
        }
    }

    public boolean isPaused() {  //return pause state
        return paused;
    }

    public int getPacmanAnimPos() {  //Return current animation frame
        return pacmanAnimPos;
    }
}