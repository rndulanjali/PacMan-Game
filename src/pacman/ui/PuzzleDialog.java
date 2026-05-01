/* This dialog window: Shows a heart puzzle image from the API, Asks the player how many hearts are in the image,Checks if the answer is correct 
This is triggered periodically during gameplay.*/

package pacman.ui;

import pacman.api.HeartGameClient.Puzzle;//This imports the Puzzle class from the API client
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;//Event handling
import java.awt.event.ActionListener;
import java.io.IOException;//Used for error handling when reading the image.
import java.net.URL;//Used to load the image from the internet.
import javax.imageio.ImageIO;//Used to read image files from a URL.

public class PuzzleDialog extends JDialog {
    private boolean solved = false;//This variable checks:Did the player solve the puzzle?

    public PuzzleDialog(JFrame parent, Puzzle puzzle) {
        super(parent, "Heart Puzzle", true);
        setupUI(parent, puzzle);
    }

    //method creates the entire puzzle window.
    private void setupUI(JFrame parent, Puzzle puzzle) {
        Color bgColor = new Color(30, 30, 30);
        Color textColor = Color.WHITE;
        Color accentColor = new Color(255, 200, 0); // gold

        //Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Instruction label
        JLabel instruction = new JLabel("How many hearts are in the image?", SwingConstants.CENTER);
        instruction.setForeground(textColor);
        instruction.setFont(new Font("SansSerif", Font.BOLD, 18));
        mainPanel.add(instruction, BorderLayout.NORTH);//Adds label to top of the dialog.

        // Image panel with scaling (custom painting)
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    URL url = new URL(puzzle.getImageUrl());//Gets the image URL from the puzzle object.
                    Image originalImage = ImageIO.read(url);//Downloads and loads the image.
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    if (panelWidth == 0 || panelHeight == 0) return;
                    int imgWidth = originalImage.getWidth(null);
                    int imgHeight = originalImage.getHeight(null);
                    //Calculates how much to resize the image.
                    double scale = Math.min((double) panelWidth / imgWidth, (double) panelHeight / imgHeight);
                    //New image size
                    int scaledWidth = (int) (imgWidth * scale);
                    int scaledHeight = (int) (imgHeight * scale);
                    //Creates resized image with smooth quality.
                    Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    //Center image
                    int x = (panelWidth - scaledWidth) / 2;
                    int y = (panelHeight - scaledHeight) / 2;
                    ////Draws the image on the panel.
                    g.drawImage(scaledImage, x, y, this);
                } catch (IOException e) {
                    e.printStackTrace();
                    g.setColor(Color.RED);
                    g.drawString("Failed to load image", 10, 20);
                }
            }
        };
        imagePanel.setBackground(new Color(50, 50, 50)); // slightly lighter dark for contrast
        imagePanel.setPreferredSize(new Dimension(500, 400));
        mainPanel.add(new JScrollPane(imagePanel), BorderLayout.CENTER);

        // Input panel. Panel for answer input.
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(bgColor);

        JLabel answerLabel = new JLabel("Your answer:");
        answerLabel.setForeground(textColor);
        answerLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inputPanel.add(answerLabel);

        JTextField answerField = new JTextField(5);
        answerField.setBackground(new Color(50, 50, 50));
        answerField.setForeground(textColor);
        answerField.setCaretColor(textColor);
        answerField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inputPanel.add(answerField);

        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(accentColor);
        submitButton.setForeground(Color.BLACK);
        submitButton.setFocusPainted(false);
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        submitButton.addActionListener(e -> {
            try {
                int answer = Integer.parseInt(answerField.getText().trim()); //Convert input to number
                if (answer == puzzle.getSolution()) {                        //Compares with correct answer.
                    solved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(PuzzleDialog.this,
                            "Incorrect. Try again.",
                            "Wrong",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {                             //If user types text instead of number.
                JOptionPane.showMessageDialog(PuzzleDialog.this,
                        "Please enter a number.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        inputPanel.add(submitButton);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

      //Sets panel as dialog content.
        setContentPane(mainPanel);
        setSize(600, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public boolean isSolved() {
        return solved;
    }
}