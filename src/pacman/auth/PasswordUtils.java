/*PasswordUtils class for password hashing and verifications
 Generates a random salt using SecureRandom.
 Base64-encoded salt string*/

package pacman.auth;

import java.security.MessageDigest;//Used to create cryptographic hashes.(SHA-256 Algorithm)
import java.security.NoSuchAlgorithmException;//Exception thrown if algorithm doesn't exist.
import java.security.SecureRandom;//Used to generate random salt values.
import java.util.Base64;//Used to encode binary data into text.

public class PasswordUtils {

    private static final int SALT_LENGTH = 16; // bytes

   
    public static String generateSalt() {     //Static method.. So, No object required
        SecureRandom sr = new SecureRandom(); //Creates a secure random generator.
        byte[] salt = new byte[SALT_LENGTH];  //Creates an array:16bytes
        sr.nextBytes(salt);                   //Fills the array with random bytes.
        return Base64.getEncoder().encodeToString(salt);//Converts the binary salt into a Base64 string.
    }

    
     //Hashes a password with the given salt using SHA-256.
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256"); //Creates a hashing object.Algorithm used:SHA-256
            md.update(salt.getBytes());                              //Adds salt to the hashing process.
            byte[] hashed = md.digest(password.getBytes());          //Hashes the password.
            return Base64.getEncoder().encodeToString(hashed);       //Convert hash into a Base64 string.
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing error", e);
        }
    }

    
     //Verifies a plaintext password against a stored salt and hash.
    public static boolean verifyPassword(String password, String salt, String storedHash) {
        String hash = hashPassword(password, salt); //Re-hash the entered password.
        return hash.equals(storedHash);             //Compare: new hash Vs stored hash. If equal: password correct
    }
}