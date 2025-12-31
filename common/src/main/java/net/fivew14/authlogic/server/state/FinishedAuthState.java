package net.fivew14.authlogic.server.state;

public class FinishedAuthState implements IAuthState {
    public final String username;

    @Override
    public boolean isAuthenticated() {
        return true;
//        return this.authentication.isAuthenticated();
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    public FinishedAuthState(String username) {
        this.username = username;
    }
}
