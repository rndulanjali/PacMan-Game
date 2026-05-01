//Handles login and registration logic
package pacman.auth;

public class AuthService {              //Handles login and registration logic
    private UserRepository repository;  //Used to access stored user data. To find user,save user, update user

    //Constructoe
    public AuthService(UserRepository repository) {
        this.repository = repository;  //passed repository object to this clas. So, AuthService can now access user data
    }

    //This method registers a new user
    public boolean register(String username, String password) {
        if (repository.find(username) != null) return false;
        String salt = PasswordUtils.generateSalt();               // generate random salt. Salt protects against:rainbow table attacks
        String hash = PasswordUtils.hashPassword(password, salt); // hash with salt.This creates a secure password hash.
        User user = new User(username, salt, hash);               //Creates a new User object.
        return repository.save(user);                             //Save User
    }

    //Authenticates a user.
    public boolean login(String username, String password) {
        User user = repository.find(username);  //Find User
        if (user == null) return false;         //Check if User Exists. If user does not exist:login fails                                      
        return PasswordUtils.verifyPassword(    ///Verify Password using register() data
        		password,                       //1️.take entered password
        		user.getSalt(),                 //2.get stored salt
        		user.getPasswordHash());        //3.get stored hash
    }
}