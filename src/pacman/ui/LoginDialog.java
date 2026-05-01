/* This class creates the login window where the user can:
Enter username,Enter password,Select Remember Me,Click Login,Click Register,View Leaderboard.It also checks the login with the database.
Handles authentication and session start */

package pacman.ui;

import pacman.auth.AuthService;//Checks username & password
import pacman.auth.SessionManager;//Starts user session after login
import pacman.auth.User;//Represents a user
import pacman.auth.UserRepository;//Communicates with database

import javax.swing.*;
import java.awt.*;
import java.io.*;//Used for file operations.-> Saving remember token to file
import java.util.UUID;///Used to generate a random unique token

public class LoginDialog extends JDialog {
	//Variables
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberCheckBox;
    private JButton loginButton, registerButton, leaderboardButton;
    private boolean succeeded = false;
    private final JFrame owner;//This stores the main game window.

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);
        this.owner = parent;
        setupUI(parent);
    }

    //This method builds the whole login window.
    private void setupUI(JFrame parent) {
        Color bgColor = new Color(30, 30, 30);
        Color textColor = Color.WHITE;
        Color accentColor = new Color(255, 200, 0);

        //Main Panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;//positioning
        JLabel userLabel = new JLabel("Username:");//Creates label.
        userLabel.setForeground(textColor);
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(userLabel, gbc);//Adds label to panel.

        gbc.gridx = 1;//Moves to next column.
        usernameField = new JTextField(15);//Creates textbox
        usernameField.setBackground(new Color(50, 50, 50));
        usernameField.setForeground(textColor);
        usernameField.setCaretColor(textColor);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(textColor);
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setBackground(new Color(50, 50, 50));
        passwordField.setForeground(textColor);
        passwordField.setCaretColor(textColor);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(passwordField, gbc);

        // Remember Me
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        rememberCheckBox = new JCheckBox("Remember Me");
        rememberCheckBox.setForeground(textColor);
        rememberCheckBox.setBackground(bgColor);
        rememberCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(rememberCheckBox, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(bgColor);

        loginButton = new JButton("Login");
        styleButton(loginButton, accentColor);
        loginButton.addActionListener(e -> login());

        registerButton = new JButton("Register");
        styleButton(registerButton, accentColor);
        registerButton.addActionListener(e -> {
            RegisterDialog reg = new RegisterDialog(owner);
            reg.setVisible(true);
        });

        leaderboardButton = new JButton("Leaderboard");
        styleButton(leaderboardButton, accentColor);
        leaderboardButton.addActionListener(e -> {              //When clicked.
            LeaderboardDialog lb = new LeaderboardDialog(owner);//Creates leaderboard window.
            lb.setVisible(true);
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(leaderboardButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        setContentPane(panel);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void styleButton(JButton btn, Color accent) {
        btn.setBackground(accent);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
    }

    private void login() {
        String username = usernameField.getText().trim();  //Gets username from textbox.trim() removes spaces.
        String password = new String(passwordField.getPassword());

        AuthService auth = new AuthService(new UserRepository());  //Creates authentication service.Uses UserRepository to access database
        if (auth.login(username, password)) {        //Check  if , username + password are correct
            SessionManager.startSession(username);   ///Start Session
            succeeded = true; 

            // Remember Me handling
            if (rememberCheckBox.isSelected()) {     //If user selected:Remember Me
                String token = UUID.randomUUID().toString();  //Creates random token.
                UserRepository repo = new UserRepository();
                repo.updateRememberToken(username, token);   //Save Token to Database
                try (PrintWriter out = new PrintWriter(".pacman_token")) { //Save Token to File
                    out.print(token);//Writes token to file.
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            dispose();
        } else {
            MessageDialog msg = new MessageDialog(this, "Login Failed",
                "Invalid username or password.", JOptionPane.ERROR_MESSAGE);
            msg.setVisible(true);
        }
    }

    public boolean isSucceeded() { return succeeded; }
}