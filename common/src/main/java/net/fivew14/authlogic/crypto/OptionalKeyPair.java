package net.fivew14.authlogic.crypto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;

public class OptionalKeyPair {
    public PrivateKey privateKey = null;
    public PublicKey publicKey = null;

    public Optional<PrivateKey> getPrivateKey() {
        return Optional.ofNullable(privateKey);
    }

    public Optional<PublicKey> getPublicKey() {
        return Optional.ofNullable(publicKey);
    }

    public boolean hasPrivateKey() {
        return privateKey != null && publicKey != null;
    }

    public static OptionalKeyPair of(KeyPair keyPair) {
        var okp = new OptionalKeyPair();
        okp.privateKey = keyPair.getPrivate();
        okp.publicKey = keyPair.getPublic();
        return okp;
    }
}
