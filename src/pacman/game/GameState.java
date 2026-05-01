/*This class will hold all the game data that was previously scattered in Board. 
 GameState is a data container that stores all the game information such as 
 Pacman position, ghost positions, maze structure, score, lives, and game status. 
 It separates the game data from the game logic to achieve better modularity, high cohesion, and low coupling.*/

package pacman.game;

public class GameState {
    // Constants
    public static final int BLOCK_SIZE = 24; //each maze cell size = 24pixel. Pacman move block by block
    public static final int N_BLOCKS = 15;   //maze grid size. 15 rows & 15 columns. Total block 15*15= 225                 
    public static final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; //So, screen 15 * 24 = 360 Pixel
    public static final int MAX_GHOSTS = 12;
    public static final int PACMAN_SPEED = 6;  //Pacman move 6 pixel per frmae. This control pacman movement's speed
    public static final int[] VALID_SPEEDS = {1, 2, 3, 4, 6, 8}; //Possible ghost speed. 1->slow, 4->medium, 8->Fast. Ghost speed increase as level increase
    public static final int MAX_SPEED = 6;  //maximum speed level allowed = 6

    // Level data
    private static final short[] LEVEL_DATA = {  //entire maze layout
        19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
        17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,         //It contains (15*15) 225 numbers. Each number encode 
        25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21,          //      Bit
        1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,                 //       1-> left wall
        1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,           //       2-> top wall
        1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,           //       4-> right wall
        1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,           //       8-> bottom wall
        1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21,          //       16->dot
        1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21,
        1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
        9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
    };

    // Dynamic state
    private short[] screenData; //current maze data
    private int pacman_x, pacman_y; //pacman pixel position 
    private int pacman_dx, pacman_dy;//pacman current movement direction
    private int req_dx, req_dy;      //direction the player want to move
    private int view_dx, view_dy;    //Control pacman image direction. viewdx=-1 & viewdy=0 means image facing left
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;
    private int nGhosts;
    private int currentSpeed;
    private int score;
    private int pacsLeft;
    private boolean inGame; //game running
    private boolean dying;  //pacman hit ghost
    private int secondsLeft = 15;   // countdown for puzzles

    //Constructor. Runs when object created in Board class
    public GameState() {
        // Allocate arrays with correct sizes
        screenData = new short[N_BLOCKS * N_BLOCKS];   //create screen data. Maze has 15*15 = 225 cells
        ghost_x = new int[MAX_GHOSTS];                 //create ghost array for 12 ghosts
        ghost_y = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        initGame();                                   //initialize the game
    }
 //This method to reset game
    public void initGame() {
        pacsLeft = 2; //set lives
        score = 0;    //reset score
        nGhosts = 6;
        currentSpeed = 3;
        secondsLeft = 15;
        initLevel();
    }

    //load maze
    public void initLevel() {
        System.arraycopy(LEVEL_DATA, 0, screenData, 0, screenData.length);// Copy level data into screenData.(Copy maze blue print)
        continueLevel();//strat the level
    }

    //start positioning
    public void continueLevel() {
        int dx = 1;
        for (int i = 0; i < nGhosts; i++) {
            ghost_y[i] = 4 * BLOCK_SIZE;  //ghost start locationing
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;              //ghost move horizontally
            ghost_dx[i] = dx;
            dx = -dx;
            int random = (int) (Math.random() * (currentSpeed + 1)); //random speed choosen
            if (random > currentSpeed) random = currentSpeed;
            ghostSpeed[i] = VALID_SPEEDS[random];
        }
        pacman_x = 7 * BLOCK_SIZE;     //pacman start positioning center of maze
        pacman_y = 11 * BLOCK_SIZE;
        pacman_dx = 0;   //no moving strat
        pacman_dy = 0;
        req_dx = 0;      //player direction direction
        req_dy = 0;
        view_dx = -1;    //pacman view direction. Pacman facing left now 
        view_dy = 0;
        dying = false;
    }

    // Getters and setters
    public int getSecondsLeft() { return secondsLeft; }
    public void setSecondsLeft(int seconds) { this.secondsLeft = seconds; }

    public short[] getScreenData() { return screenData; }
    public int getPacmanX() { return pacman_x; }  //return pacman x position
    public int getPacmanY() { return pacman_y; }
    public int getPacmanDx() { return pacman_dx; }
    public int getPacmanDy() { return pacman_dy; }
    public int getReqDx() { return req_dx; }
    public int getReqDy() { return req_dy; }
    public int getViewDx() { return view_dx; }
    public int getViewDy() { return view_dy; }
    public int[] getGhostX() { return ghost_x; }
    public int[] getGhostY() { return ghost_y; }
    public int[] getGhostDx() { return ghost_dx; }
    public int[] getGhostDy() { return ghost_dy; }
    public int[] getGhostSpeed() { return ghostSpeed; }
    public int getNGhosts() { return nGhosts; }
    public int getCurrentSpeed() { return currentSpeed; }
    public int getScore() { return score; }
    public int getPacsLeft() { return pacsLeft; }
    public boolean isInGame() { return inGame; }
    public boolean isDying() { return dying; }

    public void setReqDx(int dx) { req_dx = dx; }
    public void setReqDy(int dy) { req_dy = dy; }
    public void setPacmanDx(int dx) { pacman_dx = dx; }   //update pacman direction
    public void setPacmanDy(int dy) { pacman_dy = dy; }
    public void setViewDx(int dx) { view_dx = dx; }
    public void setViewDy(int dy) { view_dy = dy; }
    public void setInGame(boolean inGame) { this.inGame = inGame; }
    public void setDying(boolean dying) { this.dying = dying; }

    public void addScore(int points) { score += points; }  //add points
    public void decreaseLives() { pacsLeft--; }  //when pacman die decrease lives 
    public void setCurrentSpeed(int speed) { currentSpeed = speed; }
    public void setNGhosts(int n) { nGhosts = n; }

    public void movePacman(int newX, int newY) {   //update pacman postion
        pacman_x = newX;
        pacman_y = newY;
    }

    public void moveGhost(int index, int newX, int newY) {
        ghost_x[index] = newX;
        ghost_y[index] = newY;
    }

    public boolean eatDot(int pos) {            
        if ((screenData[pos] & 16) != 0) {                          //checkk if dot exists
            screenData[pos] = (short) (screenData[pos] & 15);       //if yes remove dot and
            return true;                                            //return true
        } 
        return false;
    }

    public boolean allDotsEaten() {                                //check if all dot eaten. check if maze is cleared?
        for (short value : screenData) {                           //loop through screen
            if ((value & 16) != 0) return false;                   //if found return false
        }
        return true;
    }
}