package net.fivew14.authlogic.verification;

/**
 * Exception thrown when verification fails.
 * Verification failures trigger immediate disconnection.
 */
public class VerificationException extends Exception {
    
    public VerificationException(String message) {
        super(message);
    }
    
    public VerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
