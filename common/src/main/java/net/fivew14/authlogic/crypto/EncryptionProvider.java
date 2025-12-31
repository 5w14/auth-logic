package net.fivew14.authlogic.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

public class EncryptionProvider {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LEN_BYTES = 12;
    private static final int TAG_LEN_BITS = 128;

    private static final SecureRandom RNG = new SecureRandom();

    /**
     * Encrypts plaintext with AES-GCM and returns a single blob:
     * [1 byte version][12 bytes IV][ciphertext+tag...]
     */
    public static byte[] encrypt(byte[] rawKey32, byte[] plaintext) {
        if (rawKey32 == null || rawKey32.length != 32)
            throw new IllegalArgumentException("AES-256 key must be 32 bytes");
        if (plaintext == null) throw new IllegalArgumentException("plaintext is null");

        byte version = 1;
        byte[] iv = new byte[IV_LEN_BYTES];
        RNG.nextBytes(iv);

        SecretKey key = new SecretKeySpec(rawKey32, "AES");

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BITS, iv));

            byte[] ciphertextWithTag = cipher.doFinal(plaintext);

            ByteBuffer out = ByteBuffer.allocate(1 + IV_LEN_BYTES + ciphertextWithTag.length);
            out.put(version);
            out.put(iv);
            out.put(ciphertextWithTag);
            return out.array();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypts blob produced by encrypt():
     * [1 byte version][12 bytes IV][ciphertext+tag...]
     */
    public static byte[] decrypt(byte[] rawKey32, byte[] blob) {
        if (rawKey32 == null || rawKey32.length != 32)
            throw new IllegalArgumentException("AES-256 key must be 32 bytes");
        if (blob == null || blob.length < 1 + IV_LEN_BYTES + 1) throw new IllegalArgumentException("blob too short");

        ByteBuffer in = ByteBuffer.wrap(blob);
        byte version = in.get();
        if (version != 1) throw new IllegalArgumentException("unsupported version: " + version);

        byte[] iv = new byte[IV_LEN_BYTES];
        in.get(iv);

        byte[] ciphertextWithTag = new byte[in.remaining()];
        in.get(ciphertextWithTag);

        SecretKey key = new SecretKeySpec(rawKey32, "AES");

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BITS, iv));
            return cipher.doFinal(ciphertextWithTag);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
