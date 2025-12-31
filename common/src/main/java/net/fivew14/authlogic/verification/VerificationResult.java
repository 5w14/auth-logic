package net.fivew14.authlogic.verification;

import java.security.PublicKey;
import java.util.UUID;

/**
 * Result of verification attempt.
 * Contains success status and player information if successful.
 */
public class VerificationResult {
    public final boolean success;
    public final UUID playerUUID;
    public final String username;
    public final PublicKey clientPublicKey;
    public final String failureReason;
    
    private VerificationResult(boolean success, UUID playerUUID, String username,
                               PublicKey clientPublicKey, String failureReason) {
        this.success = success;
        this.playerUUID = playerUUID;
        this.username = username;
        this.clientPublicKey = clientPublicKey;
        this.failureReason = failureReason;
    }
    
    /**
     * Creates a successful verification result.
     * 
     * @param uuid Player's UUID
     * @param username Player's username
     * @param key Client's public key
     * @return Success result
     */
    public static VerificationResult success(UUID uuid, String username, PublicKey key) {
        return new VerificationResult(true, uuid, username, key, null);
    }
    
    /**
     * Creates a failed verification result.
     * 
     * @param reason Failure reason
     * @return Failure result
     */
    public static VerificationResult failure(String reason) {
        return new VerificationResult(false, null, null, null, reason);
    }
}
