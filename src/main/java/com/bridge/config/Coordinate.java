package com.bridge.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Coordinate {
    public int x;
    public int y;
    public int z;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }
}
