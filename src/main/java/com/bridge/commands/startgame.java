package com.bridge.commands;

import com.bridge.GameSession;
import com.bridge.Map;
import com.bridge.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class startgame implements CommandExecutor, TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] args) {
        return getValidMapNames(args[0]);
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (args.length != 1) {
            s.sendMessage("Please enter map name!");
            return true;
        }

        Map map = Map.getMapByName(args[0]);

        Collection<?> bukkitOnlinePlayer = Bukkit.getOnlinePlayers();
        Player[] onlinePlayer = new Player[bukkitOnlinePlayer.size()];
        //Player sender = null;
        for (int i = 0; i < bukkitOnlinePlayer.size(); i++) {
            //Player p = (Player) bukkitOnlinePlayer.toArray()[i];
            onlinePlayer[i] = (Player) bukkitOnlinePlayer.toArray()[i];
            //if (Objects.equals(p.getName(), s.getName())) sender = p;
        }

        //if (sender == null) return true;
        GameSession session = new GameSession(map, onlinePlayer);
        return true;
    }

    private List<String> getValidMapNames(String name) {
        File mapsFolder = new File("maps");
        List<String> validMapNames = new ArrayList<>();
        for (String map : mapsFolder.list()) {
            if (!map.replace(name, "").equals(map)) validMapNames.add(map);
        }
        return validMapNames;
    }

    public static void copyDirectory(String sourcePathStr, String targetPathStr) throws IOException {
        // GPT code
        Path source = Paths.get(sourcePathStr);
        Path target = Paths.get(targetPathStr);
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static String removeAfterLastChar(String str, char c) {
        int index = str.lastIndexOf(c);
        if (index >= 0) {
            return str.substring(0, index + 1);
        } else {
            return "";
        }
    }


    public static int getSmallestAvailableNumber(String directoryPath) {
        // GPT code
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Path is not a directory");
        }
        File[] subdirectories = directory.listFiles(File::isDirectory);
        int[] numbers = Arrays.stream(subdirectories)
                .mapToInt(subdirectory -> Integer.parseInt(subdirectory.getName()))
                .sorted()
                .toArray();
        int smallestAvailableNumber = 1;
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] > smallestAvailableNumber) {
                return smallestAvailableNumber;
            }
            smallestAvailableNumber++;
        }
        return smallestAvailableNumber;
    }
}
