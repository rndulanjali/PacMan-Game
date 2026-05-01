/* Dialog displayed when the game is over, showing a joke fetched from JokeAPI.
Demonstrates event-driven programming via button actions.*/

package pacman.ui;

import pacman.auth.SessionManager; //used when the Logout button is clicked to end the user session.
import pacman.game.Pacman;         //Used to restart the game after login again.

import javax.swing.*;              //Imports all Swing GUI components such as:Dialog,JLabe,JButton,JTextArea,JPanel
import javax.swing.border.EmptyBorder; //Used to create space around components inside panels.
import java.awt.*; //Imports AWT classes like:Color,Font,BorderLayout, FlowLayout. These control the appearance and layout of UI components.

public class JokeDialog extends JDialog {  //extends JDialog means:This class inherits from JDialog. So it behaves like a popup window dialog.
    private boolean closed = false;

    public JokeDialog(JFrame parent, String joke, boolean showLogout) {
        super(parent, "☺ Joke Time", true);
        setupUI(parent, joke, showLogout);
    }

    //This method creates the UI components.
    private void setupUI(JFrame parent, String joke, boolean showLogout) {
        Color bgColor = new Color(30, 30, 30);//dark gray background color.
        Color textColor = Color.WHITE;//Text color = white.
        Color titleColor = new Color(255, 80, 80);//Creates reddish color for the title.
        Color accentColor = new Color(255, 200, 0);//Creates yellow color used for borders and buttons.

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));//Creates a panel with a BorderLayout.
        mainPanel.setBackground(bgColor);  //Sets panel background to dark color.
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 3),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("😊 OH! You lost... but here's a joke for you! 😊", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(titleColor);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea jokeArea = new JTextArea(joke);//Creates a text area containing the joke text.
        jokeArea.setEditable(false);
        jokeArea.setLineWrap(true);
        jokeArea.setWrapStyleWord(true);
        jokeArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        jokeArea.setForeground(textColor);
        jokeArea.setBackground(bgColor);
        jokeArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(jokeArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(bgColor);

        JButton okButton = new JButton("OK, I feel better! 😊");
        okButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        okButton.setBackground(accentColor);
        okButton.setForeground(Color.BLACK);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createRaisedBevelBorder());
        /**Adds event listener.Runs when button is clicked.**/
        okButton.addActionListener(e -> {
            closed = true;
            dispose();
        });
        buttonPanel.add(okButton);

        if (showLogout) {
            JButton logoutButton = new JButton("Logout");
            logoutButton.setFont(new Font("SansSerif", Font.BOLD, 16));
            logoutButton.setBackground(accentColor);
            logoutButton.setForeground(Color.BLACK);
            logoutButton.setFocusPainted(false);
            logoutButton.addActionListener(e -> {
                SessionManager.logout();  //Ends user session.
                dispose();
                parent.dispose();
                // Restart login
                SwingUtilities.invokeLater(() -> {    //Runs code in Swing event thread.
                    LoginDialog login = new LoginDialog(null);  //Creates login dialog.
                    login.setVisible(true);                     //Shows login window.
                    if (login.isSucceeded()) {
                        Pacman ex = new Pacman();
                        ex.setVisible(true);
                        Pacman.playMusic("pacmanMusic.wav");
                    } else {
                        System.exit(0);
                    }
                });
            });
            buttonPanel.add(logoutButton);
        }

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

      //Final Window Settings
        setContentPane(mainPanel);//Sets the main panel as the dialog content.
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);//Closes dialog but does not stop the program.
    }

    public boolean isClosed() {  //Method to check if dialog closed.
        return closed;
    }
}