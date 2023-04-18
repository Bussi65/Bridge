package com.bridge;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.bridge.config.MapConfig;
import com.bridge.config.MapSpawn;
import com.google.common.collect.Maps;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.graalvm.compiler.lir.sparc.SPARCMove;

public class GameSession {
    private static final HashMap<UUID, GameSession> activePlayer = new HashMap<UUID, GameSession>();
    private ArrayList<PlayerInfo> playersInfo = new ArrayList<PlayerInfo>();
    private World world;
    private MapConfig mapConfig;

    public GameSession(Map map, Player[] player) {
        world = map.createWorld();
        mapConfig = map.config;
        MapSpawn[] mapSpawns = map.config.getSpawns();
        for (int i = 0; i < player.length; i++) {
            if(i < map.config.getMaxPlayer()) {
                playersInfo.add(new PlayerInfo(player[i], mapSpawns[i]));
                activePlayer.put(player[i].getUniqueId(), this);
                player[i].teleport(mapSpawns[i].getSpawn().toLocation(world));
                player[i].setGameMode(GameMode.SURVIVAL); // Temporary
                player[i].sendMessage("You were added at: " + mapSpawns[i].getSpawn().toLocation(world).toString()); // DEBUG
            }else {
                activePlayer.put(player[i].getUniqueId(), this);
                player[i].setGameMode(GameMode.SPECTATOR);
                player[i].teleport(new Location(world, 0, 60, 0));
                player[i].sendMessage("You were NOT added"); // DEBUG
            }
        }
    }

    public static boolean removePlayer(UUID uuid) {
        if (activePlayer.containsKey(uuid)) {
            ArrayList<PlayerInfo> playersInfo = activePlayer.get(uuid).playersInfo;
            for (PlayerInfo p : playersInfo) {
                if (p.getUUID() == uuid) {
                    playersInfo.remove(p);
                    activePlayer.remove(p.getUUID());
                    p.getBukkitPlayer().setGameMode(GameMode.SPECTATOR); // Temp
                    p.getBukkitPlayer().teleport(Bridge.pastpoint);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addPlayer(Player player) {
        for (PlayerInfo p : playersInfo) {
            if (p.getUUID() == player.getUniqueId()) {
                return false;
            }
        }
        playersInfo.add(new PlayerInfo(player, getFreeSpawn()));
        activePlayer.put(player.getUniqueId(), this);
        return true;
    }

    private MapSpawn getFreeSpawn() {
        if(mapConfig.getMaxPlayer() - playersInfo.size() - 1 > 0) {
            return mapConfig.getSpawns()[playersInfo.size() + 1];
        }else {
            return null;
        }
    }
}
