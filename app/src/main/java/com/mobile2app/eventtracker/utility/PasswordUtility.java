package com.mobile2app.eventtracker.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 *  Utility class for generating the salt and hashed password
 *  for a user.
 */
public class PasswordUtility {

    // Create a random salt
    public static byte[] createSalt() {
        byte[] bytes = new byte[20];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }

    // Hash a password with a given salt
    public static byte[] hashPassword(String password, byte[] salt) {
        MessageDigest md;
        try {
            // Get an instance of the SHA-512 MessageDigest
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(salt);
        // Hash the password and return the result
        return md.digest(password.getBytes());
    }
}