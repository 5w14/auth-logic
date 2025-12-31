package net.fivew14.authlogic.server;

import net.fivew14.authlogic.crypto.KeysProvider;
import net.fivew14.authlogic.crypto.OptionalKeyPair;
import net.fivew14.authlogic.server.state.CommonAuthState;
import net.fivew14.authlogic.server.state.InProgressAuthState;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Map;

public class ServerAuthState {
    public Map<String, CommonAuthState> STATE = new HashMap<>();

    public static InProgressAuthState newAuthState() {
        var state = new InProgressAuthState();
        state.serverTemporaryKeys = OptionalKeyPair.of(KeysProvider.generateTemporaryKeyPair());
        state.serverNonce = RandomSource.create().nextInt();
//        state.serverConstPublicKey = // TODO;
        return state;
    }
}
