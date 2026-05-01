/* This class represents a single user in the system.
This imports the Serializable interface.Serializable allows an object to be:
converted into a byte stream, saved to a file, sent over a network, stored in memory.
Here I use:
1. Save users to file
2. Send user objects between systems */

package pacman.auth;

import java.io.Serializable;

public class User implements Serializable {           //User objects can be serialized (saved/transmitted)
    private static final long serialVersionUID = 1L;  //version identifier for serialized objects.This ensures compatibility between saved objects and class versions.
    private String username;
    private String salt;                              //Stores the salt used for password hashing.
    private String passwordHash;
    private int highScore;                           //Stores the user's highest score

    public User(String username, String salt, String passwordHash) {
        this.username = username;
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.highScore = 0;
    }

    //Getter methods
    public String getUsername() { return username; }
    public String getSalt() { return salt; }
    public String getPasswordHash() { return passwordHash; }
    public int getHighScore() { return highScore; }
    //Setter methods
    public void setHighScore(int highScore) { this.highScore = highScore; }
}