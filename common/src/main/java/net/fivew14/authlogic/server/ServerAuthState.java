package net.fivew14.authlogic.server;

import net.fivew14.authlogic.server.state.CommonAuthState;
import net.fivew14.authlogic.server.state.InProgressAuthState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages authentication states for all active connections.
 * Uses thread-safe map for concurrent access.
 * 
 * States are keyed by the server nonce, which is a cryptographically random
 * 64-bit value generated for each authentication challenge. This ensures
 * reliable correlation between challenges and responses at the protocol level,
 * independent of transport-layer connection identifiers.
 */
public class ServerAuthState {
    /**
     * Map of server nonce -> auth state.
     * Using Long (server nonce) as key ensures protocol-level correlation.
     */
    public static final Map<Long, CommonAuthState> STATE = new ConcurrentHashMap<>();

    /**
     * Creates a new InProgressAuthState for a connection.
     * 
     * @return New in-progress auth state
     */
    public static InProgressAuthState newAuthState() {
        return new InProgressAuthState();
    }
    
    /**
     * Removes an auth state by its server nonce.
     * Should be called after authentication completes or times out.
     * 
     * @param serverNonce The server nonce to remove
     * @return The removed state, or null if not found
     */
    public static CommonAuthState remove(long serverNonce) {
        return STATE.remove(serverNonce);
    }
    
    /**
     * Checks if a state exists for the given server nonce.
     * 
     * @param serverNonce The server nonce to check
     * @return true if state exists
     */
    public static boolean exists(long serverNonce) {
        return STATE.containsKey(serverNonce);
    }
}
