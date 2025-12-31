package net.fivew14.authlogic.crypto;

import com.mojang.logging.LogUtils;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class KeysProvider {
    private static final KeyPairGenerator tempKeyGenerator;
    private static final KeyPairGenerator constantKeyGenerator;

    static {
        try {
            tempKeyGenerator = KeyPairGenerator.getInstance("X25519");
            constantKeyGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not create a key generator", e);
        }
    }

    public static KeyPair generateTemporaryKeyPair() {
        return tempKeyGenerator.generateKeyPair();
    }

    public static KeyPair generateConstantKeyPair() {
        return constantKeyGenerator.generateKeyPair();
    }

    public static SecretKeySpec generateSharedSecret(PrivateKey ownPrivateKey, PublicKey otherPartyPublicKey) {
        try {
            var keyAgreement = KeyAgreement.getInstance("XDH");
            keyAgreement.init(ownPrivateKey);
            keyAgreement.doPhase(otherPartyPublicKey, true);
            return new SecretKeySpec(keyAgreement.generateSecret(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a cryptographically secure random nonce.
     * 
     * @return Random long value for use as nonce
     */
    public static long generateNonce() {
        return new SecureRandom().nextLong();
    }

    public static void bootstrap() { }
}
