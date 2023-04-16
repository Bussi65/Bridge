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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class startgame implements CommandExecutor, TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] args) {
        return getValidMapNames();
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (args.length != 1) {
            s.sendMessage("Please enter map name!");
            return true;
        }

        Map map = Map.getMapByName(args[0]);
        if (map == null) {
            s.sendMessage("Map with name: '" + args[0] + "' not found.");
            return true;
        }

        Collection<?> bukkitOnlinePlayer = Bukkit.getOnlinePlayers();
        Player[] onlinePlayer = new Player[bukkitOnlinePlayer.size()];
        for (int i = 0; i < bukkitOnlinePlayer.size(); i++) {
            onlinePlayer[i] = (Player) bukkitOnlinePlayer.toArray()[i];
        }

        GameSession session = new GameSession(map, onlinePlayer);
        return true;
    }

    private ArrayList<String> getValidMapNames() {
        File mapsFolder = new File("maps");
        ArrayList<String> validMapNames = null;
        for (File map : mapsFolder.listFiles()) {
            validMapNames.add(map.getName());
        }
        return validMapNames;
    }
}
