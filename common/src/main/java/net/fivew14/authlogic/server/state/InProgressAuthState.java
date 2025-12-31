package net.fivew14.authlogic.server.state;

public class InProgressAuthState implements IAuthState {
    public final String username;

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    public InProgressAuthState(String username) {
        this.username = username;
    }
}
