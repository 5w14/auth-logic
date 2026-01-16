# AGENTS.md - AuthLogic Development Guide

This file provides guidelines for AI agents working on the AuthLogic codebase.

## Project Overview

AuthLogic is a Minecraft mod providing password-based authentication for multiplayer servers. It's a cross-platform mod using Architectury to target both Fabric and Forge loaders for Minecraft 1.20.1.

**Tech Stack:**
- Java 17
- Gradle with Loom (Minecraft modding toolchain)
- Mojang official mappings
- Architectury API for platform abstraction

**Modules:**
- `common/` - Shared logic (crypto, state management, networking interfaces)
- `fabric/` - Fabric loader implementation
- `forge/` - Forge loader implementation

## Build Commands

```bash
# Build all platforms (fabric, forge)
./gradlew build

# Build specific platform
./gradlew :fabric:build
./gradlew :forge:build

# Clean build artifacts
./gradlew clean

# Run with dev environment (opens Minecraft with mod installed)
./gradlew :fabric:runClient
./gradlew :forge:runClient

# Build and publish to local maven
./gradlew publishToMavenLocal

# Compile classes only (faster than full build)
./gradlew classes

# Run tests
./gradlew test
./gradlew :common:test
./gradlew :fabric:test
./gradlew :forge:test

# Run a single test class
./gradlew test --tests "net.fivew14.authlogic.crypto.*"

# Run a single test method
./gradlew test --tests "net.fivew14.authlogic.crypto.SomeTest.shouldDoX"
```

## Code Style Guidelines

### General Principles

- Write clear, self-documenting code with descriptive names
- Add Javadoc comments for public APIs and complex logic
- Keep methods focused and under 50 lines when possible
- Use `final` variables for immutable references

### Naming Conventions

- **Classes**: PascalCase (e.g., `AuthLogic`, `ServerAuthState`)
- **Methods/variables**: camelCase (e.g., `initServerStorage()`, `serverNonce`)
- **Constants**: SCREAMING_SNAKE_CASE (e.g., `AUTH_STATE_TIMEOUT_MS`)
- **Packages**: lowercase with dots (e.g., `net.fivew14.authlogic.crypto`)
- **Records**: PascalCase (e.g., `AuthenticatedPlayer`)

### Java Language Features

- Use **records** for simple data carriers (immutable, auto-generated equals/hashCode/toString)
- Use **interfaces** for abstraction (e.g., `VerificationCodec`)
- Use **sealed classes** or **abstract classes** for controlled hierarchies
- Prefer **static nested classes** over inner classes when they don't need outer instance access
- Use **var** for local variable type inference when the type is obvious from context
- Use `CompletableFuture` for async operations (see `MojangProfileFetcher`)

### Imports

Group imports in this order:
1. Java standard library (`java.*`, `javax.*`)
2. Minecraft/Forge/Fabric imports (`net.minecraft.*`, `net.fabricmc.*`, `net.minecraftforge.*`)
3. Third-party libraries (`com.mojang.*`, `dev.architectury.*`, `org.slf4j.*`, `com.google.gson.*`)
4. Internal project imports (`net.fivew14.authlogic.*`)

Blank line between groups. Avoid wildcard imports.

### Formatting

- Use 4 spaces for indentation (not tabs)
- Keep line length under 150 characters when reasonable
- Use blank lines to separate logical sections within methods
- Opening braces on same line as declaration
- No unnecessary blank lines between consecutive method declarations

### Error Handling

- Use specific exceptions (create custom ones like `VerificationException`)
- Wrap checked exceptions in `RuntimeException` for internal errors
- Log errors with context before throwing: `LOGGER.error("Failed to initialize", e)`
- Validate inputs and throw `IllegalArgumentException` or `IllegalStateException` early
- Never expose internal exceptions to external APIs without wrapping

### Logging

- Use `org.slf4j.Logger` via `LogUtils.getLogger()`
- Pattern: `LOGGER.debug(...)` for detailed tracing, `LOGGER.info(...)` for significant events, `LOGGER.warn(...)` for concerning situations, `LOGGER.error(...)` for failures
- Use parameterized logging: `LOGGER.info("Message {}", value)` not string concatenation
- Include relevant context in log messages (player names, nonces, operation details)

### Architecture Patterns

- **ServerAuthState**: Static state management with `ConcurrentHashMap` for thread safety
- **Codec pattern**: `VerificationCodec` for encoding/decoding verification payloads
- **State pattern**: `CommonAuthState` with subclasses for auth flow stages
- **Provider pattern**: `KeysProvider`, `EncryptionProvider` for crypto operations
- **Networking**: Platform-specific implementations (`FabricNetworking`, `ForgeNetworking`)

### API Caching

- Profile fetcher: 60-minute cache (`MojangProfileFetcher`)
- Public key fetcher: 120-minute cache (`MojangPublicKeyFetcher`)
- Use expired-cache fallback on API failure rather than failing entirely
- No TTL randomization - use fixed durations for consistency

### Platform-Specific Code

- Keep common logic in `common/` module
- Platform implementations go in `fabric/` and `forge/` modules
- Use `@Environment(Env.CLIENT)` or `@Environment(Env.SERVER)` annotations for conditional code
- Use `Platform.getEnvironment()` for runtime checks

### Testing

- Place tests in `src/test/java/` within each module
- Use JUnit 5 (`org.junit.jupiter.api.*`)
- Name test methods descriptively: `shouldReturnCorrectHashWhenValidInput()`
- Test edge cases and error conditions, not just happy paths
- Mock external dependencies with Mockito if needed

### Commit Messages

- Use present tense: "Add" not "Added", "Fix" not "Fixed"
- Reference issue numbers when applicable
- Keep first line under 50 characters, describe "why" not just "what"
- Example: "Increase Mojang API cache durations to reduce 429 errors"

### Security Considerations

- Never log passwords or sensitive cryptographic material (nonces are OK)
- Use constant-time comparisons for cryptographic operations where appropriate
- Validate all external inputs
- Follow Minecraft mod security best practices for network communications
- Key derivation must use `DeterministicSecureRandom` for portability
- RSA keys must be explicitly sized (4096 bits minimum)
