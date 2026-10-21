package security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Enterprise Password Security utility using salted SHA-256.
 * Seamlessly verifies legacy plaintext passwords and converts them to salted hashes.
 */
public class PasswordSecurity {

    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "SHA-256";

    /**
     * Hashes a raw password with a newly generated cryptographic salt.
     * Format: salt$hash
     */
    public static String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);

        String hash = computeHash(rawPassword, saltBase64);
        return saltBase64 + "$" + hash;
    }

    /**
     * Verifies raw input password against stored password string.
     * Supports both modern salted hashes and legacy plaintext for backward compatibility.
     */
    public static boolean verifyPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }

        // Check if stored password is in salt$hash format
        if (storedPassword.contains("$")) {
            String[] parts = storedPassword.split("\\$");
            if (parts.length == 2) {
                String salt = parts[0];
                String expectedHash = parts[1];
                String computedHash = computeHash(rawPassword, salt);
                return expectedHash.equals(computedHash);
            }
        }

        // Legacy plaintext comparison fallback
        return rawPassword.equals(storedPassword);
    }

    private static String computeHash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error computing password hash", e);
        }
    }
}
