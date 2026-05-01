/*This class manages: current logged-in user
 Uses a static variable to hold the logged-in username – simple but global.
 Also handles logout by clearing the session and deleting the remember-me token file.*/

package pacman.auth;

import java.io.File; //Used to manage files.

public class SessionManager {
    private static String loggedInUser; //Stores the current user

    //This method starts a user session.
    public static void startSession(String username) {
        loggedInUser = username;//Store username.
    }

    //Returns current username.
    public static String getCurrentUser() {
        return loggedInUser;
    }

    //Logs the user out.
    public static void logout() {
        loggedInUser = null; //Clears session. Meaning:no user logged in
        // Delete the remember me token file
        File tokenFile = new File(".pacman_token"); //This file stores the remember-me login token
        if (tokenFile.exists()) {                   //Check if the token file exists.If then,
            tokenFile.delete();                     //Deletes the token file.Meaning:isable auto login
        }
    }
}