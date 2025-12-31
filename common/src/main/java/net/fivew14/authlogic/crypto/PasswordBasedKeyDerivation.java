package net.fivew14.authlogic.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Derives deterministic RSA keypairs from password hashes and server public keys.
 * This allows clients to regenerate the same keypair across sessions and server IP changes.
 * 
 * SECURITY: Never accepts plain-text passwords. All methods require pre-hashed passwords.
 * Uses PBKDF2-HMAC-SHA256 with 100,000 iterations to derive a seed from the password hash,
 * then uses that seed to deterministically generate an RSA keypair.
 */
public class PasswordBasedKeyDerivation {
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH_BITS = 256; // 32 bytes for seed

    /**
     * Hashes a password using SHA-256.
     * This should be called immediately when password is received to avoid storing plain text.
     * 
     * @param plainPassword Plain text password (will be hashed immediately)
     * @return SHA-256 hash as hex string
     */
    public static String hashPassword(String plainPassword) {
        byte[] hash = Hasher.sha256(plainPassword);
        return bytesToHex(hash);
    }

    /**
     * Derives a deterministic RSA keypair from a password hash and server public key.
     * Same password hash + server key will always produce the same keypair.
     * 
     * SECURITY: This method expects a SHA-256 hash (64 hex chars), NOT a plain password.
     * 
     * @param passwordHash SHA-256 hash of the password (64 hex characters)
     * @param serverPublicKey Server's RSA public key (used as salt)
     * @return Deterministic RSA keypair
     * @throws RuntimeException if key derivation fails
     * @throws IllegalArgumentException if passwordHash is not a valid SHA-256 hash
     */
    public static KeyPair deriveKeyPair(String passwordHash, PublicKey serverPublicKey) {
        // Validate that we received a hash, not a plain password
        if (passwordHash == null || passwordHash.length() != 64) {
            throw new IllegalArgumentException(
                "Password hash must be a 64-character hex string (SHA-256 hash). " +
                "Use hashPassword() to hash plain passwords before calling this method."
            );
        }
        
        try {
            // Use server public key bytes as salt for PBKDF2
            byte[] salt = serverPublicKey.getEncoded();
            
            // Convert password hash to bytes for PBKDF2 input
            // Using the hash string as input ensures deterministic derivation
            byte[] passwordHashBytes = passwordHash.getBytes(StandardCharsets.UTF_8);
            
            // Derive key material using PBKDF2
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            KeySpec spec = new PBEKeySpec(
                passwordHash.toCharArray(), 
                salt, 
                ITERATIONS, 
                KEY_LENGTH_BITS
            );
            byte[] derivedKey = factory.generateSecret(spec).getEncoded();
            
            // Use derived key as seed for deterministic random number generator
            SecureRandom seededRandom = SecureRandom.getInstance("SHA1PRNG");
            seededRandom.setSeed(derivedKey);
            
            // Generate RSA keypair deterministically
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, seededRandom); // 2048-bit RSA
            
            return keyPairGenerator.generateKeyPair();
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to derive keypair from password hash", e);
        }
    }

    /**
     * Derives a deterministic RSA keypair from a password hash and server public key bytes.
     * Convenience method that reconstructs the PublicKey from bytes.
     * 
     * @param passwordHash SHA-256 hash of the password (64 hex characters)
     * @param serverPublicKeyBytes Server's RSA public key as encoded bytes
     * @return Deterministic RSA keypair
     * @throws RuntimeException if key derivation fails
     */
    public static KeyPair deriveKeyPair(String passwordHash, byte[] serverPublicKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(serverPublicKeyBytes);
            PublicKey serverPublicKey = keyFactory.generatePublic(keySpec);
            return deriveKeyPair(passwordHash, serverPublicKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to derive keypair from password hash and key bytes", e);
        }
    }
    
    /**
     * Converts byte array to hex string.
     * 
     * @param bytes Bytes to convert
     * @return Hex string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
