package net.fivew14.authlogic.server;

import net.fivew14.authlogic.server.state.IAuthState;

import java.util.HashMap;
import java.util.Map;

public class ServerAuthState {
    public Map<String, IAuthState> STATE = new HashMap<>();
}
