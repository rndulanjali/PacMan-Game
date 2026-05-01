/*The InactivityTimer class monitors user inactivity using a Swing Timer.
If the player does not interact with the game for a specified time, the timer triggers a callback function 
which logs out the user and restarts the login process.*/

package pacman.game;

import javax.swing.*;                 //to use Swing timer
import java.awt.event.ActionEvent;    //even triggered by timer
import java.awt.event.ActionListener; //detects when timer finishede


public class InactivityTimer {
    private Timer timer; //This is Swing timer. It counts time until inactivity timeout
    private final int timeoutSeconds; //How long to wait before logout
    private final Runnable onTimeout; //What action should happen when inactivity time is reached
    //Constructor. In Board class it creates object (new inactivityTimer(1800,logoutcode))
    public InactivityTimer(int timeoutSeconds, Runnable onTimeout) {
        this.timeoutSeconds = timeoutSeconds;//inactivity time
        this.onTimeout = onTimeout;          //what to do when timeout happen
        start();                             //start inactivity timer
    }

    //This method-> Restart timer when user does something
    public void reset() {
        if (timer != null) {
            timer.restart();
        }
    }
    //Create and start the timer
    public void start() {
        if (timer != null) {
            timer.stop();//because I don't want multiple timers to run
        }
        timer = new Timer(timeoutSeconds * 1000, new ActionListener() { //create timer
            @Override
            public void actionPerformed(ActionEvent e) { //this runs when timer finished when inactivity timeout happen
                if (onTimeout != null) {
                    onTimeout.run(); //runs the log out action
                }
            }
        });
        timer.setRepeats(false); //Timer runs only one time
        timer.start();           //Timer begins counting
    }

    public void stop() {
        if (timer != null) {
            timer.stop();      //Stop inactivity timer   
           
        }
    }
}