/*The InputHandler class handles keyboard input for the Pacman game.
It listens to key events such as arrow keys for movement and the S key to start the game.
It updates the requested direction in the GameState, which is later used by the game controller to move Pacman.*/

package pacman.game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter { //This class listen to keyboard events.
    private final GameState state;//gives access to gamedata
    private final GameController controller;

    //Constructor. It receives 2 objects
    public InputHandler(GameState state, GameController controller) {
        this.state = state;
        this.controller = controller;
    }

    //This method runs whenever a key is pressed
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (state.isInGame()) {                 //If ganme is running and,
            if (key == KeyEvent.VK_LEFT) {      //If left key is pressed,
                state.setReqDx(-1);             //Pacman want to move left
                state.setReqDy(0);
            } else if (key == KeyEvent.VK_RIGHT) {
                state.setReqDx(1);
                state.setReqDy(0);
            } else if (key == KeyEvent.VK_UP) {
                state.setReqDx(0);
                state.setReqDy(-1);
            } else if (key == KeyEvent.VK_DOWN) {
                state.setReqDx(0);
                state.setReqDy(1);
            } else if (key == KeyEvent.VK_ESCAPE) {
                state.setInGame(false);          //Game stop
            }
        } else {                                 //If game is in intro screen
            if (key == KeyEvent.VK_S || key == KeyEvent.VK_S) { 
                state.setInGame(true);           //start game
                state.initGame();                //reset game data
                controller.restartGameTimers(); // restart countdown
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {     //runs when key is released
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT ||
            key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
            state.setReqDx(0);
            state.setReqDy(0);               //stop movement requests
        }
    }
}