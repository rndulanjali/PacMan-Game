/* This dialog allows the user to:Create an account, Enter username, Enter password, Confirm password */

package pacman.ui;

import pacman.auth.AuthService;
import pacman.auth.UserRepository;

import javax.swing.*;
import java.awt.*;


public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField, confirmField;

    public RegisterDialog(JFrame parent) {
        super(parent, "Register", true);
        setupUI(parent);
    }

    private void setupUI(JFrame parent) {
        Color bgColor = new Color(30, 30, 30);
        Color textColor = Color.WHITE;
        Color accentColor = new Color(255, 200, 0); // gold

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
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(textColor);
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
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

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel confirmLabel = new JLabel("Confirm:");
        confirmLabel.setForeground(textColor);
        confirmLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(confirmLabel, gbc);

        gbc.gridx = 1;
        confirmField = new JPasswordField(15);
        confirmField.setBackground(new Color(50, 50, 50));
        confirmField.setForeground(textColor);
        confirmField.setCaretColor(textColor);
        confirmField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(confirmField, gbc);

        // Password strength note
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel strengthNote = new JLabel("<html>Password must:<br>• Be at least 8 characters<br>• Contain uppercase, lowercase, digit, and special character</html>");
        strengthNote.setForeground(new Color(200, 200, 200));
        strengthNote.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(strengthNote, gbc);

        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(accentColor);
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        registerButton.addActionListener(e -> register());

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(registerButton, gbc);

        setContentPane(panel);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

  //This method creates the new user.
    private void register() {
        String username = usernameField.getText().trim();//Get username:
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {//Check empty fields
            MessageDialog msg = new MessageDialog(this, "Error", "All fields are required.", JOptionPane.ERROR_MESSAGE);
            msg.setVisible(true);
            return;
        }
        if (!password.equals(confirm)) {               //Check passwords match
            MessageDialog msg = new MessageDialog(this, "Error", "Passwords do not match.", JOptionPane.ERROR_MESSAGE);
            msg.setVisible(true);
            return;
        }
        if (!isPasswordStrong(password)) {             //Check password strength
            MessageDialog msg = new MessageDialog(this, "Weak Password",
                "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.",
                JOptionPane.ERROR_MESSAGE);
            msg.setVisible(true);
            return;
        }

        //Creates authentication service connected to database.
        AuthService auth = new AuthService(new UserRepository());
        if (auth.register(username, password)) {      //If registration successful:
            MessageDialog msg = new MessageDialog(this, "Success", "Registration successful! You can now log in.", JOptionPane.INFORMATION_MESSAGE);
            msg.setVisible(true);
            dispose();
        } else {
            MessageDialog msg = new MessageDialog(this, "Error", "Username already exists.", JOptionPane.ERROR_MESSAGE);
            msg.setVisible(true);
        }
    }

    //Password Strength Method
    private boolean isPasswordStrong(String password) {
        if (password.length() < 8) return false;//Check length:
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        //Loop through characters:
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}