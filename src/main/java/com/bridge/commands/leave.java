package com.bridge.commands;

import com.bridge.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class leave implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        Player sender = Bukkit.getPlayer(s.getName());
        GameSession.removePlayer(sender.getUniqueId());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] args) {
        return null;
    }
}
