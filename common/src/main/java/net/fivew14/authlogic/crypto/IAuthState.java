package net.fivew14.authlogic.crypto;

import java.security.PublicKey;

public interface IAuthState {
    long serverNonce(); // Requested from the client
    long clientNonce(); // Requested from the server

    PublicKey clientTempKey();
    PublicKey serverTempKey();

    PublicKey clientConstantKey();
    PublicKey serverConstantKey();
}
