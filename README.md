# authlogic

> simple and effective hybrid authentication for modded minecraft servers.

authlogic lets servers run with `online-mode=false` while still verifying player identity. players with official microsoft/mojang accounts can authenticate normally, and players without one can use a password-derived key that is unique to each server.

works on minecraft 1.21.1 with fabric or neoforge.

## features

- hybrid authentication: official accounts and password-based accounts can play together
- per-server password-derived keys for offline players
- server key verification to help prevent impersonation
- optional authlogic whitelist
- admin commands for status, player lists, whitelist management, and key resets

## for players

1. install authlogic on your client
2. join an authlogic-enabled server
3. if you have an official account, you are done
4. if you do not, enter a password once during setup

> [!WARNING]
> do not share your authlogic folder. it contains trusted server data and saved authentication material.

## for server admins

1. install authlogic on the server
2. install authlogic on every client that should join
3. set this in `server.properties`:

```properties
online-mode=false
```

4. start the server
5. optionally configure the authlogic whitelist

> [!WARNING]
> do not share your server's authlogic folder. it contains server identity and player authentication data.

## how it works

1. the server sends a signed challenge and its public key
2. the client verifies the server key
3. official-account players authenticate using mojang profile key material
4. offline players derive a unique key from their password and the server public key
5. the client sends proof of identity
6. the server verifies the player key and completes authentication

## why it is secure

- passwords never leave the player's computer
- offline player keys are unique per server
- authentication uses asymmetric keys instead of shared secrets
- the handshake is encrypted
- online-mode players use mojang-provided keys to prove their identity

<div align=center>

[modrinth](https://modrinth.com/mod/authlogic) / [discord](https://discord.gg/VAC7ZMTuPU) / [ko-fi](https://ko-fi.com/fw14)

</div>


