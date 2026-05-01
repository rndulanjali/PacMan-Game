/*Displays the top 3 high scores.Fetches data from the database via UserRepository.
This class:
1️.Retrieves the top 3 high scores from the database
2️.Displays them in a leaderboard dialog
3️.Uses medal icons (🥇🥈🥉) for ranking
4️.Uses Swing components to design the UI
5️.Allows users to close the leaderboard window*/

package pacman.ui;

import pacman.auth.User;
import pacman.auth.UserRepository;
import javax.swing.*;
import java.awt.*;
import java.util.List;


public class LeaderboardDialog extends JDialog {
    public LeaderboardDialog(JFrame parent) {
        super(parent, "Leaderboard", true);
        setupUI(parent);
    }

    private void setupUI(JFrame parent) {
        Color bgColor = new Color(30, 30, 30);
        Color textColor = Color.WHITE;
        Color gold = new Color(255, 200, 0);
        Color silver = new Color(192, 192, 192);
        Color bronze = new Color(205, 127, 50);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(gold, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("🏆 Top 3 Winners 🏆", SwingConstants.CENTER);
        title.setForeground(gold);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        //Database Access
        UserRepository repo = new UserRepository();   //Creates an object of UserRepository.
        List<User> topUsers = repo.getTopScores(3);   //Calls the method getTopScores().

        JPanel listPanel = new JPanel();
        listPanel.setBackground(bgColor);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        String[] medals = {"🥇", "🥈", "🥉"};         //Creates an array of medal emojis.
        Color[] colors = {gold, silver, bronze};     //Creates an array of colors for the medals.

        //Loop Through Top Users
        for (int i = 0; i < topUsers.size(); i++) {
            User u = topUsers.get(i);//Retrieves the user object from the list.
            JPanel entry = new JPanel(new FlowLayout(FlowLayout.LEFT));
            entry.setBackground(bgColor);

            JLabel medalLabel = new JLabel(medals[i]);
            medalLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
            medalLabel.setForeground(colors[i]);
            entry.add(medalLabel);

            JLabel nameLabel = new JLabel(u.getUsername() + " - " + u.getHighScore() + " points");
            nameLabel.setForeground(textColor);
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
            entry.add(nameLabel);

            listPanel.add(entry);
        }

        //Checks if the leaderboard has no users.
        if (topUsers.isEmpty()) {
            JLabel empty = new JLabel("No scores yet!");
            empty.setForeground(textColor);
            empty.setFont(new Font("SansSerif", Font.PLAIN, 18));
            listPanel.add(empty);
        }

        panel.add(new JScrollPane(listPanel), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(gold);
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(bgColor);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        //Final Window Settings
        setContentPane(panel);//Sets the main panel as the dialog content.
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);//Closes dialog but does not stop the program.
    }
}