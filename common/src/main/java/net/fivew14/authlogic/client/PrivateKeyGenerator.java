package net.fivew14.authlogic.client;

import java.security.KeyPair;
import java.security.PublicKey;

public class PrivateKeyGenerator {
    public static KeyPair getKeyPair(byte[] passwordHash, PublicKey serverPublicKey) {
        // Use seeded random to derive a server-specific public/private key pair.
        // After it is generated, the server's public key and the client's private key should be saved.
    }
}
