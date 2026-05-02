package net.fivew14.authlogic.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * Utility class for file-based storage operations.
 * Handles JSON and text file I/O for authentication data.
 * <p>
 * Storage locations:
 * - Root authlogic/ directory (key files):
 *   - server_private_key.txt: Server's private key
 *   - server_storage.json: Registered player public keys
 *   - client_password.txt: Client's hashed password
 *   - client_servers.json: Trusted server list
 * - Config authlogic/ directory (configuration):
 *   - server_whitelist.json: Whitelist integration
 */
public class SavedStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_DIR = "config/authlogic";
    private static final String ROOT_DIR = "authlogic";

    private static boolean hasMigrated = false;

    /**
     * Gets the config directory path.
     * Creates the directory if it doesn't exist.
     *
     * @return Path to config/authlogic/
     */
    public static Path getConfigDir() {
        Path dir = Paths.get(CONFIG_DIR);
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create config directory", e);
        }
        return dir;
    }

    /**
     * Gets the root authlogic directory path.
     * Creates the directory if it doesn't exist.
     *
     * @return Path to authlogic/
     */
    public static Path getRootDir() {
        Path dir = Paths.get(ROOT_DIR);
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create root authlogic directory", e);
        }
        return dir;
    }

    /**
     * Migrates files from config/authlogic/ to root authlogic/ directory.
     * Only migrates if the file doesn't already exist in the root directory.
     * Thread-safe and idempotent.
     */
    public static synchronized void migrateFilesFromConfig() {
        if (hasMigrated) {
            return;
        }

        migratePrivateFile(getConfigDir().resolve("server_private_key.txt"), getRootDir().resolve("server_private_key.txt"));
        migrateFile(getConfigDir().resolve("server_storage.json"), getRootDir().resolve("server_storage.json"));
        migratePrivateFile(getConfigDir().resolve("client_password.txt"), getRootDir().resolve("client_password.txt"));
        migrateFile(getConfigDir().resolve("client_servers.json"), getRootDir().resolve("client_servers.json"));
        hasMigrated = true;
    }

    private static void migrateFile(Path oldPath, Path newPath) {
        migrateFile(oldPath, newPath, false);
    }

    private static void migratePrivateFile(Path oldPath, Path newPath) {
        migrateFile(oldPath, newPath, true);
    }

    private static void migrateFile(Path oldPath, Path newPath, boolean privatePermissions) {
        boolean oldExists = Files.exists(oldPath);
        boolean newExists = Files.exists(newPath);

        if (!oldExists || newExists) {
            if (newExists && privatePermissions) {
                setOwnerOnlyPermissions(newPath);
            }
            return;
        }

        try {
            Files.copy(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            if (privatePermissions) {
                setOwnerOnlyPermissions(newPath);
            }
            Files.delete(oldPath);
            LOGGER.info("Migrated {} to {}", oldPath, newPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to migrate file from " + oldPath + " to " + newPath, e);
        }
    }

    /**
     * Gets path to server storage file.
     *
     * @return Path to authlogic/server_storage.json
     */
    public static Path getServerStoragePath() {
        return getRootDir().resolve("server_storage.json");
    }

    /**
     * Gets path to server private key file.
     *
     * @return Path to authlogic/server_private_key.txt
     */
    public static Path getServerPrivateKeyPath() {
        return getRootDir().resolve("server_private_key.txt");
    }

    /**
     * Gets path to client password file.
     *
     * @return Path to authlogic/client_password.txt
     */
    public static Path getClientPasswordPath() {
        return getRootDir().resolve("client_password.txt");
    }

    /**
     * Gets path to client servers file.
     *
     * @return Path to authlogic/client_servers.json
     */
    public static Path getClientServersPath() {
        return getRootDir().resolve("client_servers.json");
    }

    /**
     * Gets path to server whitelist file.
     *
     * @return Path to server_whitelist.json
     */
    public static Path getServerWhitelistPath() {
        return getConfigDir().resolve("server_whitelist.json");
    }

    /**
     * Reads JSON from a file.
     *
     * @param path  File path
     * @param clazz Class to deserialize to
     * @return Deserialized object
     * @throws IOException if file doesn't exist or read fails
     */
    public static <T> T readJson(Path path, Class<T> clazz) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + path);
        }
        String json = Files.readString(path, StandardCharsets.UTF_8);
        return GSON.fromJson(json, clazz);
    }

    /**
     * Writes JSON to a file.
     *
     * @param path   File path
     * @param object Object to serialize
     * @throws IOException if write fails
     */
    public static <T> void writeJson(Path path, T object) throws IOException {
        String json = GSON.toJson(object);
        Files.writeString(path, json, StandardCharsets.UTF_8);
    }

    /**
     * Reads text from a file.
     *
     * @param path File path
     * @return File contents as string
     * @throws IOException if file doesn't exist or read fails
     */
    public static String readText(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + path);
        }
        return Files.readString(path, StandardCharsets.UTF_8).trim();
    }

    /**
     * Writes text to a file.
     *
     * @param path    File path
     * @param content Content to write
     * @throws IOException if write fails
     */
    public static void writeText(Path path, String content) throws IOException {
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    /**
     * Writes sensitive text to a file with owner-only permissions where supported.
     * On POSIX filesystems this creates/replaces the file as 0600 (rw-------).
     * On non-POSIX filesystems, falls back to the platform default permissions.
     *
     * @param path    File path
     * @param content Sensitive content to write
     * @throws IOException if write fails
     */
    public static void writePrivateText(Path path, String content) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        FileAttribute<Set<PosixFilePermission>> permissions = PosixFilePermissions.asFileAttribute(OWNER_READ_WRITE);
        Path tempPath;
        try {
            tempPath = Files.createTempFile(parent, path.getFileName().toString(), ".tmp", permissions);
        } catch (UnsupportedOperationException e) {
            Files.writeString(path, content, StandardCharsets.UTF_8);
            return;
        }

        try {
            Files.writeString(tempPath, content, StandardCharsets.UTF_8);
            try {
                Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
            }
            setOwnerOnlyPermissions(path);
        } finally {
            Files.deleteIfExists(tempPath);
        }
    }

    /**
     * Restricts a file to owner read/write only where POSIX permissions are supported.
     *
     * @param path File path
     */
    public static void setOwnerOnlyPermissions(Path path) {
        try {
            Files.setPosixFilePermissions(path, OWNER_READ_WRITE);
        } catch (UnsupportedOperationException e) {
            LOGGER.debug("POSIX file permissions are not supported for {}", path);
        } catch (IOException e) {
            LOGGER.warn("Failed to restrict permissions for {}", path, e);
        }
    }

    private static final Set<PosixFilePermission> OWNER_READ_WRITE = Set.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE
    );
}
