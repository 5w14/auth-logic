package net.fivew14.authlogic.crypto;

public interface IAuthentificationMode {
    String type(); // "online" and "offline"
    byte[] getAuthBlob(IAuthState state);
}
