package com.bridge;

import com.bridge.config.Coordinate;
import com.bridge.config.MapConfig;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Scanner;

public class Map {

    private static final HashMap<String, Map> instances = new HashMap<String, Map>();

    public File world;
    public MapConfig config;

    private Map(File file) throws IOException {
        if(!file.isDirectory() || !file.canRead()) throw new RuntimeException("Error");
        Scanner scanner = new Scanner(new File(file, "data.json"));
        String data = "";
        if (scanner.hasNext()) {
            data += scanner.nextLine();
        }
        world = new File(file, "world");
        config = Bridge.gson.fromJson(data, MapConfig.class);
    }

    public static Map getMapByName(String name) {
        if (!name.matches("^[\\w|äöüß]{3,32}$")) return null;
        if (instances.containsKey(name)) {
            return instances.get(name);
        }else {
            try {
                Map map = new Map(new File("maps", name));
                instances.put(name, map);
                return map;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Map \""+name+"\" konnte nicht geladen werden.");
                return null;
            }
        }
    }

    public static Map getMapByName(String parent, String name) {
        if (!name.matches("^[\\w|äöüß]{3,32}$")) return null;
        if (instances.containsKey(name)) {
            return instances.get(name);
        }else {
            try {
                Map map = new Map(new File(parent, name));
                instances.put(name, map);
                return map;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Map \""+name+"\" konnte nicht geladen werden.");
                return null;
            }
        }
    }

    public World createWorld() {
        return new WorldCreator(world.getAbsolutePath().replace("home/container/", "")).createWorld();
    }
}