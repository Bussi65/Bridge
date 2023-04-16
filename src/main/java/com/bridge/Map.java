package com.bridge;

import com.bridge.config.Coordinate;
import com.bridge.config.MapConfig;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Scanner;

public class Map {

    private static final HashMap<String, Map> instances = new HashMap<String, Map>();

    public Clipboard clipboard;
    public Coordinate[] spawns;
    public Coordinate[] beds;

    private Map(File file) throws IOException, InvalidConfigurationException {
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
                System.out.println("Map \""+name+"\" konnte nicht geladen werden.");
                return null;
            }
        }
        return null;
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
