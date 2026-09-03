package net.fivew14.authlogic.client;

import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fivew14.authlogic.utilities.SavedStorage;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public record AuthlogicClientConfig(boolean shouldSkipOnlineModeCheck) {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final AuthlogicClientConfig DEFAULT = new AuthlogicClientConfig(false);
    private static final Codec<AuthlogicClientConfig> CODEC = RecordCodecBuilder.<AuthlogicClientConfig>create((instance) -> instance.group(
            Codec.BOOL.fieldOf("force_offline_mode").forGetter(AuthlogicClientConfig::shouldSkipOnlineModeCheck)
    ).apply(instance, AuthlogicClientConfig::new));

    private static String toJsonString(AuthlogicClientConfig config) {
        var result = CODEC.encode(config, JsonOps.INSTANCE, JsonOps.INSTANCE.empty());
        return result.getOrThrow().toString();
    }

    private static AuthlogicClientConfig fromJsonString(String data) {
        var result = CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(data));
        if (result.isSuccess())
            return result.getOrThrow().getFirst();

        result.error().ifPresentOrElse(
                err -> LOGGER.error("Could not read config.json: {}", err.message()),
                () -> LOGGER.error("Could not read config.json")
        );

        return DEFAULT;

    }

    public static AuthlogicClientConfig create() {
        var file = SavedStorage.getClientConfigPath();

        try {
            if (file.toFile().createNewFile()) {
                Files.writeString(file, toJsonString(DEFAULT), Charset.defaultCharset());
                return DEFAULT;
            }

            var data = Files.readString(file);
            return fromJsonString(data);
        } catch (IOException e) {
            LOGGER.error("Could not create or read config.json", e);
            return DEFAULT;
        }
    }
}

