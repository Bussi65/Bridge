package com.bridge.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerStats {
    private static HashMap<UUID, PlayerStats> instances = new HashMap<UUID, PlayerStats>();
    public PlayerStats getPlayerStats(UUID uuid) {
        if (instances.containsKey(uuid)) {
            return instances.get(uuid);
        }else {
            PlayerStats stat = new PlayerStats();
            instances.put(uuid, stat);
            return stat;
        }
    }
}
