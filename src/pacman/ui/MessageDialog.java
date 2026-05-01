/* Reusable dialog to show messages like:Login failed,Welcome message, Errors, Warnings */

package pacman.ui;

import javax.swing.*;

import java.awt.*;

public class MessageDialog extends JDialog {
	//Constructor 1:Used when parent is another dialog.
    public MessageDialog(JDialog parent, String title, String message, int messageType) {
        super(parent, title, true);
        setupUI(parent, title, message, messageType);
    }
    //Constructor 2:Used when parent is main window.
    public MessageDialog(JFrame parent, String title, String message, int messageType) {
        super(parent, title, true);
        setupUI(parent, title, message, messageType);
    }

    //Builds the dialog window.
    private void setupUI(Window parent, String title, String message, int messageType) {
        Color bgColor = new Color(30, 30, 30);
        Color textColor = Color.WHITE;
        Color accentColor = new Color(255, 200, 0); // gold
        Color errorColor = new Color(255, 80, 80);  // red for errors
 
      //Panel
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Icon based on message type
        String icon = "⚠️"; // default warning
        if (messageType == JOptionPane.ERROR_MESSAGE) {
            icon = "❌";
        } else if (messageType == JOptionPane.INFORMATION_MESSAGE) {
            icon = "ℹ️";
        } else if (messageType == JOptionPane.QUESTION_MESSAGE) {
            icon = "❓";
        }

        // Title label
        JLabel titleLabel = new JLabel(icon + " " + title + " " + icon, SwingConstants.CENTER);
        titleLabel.setForeground(messageType == JOptionPane.ERROR_MESSAGE ? errorColor : accentColor);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Message area
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageArea.setForeground(textColor);
        messageArea.setBackground(bgColor);
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(messageArea, BorderLayout.CENTER);

        // OK button
        JButton okButton = new JButton("OK");
        okButton.setBackground(accentColor);
        okButton.setForeground(Color.BLACK);
        okButton.setFocusPainted(false);
        okButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        okButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(bgColor);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        setSize(500, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}