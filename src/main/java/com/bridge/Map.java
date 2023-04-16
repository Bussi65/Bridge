package com.bridge;

import com.bridge.config.Coordinate;
import com.bridge.config.MapConfig;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Map {

    private static final HashMap<String, Map> instances = new HashMap<String, Map>();

    public Clipboard clipboard;
    public Coordinate[] spawns;
    public Coordinate[] beds;

    private Map(File file) throws IOException {
        if(!file.isDirectory() || !file.canRead()) throw new RuntimeException("Error");
        Scanner scanner = new Scanner(file);
        String data = "";
        if (scanner.hasNext()) {
            data += scanner.nextLine();
        }

        MapConfig config = Bridge.gson.fromJson(data, MapConfig.class);
        spawns = config.getSpawns();
        beds = config.getBeds();
    }

    public Map getMapByName(String name) {
        if (!name.matches("^[\\w|äöüß]{3,32}$")) return null;
        if (instances.containsKey(name)) {
            return instances.get(name);
        }else {
            try {
                Map map = new Map(new File(name));
                instances.put(name, map);
                return map;
            } catch (Exception e) {
                System.out.printf("Map \"%s\" konnte nicht geladen werden.%n", name);
                return null;
            }
        }
    }
}

/*
 *
 * spawns:
 *  - asdh
 *
 *
 *
 * */
