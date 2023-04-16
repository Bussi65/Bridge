package com.bridge;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.bridge.config.MapSpawn;
import com.google.common.collect.Maps;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class GameSession {
    private ArrayList<PlayerInfo> playersInfo = new ArrayList<PlayerInfo>();
    private World world;

    public GameSession(Map map, Player[] player) {
        world = map.createWorld();
        MapSpawn[] mapSpawns = map.config.getSpawns();
        for (int i = 0; i < player.length; i++) {
            if(i < map.config.getMaxPlayer()) {
                playersInfo.add(new PlayerInfo(player[i], mapSpawns[i]));
                player[i].teleport(mapSpawns[i].getSpawn().toLocation(world));
            }else {
                player[i].setGameMode(GameMode.SPECTATOR);
                player[i].teleport(new Location(world, 0, 60, 0));
            }
        }
    }
}
