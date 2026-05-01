/*Borad class represents the main game pannel. It extends Jpannel and responsible for rendering the game graphics.
  It connect the gameState, gameController and renderer class, handle key bloard inputs and overrides paint component to
  draw maze, pacman, ghost and score on the screen. It has several UI methods 
  showPuzzle(), showGameOverJoke(), saveScore(), drawScore(), intro screen*/

package pacman.game;

import pacman.api.HeartGameClient;
import pacman.api.JokeClient;
import pacman.game.*;
import pacman.ui.PuzzleDialog;
import pacman.ui.JokeDialog;
import pacman.auth.SessionManager;
import pacman.auth.UserRepository;
import pacman.ui.LoginDialog;

import javax.swing.*;     //GUI components
import java.awt.*;        //Graphic drawing
import java.awt.event.*;  //Keyboard events

public class Board extends JPanel {
    private final GameState state;
    private final Renderer renderer;
    private final GameController controller;
    private final InputHandler inputHandler;
    private InactivityTimer inactivityTimer;
    private JFrame parentFrame;

    //Constructor of the board class
    public Board(JFrame frame) {
        this.parentFrame = frame;  //store the main window inside a variable
        state = new GameState();
        renderer = new Renderer(); //Rendere is responsible for drawing the game graphics.
        controller = new GameController(state);  //create game logic controller
        inputHandler = new InputHandler(state, controller);//this object reads keyboard inputs. InputHndler updates GameState requested direction

        //Repaint callback
        controller.setRepaintCallback(() -> repaint()); //Whenver gamecontroller update the game screen must RE-DRAW

        /*Inactivity timer (30 minutes = 1800 seconds)
          After 30 minutes of no user activity, auto-logout and restart.*/
        inactivityTimer = new InactivityTimer(1800, () -> {
            SwingUtilities.invokeLater(() -> {   //Swing requires UI updates to run in the Event Dispatch Thread
                SessionManager.logout();         //logout
                parentFrame.dispose();           //close the current game window
                showLoginAndRestart();           //open log in screen again
            });
        });

        // Thse resets the inactivity timer
        addKeyListener(new KeyAdapter() {          //detect any keyboard press
            @Override
            public void keyPressed(KeyEvent e) {
                inactivityTimer.reset();
            }
        });
        addMouseListener(new MouseAdapter() {     //detect any mouse click
            @Override
            public void mousePressed(MouseEvent e) {
                inactivityTimer.reset();
            }
        });

        // --- Periodic puzzle callback ---
        // Triggered by GameController every 15 seconds via countdown.
        controller.setPeriodicCallback(() -> {
            inactivityTimer.reset(); // timer activity before puzzle
            SwingUtilities.invokeLater(() -> {
                try {
                    HeartGameClient client = new HeartGameClient();
                    HeartGameClient.Puzzle puzzle = client.fetchPuzzle();  //The game call SEVER API to get puzzle
                    PuzzleDialog dialog = new PuzzleDialog(parentFrame, puzzle);//Open puzzle window
                    dialog.addWindowListener(new WindowAdapter() {              //Detect when puzzle window closed
                        @Override
                        public void windowClosed(WindowEvent e) {
                            inactivityTimer.reset();                            //Reset timer after puzzle
                        }
                    });
                    dialog.setVisible(true);                                    //Display the puzzle window
                    controller.continueAfterPeriodicPuzzle(dialog.isSolved());  //Tell controller to result
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Puzzle error. Continuing without bonus.",
                            "Puzzle Error",
                            JOptionPane.WARNING_MESSAGE);
                    controller.continueAfterPeriodicPuzzle(false);
                }
            });
        });

        // ---Game over joke callback ---
        // Triggered when the player loses all lives.
        controller.setJokeCallback(() -> {
            inactivityTimer.reset(); // activity before joke
            SwingUtilities.invokeLater(() -> {
                try {
                    JokeClient client = new JokeClient();
                    JokeClient.Joke joke = client.fetchJoke();  //Get random joke from API
                    JokeDialog dialog = new JokeDialog(parentFrame, joke.getJoke(), true); //Display joke to player
                    dialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            inactivityTimer.reset(); // after joke
                        }
                    });
                    dialog.setVisible(true);
                    controller.continueAfterJoke();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Failed to fetch joke. Game over.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    controller.continueAfterJoke();
                }
            });
        });

        // --- Game over listener for high score update ---
        controller.setGameOverListener(finalScore -> {
            String username = SessionManager.getCurrentUser();  //get current loged in user
            if (username != null) {
                UserRepository repo = new UserRepository();
                repo.updateHighScore(username, finalScore);    //update databse high score
            }
        });
        
        //Pannel setuo. Allow keyboard inputs.
        setFocusable(true);
        setBackground(Color.black);
        addKeyListener(inputHandler); //Connent keyboard movements
        // Second listener for pause (VK_P) – separated to keep InputHandler focused on movement.
        addKeyListener(new KeyAdapter() {//Seperate key listner for paused
            @Override
            public void keyPressed(KeyEvent e) {
                controller.handleKey(e);   //This check P Key -> Pause game
            }
        });

        //Create Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(255, 200, 0));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        logoutButton.addActionListener(e -> {  //Click action. When player click,
            inactivityTimer.stop();            //stop timer
            SessionManager.logout();           //log out
            parentFrame.dispose();             //close window
            showLoginAndRestart();             //show log in again
        });

        setLayout(null); //use absolute positioning
        logoutButton.setBounds(10, 10, 80, 25);//button positioning. Top-left corner
        add(logoutButton); //add button to pannel
    }

    /*Disposes the current game window and shows the login dialog again.
     */
    private void showLoginAndRestart() {
        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog(null);   //open log in window
            login.setVisible(true);
            if (login.isSucceeded()) {                   //if log in suceed,
                Pacman ex = new Pacman();                //create new game window
                ex.setVisible(true);
                Pacman.playMusic("pacmanMusic.wav");    //start the backgroun music
            } else {
                System.exit(0);                         //else close the appication
            }
        });
    }

    //This methood draw game every frame
    @Override
    public void paintComponent(Graphics g) {  
        super.paintComponent(g);              //erase previos frame. clear thr screen
        Graphics2D g2d = (Graphics2D) g;      //current graphics. allow advanced drawing

        g2d.setColor(Color.black);           //fill background
        g2d.fillRect(0, 0, getWidth(), getHeight());

        renderer.draw(g2d, state, controller.getPacmanAnimPos()); //drwa the game. Renderer draw pacma, ghost, mazedots, score

        Toolkit.getDefaultToolkit().sync();  //privent scrren flickering
        g2d.dispose();                       //release memory
    }
}