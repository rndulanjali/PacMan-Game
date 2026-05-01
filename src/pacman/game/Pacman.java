/*The Pacman class is the main entry point of the application. It extends JFrame and creates the main game window.
It also handles startup tasks such as auto-login using a remember*/

package pacman.game;

import pacman.auth.SessionManager;
import pacman.auth.User;
import pacman.auth.UserRepository;
import pacman.ui.LoginDialog;
import pacman.ui.MessageDialog;

import javax.swing.*;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Pacman extends JFrame { //Pacman class itself is a game window. JFrame=JavaWindow
    //Constructor
	public Pacman() {
        initUI(); //when pacman object is created initialize game UI
    }

    private void initUI() {   //create game window interface
        add(new Board(this)); // pass current frame to Board
        setTitle("Pacman");   //set window title
        setDefaultCloseOperation(EXIT_ON_CLOSE);//if user close window -> program exists
        setSize(380, 420);//set game window size 
        setLocationRelativeTo(null);//window appears at center of screen
    }
/** Main method. This is where program starts **/
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            // --- Autologin via remember token ---
/** --This code implements the “Remember Me” auto-login feature. It reads a saved token from the .pacman_token file. 
 If the token exists and matches a user in the database, the system automatically starts the user session, displays 
 a welcome message with their high score, opens the Pacman game window, and skips the login dialog.-- **/
            String token = null;
            try (Scanner scanner = new Scanner(new File(".pacman_token"))) { //read token file. if user select remember me token save in here
                if (scanner.hasNext()) {
                    token = scanner.nextLine();
                }
            } catch (FileNotFoundException e) {
                // ignore – no saved token
            }

            if (token != null) {
                UserRepository repo = new UserRepository();
                User user = repo.findByRememberToken(token);
                if (user != null) {
                    SessionManager.startSession(user.getUsername());
                    MessageDialog welcome = new MessageDialog((JFrame) null, "Welcome",
                        "Welcome back, " + user.getUsername() + "!\nYour current high score: " + user.getHighScore(),
                        JOptionPane.INFORMATION_MESSAGE);
                    welcome.setVisible(true);
                    Pacman ex = new Pacman();
                    ex.setVisible(true);
                    playMusic("pacmanMusic.wav");
                    return; // skip login dialog
                }
            }

            // No token or invalid → show login
            LoginDialog login = new LoginDialog(null);//create new log in window
            login.setVisible(true);//this shows log in window on screen
            if (login.isSucceeded()) { //if log in success
                String username = SessionManager.getCurrentUser(); /**SessionManager -> stores who is loged into system**/
                UserRepository repo = new UserRepository();        /**UserRepository -> responsible for find user,read user info,update score**/
                User user = repo.find(username);   //get user using find(nimesha)
                int highScore = (user != null) ? user.getHighScore() : 0;

                MessageDialog welcome = new MessageDialog((JFrame) null, "Welcome",    //this sets the dialog time. Shows an info msg icon
                    "Welcome, " + username + "!\nYour current high score: " + highScore,
                    JOptionPane.INFORMATION_MESSAGE);
                welcome.setVisible(true);

                Pacman ex = new Pacman();   //starts the game. this create main game window
                ex.setVisible(true);
                playMusic("pacmanMusic.wav");
            } else {
                System.exit(0);            //if login failed or cancelled the program close comletely
            }
        });
    }

    public static void playMusic(String musicLocation) {
        try {
            File musicPath = new File(musicLocation);  //load the file
            if (musicPath.exists()) {
                javax.sound.sampled.AudioInputStream audioInput =
                    javax.sound.sampled.AudioSystem.getAudioInputStream(musicPath);        //this reads audio file
                javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip(); //create audio player
                clip.open(audioInput);                                                     //load sound into player
                clip.start();                                                              //start playing music
                clip.loop(javax.sound.sampled.Clip.LOOP_CONTINUOUSLY);                     //loop music forever untile game close
            } else {
                System.out.println("Cannot find the Audio File");
            }
        } catch (Exception ex) {
            ex.printStackTrace();//if any error happen program print error details
        }
    }
}