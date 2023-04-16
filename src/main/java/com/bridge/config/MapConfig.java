package com.bridge.config;

public class MapConfig {
    private Coordinate[] spawns;
    private Coordinate[] beds;

    public Coordinate[] getSpawns() {
        return spawns;
    }

    public void setSpawns(Coordinate[] spawns) {
        this.spawns = spawns;
    }

    public Coordinate[] getBeds() {
        return beds;
    }

    public void setBeds(Coordinate[] beds) {
        this.beds = beds;
    }
}
