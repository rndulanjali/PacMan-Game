/*This class will handle the decision logic for ghost movement. 
 It decide which direction a ghost moves
 It takes a GameState and an index, and updates the ghost’s direction.*/

package pacman.game;

public class GhostAI {
    private final GameState state; //This class stores the game data object.(Pacman & ghost postion, maze layout,score,speed_ GhostAI need this
                                   //to decide where ghost can move
    public GhostAI(GameState state) {
        this.state = state;        //GhostAI now has access to all game data
    }

    public void moveGhost(int i) {  //(int i) --> Which ghost to move. moveGhost(2) means move 3rd ghost
        int[] ghostX = state.getGhostX(); //gets the X position of all ghost
        int[] ghostY = state.getGhostY(); //gets the Y position of all ghost
        int[] ghostDx = state.getGhostDx();//gets the X movement direction for ghost [-1,0,1]
        int[] ghostDy = state.getGhostDy();//gets the Y movement direction
        int[] ghostSpeed = state.getGhostSpeed();//[2,3,2,4]
        short[] screenData = state.getScreenData();//gets maze data. store walls,dots and paths

        /*Check grid alignment
          Change direction only when aligned to grid*/
        if (ghostX[i] % GameState.BLOCK_SIZE == 0 && ghostY[i] % GameState.BLOCK_SIZE == 0) { //check ghost is on a maze grid cell
            int pos = ghostX[i] / GameState.BLOCK_SIZE + GameState.N_BLOCKS * (ghostY[i] / GameState.BLOCK_SIZE);//find ghost position on maze
            int[] dx = new int[4];//array store all possible directions
            int[] dy = new int[4];
            int count = 0;        //how many directions are avilable

            // Check available directions
            
            //check left movements
            if ((screenData[pos] & 1) == 0 && ghostDx[i] != 1) {      //(screenData[pos] & 1) == 0--> Is there No wall on the left
                dx[count] = -1; dy[count] = 0; count++;               //ghostDx[i] != 1  --> Prevent ghost from immediately reversing direction
            }
          //check up movements
            if ((screenData[pos] & 2) == 0 && ghostDy[i] != 1) {
                dx[count] = 0; dy[count] = -1; count++;               //Store up direction. move up
            }
          //check right movements
            if ((screenData[pos] & 4) == 0 && ghostDx[i] != -1) {
                dx[count] = 1; dy[count] = 0; count++;
            }
          //check down movements
            if ((screenData[pos] & 8) == 0 && ghostDy[i] != -1) {
                dx[count] = 0; dy[count] = 1; count++;
            }
          //dead end check
            if (count == 0) {
                // Dead end – reverse direction
                if ((screenData[pos] & 15) == 15) { //if all are fully blocked
                    ghostDx[i] = 0;                 //ghost stop moving
                    ghostDy[i] = 0;
                } else {
                    ghostDx[i] = -ghostDx[i];       //otherwise reverse. Ghost turn back
                    ghostDy[i] = -ghostDy[i];
                }
            } else {                                //if there are multiple possible paths then choose randomly
                // Random choice among available directions
                int r = (int) (Math.random() * count);
                if (r > 3) r = 3;   //safety check to avoid array errors
                ghostDx[i] = dx[r];
                ghostDy[i] = dy[r]; //set choosen direction 
            }
        }
        //move ghost
        int newX = ghostX[i] + ghostDx[i] * ghostSpeed[i];  //calculate new X position
        int newY = ghostY[i] + ghostDy[i] * ghostSpeed[i];  //calculate new Y position
        state.moveGhost(i, newX, newY);                     //update ghost position in gameState
    }
}