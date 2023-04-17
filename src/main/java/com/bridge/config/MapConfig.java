package com.bridge.config;

public class MapConfig {
    private MapSpawn[] spawns;
    private int maxPlayer;

    public MapSpawn[] getSpawns() {
        return spawns;
    }

    public void setSpawns(MapSpawn[] spawns, int maxPlayer) {
        this.spawns = spawns;
        this.maxPlayer = maxPlayer;
    }

    public void setSpawns(MapSpawn[] spawns) {
        this.spawns = spawns;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }
}
