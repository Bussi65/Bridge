package com.bridge;

import com.bridge.config.MapSpawn;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerInfo {
    private Player bukkitPlayer;
    private UUID uuid;
    private MapSpawn mapSpawn;

    public PlayerInfo(Player player, MapSpawn mapSpawn) {
        bukkitPlayer = player;
        uuid = player.getUniqueId();
        this.mapSpawn = mapSpawn;
    }

    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public void setBukkitPlayer(Player bukkitPlayer) {
        this.bukkitPlayer = bukkitPlayer;
        uuid = bukkitPlayer.getUniqueId();
    }

    public UUID getUUID() {
        return uuid;
    }

    public MapSpawn getSpawnPoint() {
        return mapSpawn;
    }

    public void setSpawnPoint(MapSpawn mapSpawn) {
        this.mapSpawn = mapSpawn;
    }
}
