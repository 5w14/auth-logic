package net.fivew14.authlogic.crypto;

import com.mojang.logging.LogUtils;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeysProvider {
    private static final KeyPairGenerator tempKeyGenerator;
    private static final KeyPairGenerator constantKeyGenerator;

    static {
        try {
            tempKeyGenerator = KeyPairGenerator.getInstance("X25519");
            constantKeyGenerator = KeyPairGenerator.getInstance("RSA");

            LogUtils.getLogger().info("NewTempKeyPub: {}", tempKeyGenerator.generateKeyPair().getPublic().getEncoded());
            LogUtils.getLogger().info("NewConstKeyPub: {}", constantKeyGenerator.generateKeyPair().getPublic().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not create a key generator", e);
        }
    }

    public static void bootstrap() { }
}
